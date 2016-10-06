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

data class MemoryInfo(
        val free: Long,
        val buffers: Long,
        val cached: Long,
        val used: Long,
        val total: Long,
        val swapFree: Long,
        val swapCached: Long,
        // additional metrics (https://github.com/mackerelio/mackerel-agent/blob/master/metrics/linux/memory.go)
        val active: Long,
        val inactive: Long,
        // after linux kernel version 3.14 (KitKat: 4.4)
        val available: Long?
)
