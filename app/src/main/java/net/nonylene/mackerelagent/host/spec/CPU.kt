package net.nonylene.mackerelagent.host.spec

import com.google.gson.annotations.SerializedName
import net.nonylene.mackerelagent.utils.splitBy
import java.io.File

//case "processor":
//cur = make(map[string]interface{})
//if modelName != "" {
//    cur["model_name"] = modelName
//}
// results = append(results, cur)
// case "Processor":
// modelName = val
// case "vendor_id", "model", "stepping", "physical id", "core id", "model name", "cache size":
// cur[strings.Replace(key, " ", "_", -1)] = val
// case "cpu family":
//cur["family"] = val
//case "cpu cores":
//cur["cores"] = val
//case "cpu MHz":
//cur["mhz"] = val

// https://github.com/mackerelio/mackerel-agent/blob/master/spec/linux/cpu.go

fun getCPUSpec(): List<CPUCoreSpec> {
    // ignore duplicate (core)
    return File("/proc/cpuinfo").readLines().filter {
        it.contains(':')
    }.map {
        it.split(":").map(String::trim)
    }.splitBy {
        it.first() == "processor"
        // remove first element
    }.drop(1).map {
        val map = it.map { it[0] to it[1] }.toMap()
        CPUCoreSpec(
                map["vendor_id"],
                map["model"],
                map["stepping"],
                map["physical id"],
                map["core id"],
                map["model name"],
                map["cache size"],
                map["cpu family"],
                map["cpu cores"],
                map["cpu MHz"]
        )
    }
}

data class CPUCoreSpec(
        @SerializedName("vendor_id")
        val vendorId: String?,
        @SerializedName("model")
        val model: String?,
        @SerializedName("stepping")
        val stepping: String?,
        @SerializedName("physical_id")
        val physicalId: String?,
        @SerializedName("core_id")
        val coreId: String?,
        @SerializedName("model_name")
        val modelName: String?,
        @SerializedName("cache_size")
        val cacheSize: String?,
        @SerializedName("family")
        val cpuFamily: String?,
        @SerializedName("cores")
        val cpuCores: String?,
        @SerializedName("")
        val cpuMHz: String?
)