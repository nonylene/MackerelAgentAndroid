package net.nonylene.mackerelagent.metric

import java.io.File
import java.util.*

fun getMemoryInfo(): MemoryInfo {
    val regex = Regex("(.*):\\s*(\\d+)\\skB")
    val hash = HashMap<String, Long>()
    File("/proc/meminfo").forEachLine {
        regex.find(it)?.let { result ->
            val values = result.groupValues
            hash.put(values[1], values[2].toLong())
        }
    }

    val free = hash["MemFree"]!!
    val total = hash["MemTotal"]!!
    val cached = hash["Cached"]!!
    val buffers = hash["Cached"]!!
    val used = total - free - cached - buffers
    println(hash)
    return MemoryInfo(free, buffers, cached, used, total, hash["SwapFree"]!!, hash["SwapCached"]!!
            , hash["Active"]!!, hash["Inactive"]!!, hash["MemAvailable"])
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
