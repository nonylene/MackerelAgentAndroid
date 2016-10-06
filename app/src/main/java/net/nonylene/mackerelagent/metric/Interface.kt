package net.nonylene.mackerelagent.metric

import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.realm.Realm
import io.realm.Sort
import net.nonylene.mackerelagent.realm.RealmInterfaceStats
import java.io.File
import java.util.*
import java.util.concurrent.TimeUnit

fun getInterfaceDeltaObservable(): Observable<List<InterfaceDelta>> {
    // map / doOnNext will be executed 5 SECONDS after initialize
    return Observable.interval(5, TimeUnit.SECONDS).map { getCurrentInterfaceStats() }
            // save result cache to realm
            .doOnNext { origStats ->
                Realm.getDefaultInstance().executeTransactionAsync { realm ->
                    realm.delete(RealmInterfaceStats::class.java)
                    with(realm.createObject(RealmInterfaceStats::class.java)) {
                        origStats.map { createRealmInterfaceStat(it, realm) }.forEach {
                            stats.add(it)
                        }
                        timeStamp = origStats.first().timeStamp
                    }
                }
            }
            // initial value will be evaluated immediately
            .scan(null to getInitialInterfaceStats(), { beforePair: Pair<List<InterfaceDelta>?, List<InterfaceStat>>, after ->
                beforePair.second.mapIndexed { i, interfaceStat ->
                    createInterfaceDelta(interfaceStat, after[i])
                } to after
            }).skip(1)
            // skip first -> nonnull
            .map { it.first!! }
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
}

private fun getCurrentInterfaceStats(): List<InterfaceStat> {
    val time = Date()
    return File("/proc/net/dev").readLines()
            .map { it.split(":").map(String::trim) }
            .filter { it.size == 2 }
            // remove loopback
            .filter { it[0] != "lo" }
            .map {
                val values = it[1].split(Regex("\\s+")).map(String::toDouble)
                InterfaceStat(it[0].trim(),
                        values[0], values[1], values[2], values[3], values[4], values[5], values[6],
                        values[7], values[8], values[9], values[10], values[11], values[12], values[13],
                        values[14], values[15], time
                )
            }
}

/**
 * get (most recent [InterfaceStat] 10 minutes before or later) OR (current [InterfaceStat])
 */
private fun getInitialInterfaceStats(): List<InterfaceStat> {
    val tenMinutesBefore = Calendar.getInstance().apply {
        add(Calendar.MINUTE, -10)
    }

    // recent stat 1.5 minutes before or later
    val recentStats = Realm.getDefaultInstance()
            .where(RealmInterfaceStats::class.java)
            .greaterThanOrEqualTo("timeStamp", tenMinutesBefore.time)
            .findAllSorted("timeStamp", Sort.DESCENDING)
            .firstOrNull()

    return recentStats?.let { it.stats.map(::convertFromRealmInterfaceStat) } ?: getCurrentInterfaceStats()
}

private fun convertFromRealmInterfaceStat(stat: net.nonylene.mackerelagent.realm.RealmInterfaceStat): InterfaceStat {
    return InterfaceStat(stat.name,
            stat.receiveBytes, stat.receivePackets, stat.receiveErrs, stat.receiveDrop,
            stat.receiveFifo, stat.receiveFrame, stat.receiveCompressed, stat.receiveMulticast,
            stat.transmitBytes, stat.transmitPackets, stat.transmitErrs, stat.transmitDrop,
            stat.transmitFifo, stat.transmitColls, stat.transmitCarrier, stat.transmitCompressed,
            stat.timeStamp
    )
}

private fun createInterfaceDelta(before: InterfaceStat, after: InterfaceStat): InterfaceDelta {
    if (before.name != after.name) {
        throw IllegalArgumentException("before stat name (${before.name}) and after stat name (${after.name}) are not the same")
    }
    val receiveDiff = after.receiveBytes - before.receiveBytes
    val transmitDiff = after.transmitBytes - before.transmitBytes
    val secDiff = (after.timeStamp.time - before.timeStamp.time) / 1000
    return InterfaceDelta(before.name, receiveDiff / secDiff, transmitDiff / secDiff)
}

private fun createRealmInterfaceStat(interfaceStat: InterfaceStat, realm: Realm): net.nonylene.mackerelagent.realm.RealmInterfaceStat {
    return realm.createObject(net.nonylene.mackerelagent.realm.RealmInterfaceStat::class.java).apply {
        name = interfaceStat.name
        receiveBytes = interfaceStat.receiveBytes
        receivePackets = interfaceStat.receivePackets
        receiveErrs = interfaceStat.receiveErrs
        receiveDrop = interfaceStat.receiveDrop
        receiveFifo = interfaceStat.receiveFifo
        receiveFrame = interfaceStat.receiveFrame
        receiveCompressed = interfaceStat.receiveCompressed
        receiveMulticast = interfaceStat.receiveMulticast
        transmitBytes = interfaceStat.transmitBytes
        transmitPackets = interfaceStat.transmitPackets
        transmitErrs = interfaceStat.transmitErrs
        transmitDrop = interfaceStat.transmitDrop
        transmitFifo = interfaceStat.transmitFifo
        transmitColls = interfaceStat.transmitColls
        transmitCarrier = interfaceStat.transmitCarrier
        transmitCompressed = interfaceStat.transmitCompressed
        timeStamp = interfaceStat.timeStamp
    }
}

private data class InterfaceStat(
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
data class InterfaceDelta(
        val name: String,
        val receiveBytes: Double,
        val transmitPackets: Double
)
