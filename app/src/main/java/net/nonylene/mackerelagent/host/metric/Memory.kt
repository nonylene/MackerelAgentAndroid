package net.nonylene.mackerelagent.host.metric

import java.io.File
import java.util.*

fun getMemoryInfo(): MemoryInfo {
    val regex = Regex("(.*):\\s*(\\d+)\\s+kB")
    val hash = HashMap<String, Long>()
    File("/proc/meminfo").forEachLine {
        regex.find(it)?.let { result ->
            val values = result.groupValues
            hash.put(values[1], values[2].toLong())
        }
    }

    val free = hash["MemFree"]!! * 1024
    val total = hash["MemTotal"]!! * 1024
    val cached = hash["Cached"]!! * 1024
    val buffers = hash["Buffers"]!! * 1024
    val used = total - free - cached - buffers
    return MemoryInfo(free, buffers, cached, used, total,
            hash["SwapFree"]!! * 1024, hash["SwapCached"]!! * 1024, hash["Active"]!! * 1024,
            hash["Inactive"]!! * 1024, hash["MemAvailable"]?.let { it * 1024 },
            Date()
    )
}

//"MemTotal":     "total",
//"MemFree":      "free",
//"MemAvailable": "available",
//"Buffers":      "buffers",
//"Cached":       "cached",
//"Active":       "active",
//"Inactive":     "inactive",
//"SwapCached":   "swap_cached",
//"SwapTotal":    "swap_total",
//"SwapFree":     "swap_free",

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
