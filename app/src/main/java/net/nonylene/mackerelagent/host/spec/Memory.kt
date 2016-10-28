package net.nonylene.mackerelagent.host.spec

import com.google.gson.annotations.SerializedName
import net.nonylene.mackerelagent.network.Exclude
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

fun getMemorySpec(): MemorySpec {
    val regex = Regex("(.*):\\s*(\\d+)\\s+kB")
    val map = HashMap<String, Long>()
    File("/proc/meminfo").forEachLine {
        regex.find(it)?.let { result ->
            val values = result.groupValues
            // kB -> * 1024
            map.put(values[1], values[2].toLong() * 1024)
        }
    }

    return MemorySpec(
            map["MemTotal"], map["MemFree"], map["Buffers"], map["Cached"], map["Active"], map["Inactive"],
            map["HighTotal"], map["HighFree"], map["LowTotal"], map["LowFree"], map["Dirty"],
            map["Writeback"], map["AnonPages"], map["Mapped"], map["Slab"], map["SReclaimable"], map["SUnreclaim"],
            map["PageTables"], map["NFS_Unstable"], map["Bounce"], map["CommitLimit"], map["Committed_AS"],
            map["VmallocTotal"], map["VmallocUsed"], map["VmallocChunk"],
            map["SwapCached"], map["SwapTotal"], map["SwapFree"], map["MemAvailable"]
            )
}


data class MemorySpec(
        @SerializedName("total")
        val total: Long?,
        @SerializedName("free")
        val free: Long?,
        @SerializedName("buffers")
        val buffers: Long?,
        @SerializedName("cached")
        val cached: Long?,
        @SerializedName("active")
        val active: Long?,
        @SerializedName("inactive")
        val inactive: Long?,
        @SerializedName("high_total")
        val highTotal: Long?,
        @SerializedName("high_free")
        val highFree: Long?,
        @SerializedName("low_total")
        val lowTotal: Long?,
        @SerializedName("low_free")
        val lowFree: Long?,
        @SerializedName("dirty")
        val dirty: Long?,
        @SerializedName("writeback")
        val writeback: Long?,
        @SerializedName("anon_pages")
        val anonPages: Long?,
        @SerializedName("mapped")
        val mapped: Long?,
        @SerializedName("slab")
        val slab: Long?,
        @SerializedName("slab_reclaimable")
        val slabReclaimable: Long?,
        @SerializedName("slab_unreclaim")
        val slabUnreclaim: Long?,
        @SerializedName("page_tables")
        val pageTables: Long?,
        @SerializedName("nfs_unstable")
        val nfsUnstable: Long?,
        @SerializedName("bounce")
        val bounce: Long?,
        @SerializedName("commit_limit")
        val commitLimit: Long?,
        @SerializedName("committed_as")
        val committedAs: Long?,
        @SerializedName("vmalloc_total")
        val vmallocTotal: Long?,
        @SerializedName("vmalloc_used")
        val vmallocTsed: Long?,
        @SerializedName("vmalloc_chunk")
        val vmallocChunk: Long?,
        @SerializedName("swap_cached")
        val swapCached: Long?,
        @SerializedName("swap_total")
        val swapTotal: Long?,
        @SerializedName("swap_free")
        val swapFree: Long?,
        @Exclude
        val available: Long?
)
