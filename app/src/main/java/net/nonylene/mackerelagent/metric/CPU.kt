package net.nonylene.mackerelagent.metric

import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.realm.Realm
import io.realm.Sort
import net.nonylene.mackerelagent.realm.RealmCPUStat
import java.io.File
import java.util.*
import java.util.concurrent.TimeUnit

fun getCPUPercentageObservable(): Observable<CPUPercentage> {
    // map / doOnNext will be executed 2 SECONDS after initialize
    return Observable.interval(2, TimeUnit.SECONDS).map { getCurrentCPUStat() }
            // save result cache to realm
            .doOnNext { stat ->
                Realm.getDefaultInstance().executeTransactionAsync { realm ->
                    realm.delete(RealmCPUStat::class.java)
                    realm.createObject(RealmCPUStat::class.java).fromCPUStat(stat)
                }
            }
            // initial value will be evaluated immediately
            .scan(null to getInitialCPUStat(), { beforePair: Pair<CPUPercentage?, CPUStat>, after ->
                createCPUPercentage(beforePair.second, after) to after
            }).skip(1)
            // skip first -> nonnull
            .map { it.first!! }
            .subscribeOn(Schedulers.newThread())
}

private fun getCurrentCPUStat(): CPUStat {
    // remove first element ("cpu")
    val values = File("/proc/stat").readLines().first().split(Regex("\\s+")).drop(1).map(String::toDouble)
    val stat = CPUStat(values[0], values[1], values[2], values[3],
            values[4], values[5], values[6], values[7], values[8], values[9])
    return stat
}

/**
 * get (most recent [CPUStat] 1.5 minutes before or later) OR (current [CPUStat])
 */
private fun getInitialCPUStat(): CPUStat {
    val oneAndHalfMinutesBefore = Calendar.getInstance().apply {
        add(Calendar.SECOND, -90)
    }

    // recent stat 1.5 minutes before or later
    Realm.getDefaultInstance().use {
        val recentStat = it.where(RealmCPUStat::class.java)
                .greaterThanOrEqualTo("timeStamp", oneAndHalfMinutesBefore.time)
                .findAllSorted("timeStamp", Sort.DESCENDING)
                .firstOrNull()
        return recentStat?.let(::convertFromRealmCPUStat) ?: getCurrentCPUStat()
    }
}

private fun convertFromRealmCPUStat(stat: RealmCPUStat): CPUStat {
    return CPUStat(stat.user, stat.nice, stat.system, stat.idle, stat.iowait,
            stat.irq, stat.softirq, stat.steal, stat.guest, stat.guestNice)
}

private fun createCPUPercentage(before: CPUStat, after: CPUStat): CPUPercentage {
    // merge all diffs
    val userDiff = after.user - before.user
    val niceDiff = after.nice - before.nice
    val systemDiff = after.system - before.system
    val idleDiff = after.idle - before.idle
    val iowaitDiff = after.iowait - before.iowait
    val irqDiff = after.irq - before.irq
    val softirqDiff = after.softirq - before.softirq
    val stealDiff = after.steal - before.steal
    val guestDiff = after.guest - before.guest
    val guestNiceDiff = after.guestNice - before.guestNice

    val allDiff = userDiff + niceDiff + systemDiff + idleDiff + iowaitDiff + irqDiff + softirqDiff + stealDiff + guestDiff + guestNiceDiff
    return CPUPercentage(
            userDiff / allDiff * 100,
            niceDiff / allDiff * 100,
            systemDiff / allDiff * 100,
            idleDiff / allDiff * 100,
            iowaitDiff / allDiff * 100,
            irqDiff / allDiff * 100,
            softirqDiff / allDiff * 100,
            stealDiff / allDiff * 100,
            guestDiff / allDiff * 100,
            guestNiceDiff / allDiff * 100
    )
}

private fun RealmCPUStat.fromCPUStat(cpuStat: CPUStat) {
    user = cpuStat.user
    nice = cpuStat.nice
    system = cpuStat.system
    idle = cpuStat.idle
    iowait = cpuStat.iowait
    irq = cpuStat.irq
    softirq = cpuStat.softirq
    steal = cpuStat.steal
    guest = cpuStat.guest
    guestNice = cpuStat.guestNice
    timeStamp = Date()
}

private data class CPUStat(
        val user: Double,
        val nice: Double,
        val system: Double,
        val idle: Double,
        val iowait: Double,
        val irq: Double,
        val softirq: Double,
        val steal: Double,
        val guest: Double,
        val guestNice: Double
)

// metrics: https://github.com/mackerelio/mackerel-agent/blob/master/metrics/linux/cpuusage.go

data class CPUPercentage(
        val user: Double,
        val nice: Double,
        val system: Double,
        val idle: Double,
        val iowait: Double,
        val irq: Double,
        val softirq: Double,
        val steal: Double,
        val guest: Double,
        // unused now
        val guestNice: Double
)
