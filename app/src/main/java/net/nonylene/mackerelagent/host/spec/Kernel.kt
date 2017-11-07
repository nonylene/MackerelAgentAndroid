package net.nonylene.mackerelagent.host.spec

import android.os.Build
import com.google.gson.annotations.SerializedName
import java.io.File

// https://github.com/mackerelio/mackerel-agent/blob/master/spec/linux/kernel.go

//"name":    {"uname", "-s"},
//"release": {"uname", "-r"},
//"version": {"uname", "-v"},
//"machine": {"uname", "-m"},
//"os":      {"uname", "-o"},

// not send "os" in this application
// (Android older versions does not contain "uname")
// "os" is not appeared in /proc or properties

fun getKernelSpec(): KernelSpec {
    val osType = File("/proc/sys/kernel/ostype").readLines()[0].trim()
    val osRelease = File("/proc/sys/kernel/osrelease").readLines()[0].trim()
    val osVersion = File("/proc/sys/kernel/version").readLines()[0].trim()
    val machineName = System.getProperty("os.arch")
    return KernelSpec(osType, osRelease, osVersion, machineName, "Android", Build.VERSION.RELEASE)
}

data class KernelSpec(
        @SerializedName("name")
        val name: String,
        @SerializedName("release")
        val release: String,
        @SerializedName("version")
        val version: String,
        @SerializedName("machine")
        val machine: String,
        @SerializedName("platform_name")
        val platformName: String,
        @SerializedName("platform_version")
        val platformVersion: String
)