package net.nonylene.mackerelagent.host.spec

import java.io.File
import java.util.*

// https://github.com/mackerelio/mackerel-agent/blob/master/spec/linux/memory.go

// root@vbox86p:/ # cat /proc/meminfo
// MemTotal:        2052600 kB
// MemFree:         1398424 kB
// Buffers:           12720 kB
// Cached:           406236 kB
// SwapCached:            0 kB
// Active:           307400 kB
// Inactive:         286748 kB
// Active(anon):     175204 kB
// Inactive(anon):     4188 kB
// Active(file):     132196 kB
// Inactive(file):   282560 kB
// Unevictable:           0 kB
// Mlocked:               0 kB
// SwapTotal:             0 kB
// SwapFree:              0 kB
// Dirty:                 0 kB
// Writeback:             0 kB
// AnonPages:        175224 kB
// Mapped:           139328 kB
// Shmem:              4220 kB
// Slab:              34508 kB
// SReclaimable:      13196 kB
// SUnreclaim:        21312 kB
// KernelStack:        4352 kB
// PageTables:        13180 kB
// NFS_Unstable:          0 kB
// Bounce:                0 kB
// WritebackTmp:          0 kB
// CommitLimit:     1026300 kB
// Committed_AS:   10643720 kB
// VmallocTotal:   34359738367 kB
// VmallocUsed:       37208 kB
// VmallocChunk:   34359621636 kB
// HugePages_Total:       0
// HugePages_Free:        0
// HugePages_Rsvd:        0
// HugePages_Surp:        0
// Hugepagesize:       2048 kB
// DirectMap4k:       14272 kB
// DirectMap2M:     2082816 kB

fun getMemoryInfo(): MemoryInfo {
    val regex = Regex("(.*):\\s*(\\d+)\\s+kB")
    val map = HashMap<String, Long>()
    File("/proc/meminfo").forEachLine {
        regex.find(it)?.let { result ->
            val values = result.groupValues
            // kB -> * 1024
            map.put(values[1], values[2].toLong() * 1024)
        }
    }

    return MemoryInfo(
            map["MemTotal"], map["MemFree"], map["Buffers"], map["Cached"], map["Active"], map["Inactive"],
            map["HighTotal"], map["HighFree"], map["LowTotal"], map["LowFree"], map["Dirty"],
            map["Writeback"], map["AnonPages"], map["Mapped"], map["Slab"], map["SReclaimable"], map["SUnreclaim"],
            map["PageTables"], map["NFS_Unstable"], map["Bounce"], map["CommitLimit"], map["Committed_AS"],
            map["VmallocTotal"], map["VmallocUsed"], map["VmallocChunk"],
            map["SwapCached"], map["SwapTotal"], map["SwapFree"], map["MemAvailable"]
            )
}


data class MemoryInfo(
        val total: Long?,
        val free: Long?,
        val buffers: Long?,
        val cached: Long?,
        val active: Long?,
        val inactive: Long?,
        val highTotal: Long?,
        val highFree: Long?,
        val lowTotal: Long?,
        val lowFree: Long?,
        val dirty: Long?,
        val writeback: Long?,
        val anonPages: Long?,
        val mapped: Long?,
        val slab: Long?,
        val slabReclaimable: Long?,
        val slabUnreclaim: Long?,
        val pageTables: Long?,
        val nfsUnstable: Long?,
        val bounce: Long?,
        val commitLimit: Long?,
        val committedAs: Long?,
        val vmallocTotal: Long?,
        val vmallocTsed: Long?,
        val vmallocChunk: Long?,
        val swapCached: Long?,
        val swapTotal: Long?,
        val swapFree: Long?,
        val available: Long?
)

