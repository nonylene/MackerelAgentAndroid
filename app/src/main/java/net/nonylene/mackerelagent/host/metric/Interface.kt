package net.nonylene.mackerelagent.host.metric

import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import io.realm.Realm
import io.realm.Sort
import net.nonylene.mackerelagent.realm.RealmInterfaceStat
import net.nonylene.mackerelagent.realm.RealmInterfaceStats
import net.nonylene.mackerelagent.realm.createRealmInterfaceStats
import java.io.File
import java.util.*
import java.util.concurrent.TimeUnit

fun getInterfaceMetricsListObservable(): Observable<List<InterfaceDeltaMetrics>> {
    // map / doOnNext will be executed 5 SECONDS after initialize
    return Observable.interval(5, TimeUnit.SECONDS).map { getCurrentInterfaceStats() }
            // save result cache to realm
            .doOnNext { stats ->
                Realm.getDefaultInstance().use {
                    it.executeTransactionAsync { realm ->
                        realm.delete(RealmInterfaceStats::class.java)
                        realm.delete(RealmInterfaceStat::class.java)
                        realm.createRealmInterfaceStats(stats)
                    }
                }
            }
            // initial value will be evaluated immediately
            // use lambda to get latest realm result after retry
            .scanWith({ null to getInitialInterfaceStats() }, { beforePair: Pair<List<InterfaceDeltaMetrics>?, List<InterfaceStat>>, after ->
                val beforeList = beforePair.second
                after.mapNotNull { afterStat ->
                    beforeList.find { it.name == afterStat.name }?.let { beforeStat ->
                        createInterfaceMetrics(beforeStat, afterStat)
                    }
                } to after
            }).skip(1)
            // skip first -> nonnull
            .map { it.first!! }
            .subscribeOn(Schedulers.io())
}

private fun getCurrentInterfaceStats(): List<InterfaceStat> {
    val time = Date()
    return File("/proc/net/dev").readLines()
            .map { it.split(":").map(String::trim) }
            .filter { it.size == 2 }
            // remove loopback
            .filter { it[0] != "lo" }
            .mapNotNull {
                val values = it[1].split(Regex("\\s+")).map(String::toDouble)
                // all zero -> remove data
                if (values.all { it == 0.0 }) return@mapNotNull null
                InterfaceStat(it[0].trim(),
                        values[0], values[1], values[2], values[3], values[4], values[5], values[6],
                        values[7], values[8], values[9], values[10], values[11], values[12], values[13],
                        values[14], values[15], time
                )
            }
}

/**
 * get (most recent [InterfaceStat] 5 minutes before or later) OR (current [InterfaceStat])
 */
private fun getInitialInterfaceStats(): List<InterfaceStat> {
    val fiveMinutesBefore = Calendar.getInstance().apply {
        add(Calendar.MINUTE, -5)
    }

    // recent stat 5 minutes before or later
    Realm.getDefaultInstance().use {
        val recentStats = it.where(RealmInterfaceStats::class.java)
                .greaterThanOrEqualTo("timeStamp", fiveMinutesBefore.time)
                .findAllSorted("timeStamp", Sort.DESCENDING)
                .firstOrNull()
        println("stat:" + recentStats)
        return recentStats?.let { it.stats.map(RealmInterfaceStat::createInterfaceStat) } ?: getCurrentInterfaceStats()
    }
}

private fun createInterfaceMetrics(before: InterfaceStat, after: InterfaceStat): InterfaceDeltaMetrics {
    if (before.name != after.name) {
        throw IllegalArgumentException("before stat name (${before.name}) and after stat name (${after.name}) are not the same")
    }
    val receiveDiff = after.receiveBytes - before.receiveBytes
    val transmitDiff = after.transmitBytes - before.transmitBytes
    val secDiff = (after.timeStamp.time - before.timeStamp.time) / 1000
    return InterfaceDeltaMetrics(receiveDiff / secDiff, transmitDiff / secDiff, after.name, after.timeStamp)
}

data class InterfaceStat(
        val name: String,
        val receiveBytes: Double,
        val receivePackets: Double,
        val receiveErrs: Double,
        val receiveDrop: Double,
        val receiveFifo: Double,
        val receiveFrame: Double,
        val receiveCompressed: Double,
        val receiveMulticast: Double,
        val transmitBytes: Double,
        val transmitPackets: Double,
        val transmitErrs: Double,
        val transmitDrop: Double,
        val transmitFifo: Double,
        val transmitColls: Double,
        val transmitCarrier: Double,
        val transmitCompressed: Double,
        val timeStamp: Date
)

// https://github.com/mackerelio/mackerel-agent/blob/master/metrics/linux/interface.go
@Suppress("unused")
class InterfaceDeltaMetrics(
        @MetricVariable("rxBytes.delta")
        val receiveBytes: Double,
        @MetricVariable("txBytes.delta")
        val transmitPackets: Double,
        name: String,
        timeStamp: Date
) : MetricsContainer.Default("interface", name, timeStamp)
