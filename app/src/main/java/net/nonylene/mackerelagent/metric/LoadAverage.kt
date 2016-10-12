package net.nonylene.mackerelagent.metric

import java.io.File
import java.util.*

fun getLoadAverage5min(): LoadAverage {
    return LoadAverage(File("/proc/loadavg").readText().split(" ")[1], Date())
}

@Suppress("unused")
class LoadAverage(
        @MetricVariable("loadavg5")
        val loadavg5: String,
        timeStamp: Date
): MetricsContainer.Default(null, null, timeStamp)
