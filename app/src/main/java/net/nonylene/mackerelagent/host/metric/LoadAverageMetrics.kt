package net.nonylene.mackerelagent.host.metric

import java.io.File
import java.util.*

fun getLoadAverageMetrics(): LoadAverageMetrics {
    return LoadAverageMetrics(File("/proc/loadavg").readText().split(" ")[1], Date())
}

@Suppress("unused")
class LoadAverageMetrics(
        @MetricVariable("loadavg5")
        val loadavg5: String,
        timeStamp: Date
): MetricsContainer.Default(null, null, timeStamp)
