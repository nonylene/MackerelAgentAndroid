package net.nonylene.mackerelagent.host.metric

import java.io.File
import java.util.*

fun getLoadAverageMetrics(): LoadAverageMetrics {
    return LoadAverageMetrics(File("/proc/loadavg").readLines()[0].split(" ")[1].toDouble(), Date())
}

@Suppress("unused")
class LoadAverageMetrics(
        @MetricVariable("loadavg5")
        val loadavg5: Double,
        timeStamp: Date
): MetricsContainer.Default(null, null, timeStamp)
