package net.nonylene.mackerelagent.host.spec

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
        val vendorId: String?,
        val model: String?,
        val stepping: String?,
        val physicalId: String?,
        val coreId: String?,
        val modelName: String?,
        val cacheSize: String?,
        val cpuFamily: String?,
        val cpuCores: String?,
        val cpuMHz: String?
)