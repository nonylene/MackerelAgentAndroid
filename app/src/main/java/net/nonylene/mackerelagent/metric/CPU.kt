package net.nonylene.mackerelagent.metric

import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.io.File
import java.util.concurrent.TimeUnit

fun getCPUUsageObservable() : Observable<CPUPercentage> {
    // will be executed 2 SECONDS after initialize
    return Observable.interval(2, TimeUnit.SECONDS).map{getCurrentCPUStat()}
            // initial value will be evaluated immediately
            .scan(null to getCurrentCPUStat(), { beforePair : Pair<CPUPercentage?, CPUStat>, after ->
                val before = beforePair.second
                // merge all diffs
                val userDiff =  after.user - before.user
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
                CPUPercentage(
                        userDiff / allDiff,
                        niceDiff / allDiff,
                        systemDiff / allDiff,
                        idleDiff / allDiff,
                        iowaitDiff / allDiff,
                        irqDiff / allDiff,
                        softirqDiff / allDiff,
                        stealDiff / allDiff,
                        guestDiff / allDiff,
                        guestNiceDiff / allDiff
                ) to after
            }).skip(1).take(4)
            .map {it.first!!}
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
}

private fun getCurrentCPUStat(): CPUStat {
    // remove first element ("cpu")
    val values = File("/proc/stat").readLines().first().split(Regex("\\s+")).drop(1).map(String::toDouble)
    val stat = CPUStat(values[0], values[1], values[2], values[3], values[4], values[5], values[6], values[7], values[8], values[9])
    return stat
}

data class CPUStat(
        val user: Double,
        val nice: Double,
        val system: Double,
        val idle: Double,
        val iowait: Double,
        val irq: Double,
        val softirq: Double,
        val steal: Double,
        val guest: Double,
        val guestNice: Double) {
}

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
        val guestNice: Double) {
}
