package net.nonylene.mackerelagent.host.metric

import java.io.File
import java.util.*

fun getMemoryInfo(): MemoryInfo {
    val regex = Regex("(.*):\\s*(\\d+)\\s+kB")
    val map = HashMap<String, Long>()
    File("/proc/meminfo").forEachLine {
        regex.find(it)?.let { result ->
            val values = result.groupValues
            map.put(values[1], values[2].toLong())
        }
    }

    val free = map["MemFree"]!! * 1024
    val total = map["MemTotal"]!! * 1024
    val cached = map["Cached"]!! * 1024
    val buffers = map["Buffers"]!! * 1024
    val used = total - free - cached - buffers
    return MemoryInfo(free, buffers, cached, used, total,
            map["SwapFree"]!! * 1024, map["SwapCached"]!! * 1024, map["Active"]!! * 1024,
            map["Inactive"]!! * 1024, map["MemAvailable"]?.let { it * 1024 },
            Date()
    )
}

// "MemTotal":     "total",
// "MemFree":      "free",
// "MemAvailable": "available",
// "Buffers":      "buffers",
// "Cached":       "cached",
// "Active":       "active",
// "Inactive":     "inactive",
// "SwapCached":   "swap_cached",
// "SwapTotal":    "swap_total",
// "SwapFree":     "swap_free",

@Suppress("unused")
class MemoryInfo(
        @MetricVariable("free")
        val free: Long,
        @MetricVariable("buffers")
        val buffers: Long,
        @MetricVariable("cached")
        val cached: Long,
        @MetricVariable("used")
        val used: Long,
        @MetricVariable("total")
        val total: Long,
        @MetricVariable("swap_free")
        val swapFree: Long,
        @MetricVariable("swap_cached")
        val swapCached: Long,
        // additional metrics (https://github.com/mackerelio/mackerel-agent/blob/master/metrics/linux/memory.go)
        @MetricVariable("active")
        val active: Long,
        @MetricVariable("inactive")
        val inactive: Long,
        // after linux kernel version 3.14 (KitKat: 4.4)
        @MetricVariable("available")
        val available: Long?,
        timeStamp: Date
) : MetricsContainer.Default("memory", null, timeStamp)
