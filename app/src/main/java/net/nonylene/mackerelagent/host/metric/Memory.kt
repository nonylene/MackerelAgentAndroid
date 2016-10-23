package net.nonylene.mackerelagent.host.metric

import net.nonylene.mackerelagent.host.spec.getMemoryInfo
import java.util.*

fun getMemoryMetrics(): MemoryMetrics {
    val info = getMemoryInfo()

    val used = info.total!! - info.free!! - info.cached!! - info.buffers!!
    return MemoryMetrics(info.free, info.buffers, info.cached, used, info.total,
            info.swapFree!!, info.swapCached!!, info.active!!, info.inactive!!, info.available,
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
