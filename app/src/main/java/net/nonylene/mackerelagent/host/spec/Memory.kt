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
    val regex = Regex("(.*):\\s*(\\d+)\\s+(.+)")
    val map = HashMap<String, String>()
    File("/proc/meminfo").forEachLine {
        regex.find(it)?.let { result ->
            val values = result.groupValues
            // kB -> * 1024
            map.put(values[1], values[2] + values[3])
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
        val total: String?,
        @SerializedName("free")
        val free: String?,
        @SerializedName("buffers")
        val buffers: String?,
        @SerializedName("cached")
        val cached: String?,
        @SerializedName("active")
        val active: String?,
        @SerializedName("inactive")
        val inactive: String?,
        @SerializedName("high_total")
        val highTotal: String?,
        @SerializedName("high_free")
        val highFree: String?,
        @SerializedName("low_total")
        val lowTotal: String?,
        @SerializedName("low_free")
        val lowFree: String?,
        @SerializedName("dirty")
        val dirty: String?,
        @SerializedName("writeback")
        val writeback: String?,
        @SerializedName("anon_pages")
        val anonPages: String?,
        @SerializedName("mapped")
        val mapped: String?,
        @SerializedName("slab")
        val slab: String?,
        @SerializedName("slab_reclaimable")
        val slabReclaimable: String?,
        @SerializedName("slab_unreclaim")
        val slabUnreclaim: String?,
        @SerializedName("page_tables")
        val pageTables: String?,
        @SerializedName("nfs_unstable")
        val nfsUnstable: String?,
        @SerializedName("bounce")
        val bounce: String?,
        @SerializedName("commit_limit")
        val commitLimit: String?,
        @SerializedName("committed_as")
        val committedAs: String?,
        @SerializedName("vmalloc_total")
        val vmallocTotal: String?,
        @SerializedName("vmalloc_used")
        val vmallocTsed: String?,
        @SerializedName("vmalloc_chunk")
        val vmallocChunk: String?,
        @SerializedName("swap_cached")
        val swapCached: String?,
        @SerializedName("swap_total")
        val swapTotal: String?,
        @SerializedName("swap_free")
        val swapFree: String?,
        @Exclude
        val available: String?
)
