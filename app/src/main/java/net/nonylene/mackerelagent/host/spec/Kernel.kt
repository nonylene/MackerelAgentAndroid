package net.nonylene.mackerelagent.host.spec

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
    val osType = File("/proc/sys/kernel/ostype").readText().trim()
    val osRelease = File("/proc/sys/kernel/osrelease").readText().trim()
    val osVersion = File("/proc/sys/kernel/version").readText().trim()
    val machineName = System.getProperty("os.arch")
    return KernelSpec(osType, osRelease, osVersion, machineName)
}

data class KernelSpec(
        val name: String,
        val release: String,
        val version: String,
        val machine: String
)