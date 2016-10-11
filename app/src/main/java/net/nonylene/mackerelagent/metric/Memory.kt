package net.nonylene.mackerelagent.metric

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

    val free = hash["MemFree"]!! * 1000
    val total = hash["MemTotal"]!! * 1000
    val cached = hash["Cached"]!! * 1000
    val buffers = hash["Cached"]!! * 1000
    val used = total - free - cached - buffers
    return MemoryInfo(free, buffers, cached, used, total,
            hash["SwapFree"]!! * 1000, hash["SwapCached"]!! * 1000, hash["Active"]!! * 1000,
            hash["Inactive"]!! * 1000, hash["MemAvailable"]?.let { it * 1000 }
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
@MetricPrefix("memory")
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
        val available: Long?
) : Metric.DefaultMetric()
