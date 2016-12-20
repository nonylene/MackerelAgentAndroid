package net.nonylene.mackerelagent.host.metric

import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import java.io.File
import java.util.*
import java.util.concurrent.TimeUnit

fun getDiskMetricsListObservable(): Observable<List<DiskDeltaMetrics>> {
    // map / doOnNext will be executed 5 SECONDS after initialize
    return Observable.interval(5, TimeUnit.SECONDS).map { getCurrentDiskStats() }
            // initial value will be evaluated immediately
            // use lambda to get latest result after retry
            .scanWith({ null to getCurrentDiskStats() }, { beforePair: Pair<List<DiskDeltaMetrics>?, List<DiskStat>>, after ->
                val beforeList = beforePair.second
                after.mapNotNull { afterStat ->
                    beforeList.find { it.name == afterStat.name }?.let { beforeStat ->
                        createDiskDeltaMetrics(beforeStat, afterStat)
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

private fun createDiskDeltaMetrics(before: DiskStat, after: DiskStat): DiskDeltaMetrics {
    if (before.name != after.name) {
        throw IllegalArgumentException("before stat name (${before.name}) and after stat name (${after.name}) are not the same")
    }
    val readsDiff = after.reads - before.reads
    val writesDiff = after.writes - before.writes
    val secDiff = (after.timeStamp.time - before.timeStamp.time) / 1000
    return DiskDeltaMetrics(readsDiff / secDiff, writesDiff / secDiff, after.name, after.timeStamp)
}

data class DiskStat(
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
@Suppress("unused")
class DiskDeltaMetrics(
        @MetricVariable("reads.delta")
        val reads: Double,
        @MetricVariable("writes.delta")
        val writes: Double,
        name: String,
        timeStamp: Date
) : MetricsContainer.Default("disk", name, timeStamp)
