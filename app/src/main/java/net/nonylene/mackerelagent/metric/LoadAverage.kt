package net.nonylene.mackerelagent.metric

import java.io.File

fun getLoadAverage5min(): String {
    return File("/proc/loadavg").readText().split(" ")[1]
}