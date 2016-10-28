package net.nonylene.mackerelagent.host.metric

import java.io.File
import java.util.*

fun getMemoryMetrics(): MemoryMetrics {
    val regex = Regex("(.*):\\s*(\\d+)\\s+kB")
    val map = HashMap<String, Long>()
    File("/proc/meminfo").forEachLine {
        regex.find(it)?.let { result ->
            val values = result.groupValues
            // kB -> * 1024
            map.put(values[1], values[2].toLong() * 1024)
        }
    }

    val total = map["MemTotal"]!!
    val free = map["MemFree"]!!
    val cached = map["Cached"]!!
    val buffers = map["Buffers"]!!
    val used = total - free - cached - buffers
    return MemoryMetrics(free, buffers, cached, used, total,
            map["SwapCached"]!!, map["SwapTotal"]!!, map["SwapFree"]!!,
            map["Active"]!!, map["Inactive"]!!, map["MemAvailable"],
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
class MemoryMetrics(
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
        @MetricVariable("swap_cached")
        val swapCached: Long,
        @MetricVariable("swap_total")
        val swapTotal: Long,
        @MetricVariable("swap_free")
        val swapFree: Long,
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
