package net.nonylene.mackerelagent.metric

import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.realm.Realm
import io.realm.Sort
import net.nonylene.mackerelagent.realm.RealmDiskStat
import net.nonylene.mackerelagent.realm.RealmDiskStats
import java.io.File
import java.util.*
import java.util.concurrent.TimeUnit

fun getDiskDeltaObservable(): Observable<List<DiskDelta>> {
    // map / doOnNext will be executed 5 SECONDS after initialize
    return Observable.interval(5, TimeUnit.SECONDS).map { getCurrentDiskStats() }
            // save result cache to realm
            .doOnNext { origStats ->
                Realm.getDefaultInstance().executeTransactionAsync { realm ->
                    realm.delete(RealmDiskStats::class.java)
                    with(realm.createObject(RealmDiskStats::class.java)) {
                        origStats.map { createRealmDiskStat(it, realm) }.forEach {
                            stats.add(it)
                        }
                        timeStamp = origStats.first().timeStamp
                    }
                }
            }
            // initial value will be evaluated immediately
            // use lambda to get latest realm result after retry
            .scanWith({ null to getInitialDiskStats() }, { beforePair: Pair<List<DiskDelta>?, List<DiskStat>>, after ->
                val beforeList = beforePair.second
                after.mapNotNull { afterStat ->
                    beforeList.find { it.name == afterStat.name }?.let { beforeStat ->
                        createDiskDelta(beforeStat, afterStat)
                    }
                } to after
            }).skip(1)
            // skip first -> nonnull
            .map { it.first!! }
            .subscribeOn(Schedulers.io())
}

private fun getCurrentDiskStats(): List<DiskStat> {
    val time = Date()
    return File("/proc/diskstats").readLines()
            .map(String::trim)
            .mapNotNull {
                val cols = it.split(Regex("\\s+")).drop(2).map(String::trim)
                val name = cols[0]
                val values = cols.drop(1).map(String::toDouble)
                // all zero -> remove data
                if (values.all { it == 0.0 }) return@mapNotNull null
                DiskStat(name,
                        values[0], values[1], values[2], values[3], values[4], values[5], values[6],
                        values[7], values[8], values[9], values[10], time
                )
            }
}

/**
 * get (most recent [DiskStat] 1.5 minutes before or later) OR (current [DiskStat])
 */
private fun getInitialDiskStats(): List<DiskStat> {
    val oneAndHalfMinutesBefore = Calendar.getInstance().apply {
        add(Calendar.SECOND, -90)
    }

    // recent stat 1.5 minutes before or later
    val recentStats = Realm.getDefaultInstance()
            .where(RealmDiskStats::class.java)
            .greaterThanOrEqualTo("timeStamp", oneAndHalfMinutesBefore.time)
            .findAllSorted("timeStamp", Sort.DESCENDING)
            .firstOrNull()

    return recentStats?.let { it.stats.map(::convertFromRealmDiskStat) } ?: getCurrentDiskStats()
}

private fun convertFromRealmDiskStat(stat: RealmDiskStat): DiskStat {
    return DiskStat(stat.name,
            stat.reads, stat.readsMerged, stat.sectorsRead, stat.readTime,
            stat.writes, stat.writesMerged, stat.sectorsWritten, stat.writeTime,
            stat.ioInProgress, stat.ioTime, stat.ioTimeWeighted, stat.timeStamp
    )
}

private fun createDiskDelta(before: DiskStat, after: DiskStat): DiskDelta {
    if (before.name != after.name) {
        throw IllegalArgumentException("before stat name (${before.name}) and after stat name (${after.name}) are not the same")
    }
    val readsDiff = after.reads - before.reads
    val writesDiff = after.writes - before.writes
    val secDiff = (after.timeStamp.time - before.timeStamp.time) / 1000
    return DiskDelta(before.name, readsDiff / secDiff, writesDiff / secDiff)
}

private fun createRealmDiskStat(diskStat: DiskStat, realm: Realm): RealmDiskStat {
    return realm.createObject(RealmDiskStat::class.java).apply {
        name = diskStat.name
        reads = diskStat.reads
        readsMerged = diskStat.readsMerged
        sectorsRead = diskStat.sectorsRead
        readTime = diskStat.readTime
        writes = diskStat.writes
        writesMerged = diskStat.writesMerged
        sectorsWritten = diskStat.sectorsWritten
        writeTime = diskStat.writeTime
        ioInProgress = diskStat.ioInProgress
        ioTime = diskStat.ioTime
        ioTimeWeighted = diskStat.ioTimeWeighted
        timeStamp = diskStat.timeStamp
    }
}

private data class DiskStat(
        val name: String,
        val reads: Double,
        val readsMerged: Double,
        val sectorsRead: Double,
        val readTime: Double,
        val writes: Double,
        val writesMerged: Double,
        val sectorsWritten: Double,
        val writeTime: Double,
        val ioInProgress: Double,
        val ioTime: Double,
        val ioTimeWeighted: Double,
        val timeStamp: Date
)

// https://github.com/mackerelio/mackerel-agent/blob/master/metrics/linux/disk.go
@MetricPrefix("disk")
data class DiskDelta(
        @MetricName
        val name: String,
        @MetricVariable("reads.delta")
        val reads: Double,
        @MetricVariable("writes.delta")
        val writes: Double
) : DefaultMetric
