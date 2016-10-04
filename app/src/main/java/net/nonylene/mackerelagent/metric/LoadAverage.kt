package net.nonylene.mackerelagent.metric

import java.io.File

fun getLoadAverage(): String {
    return File("/proc/loadavg").readText().split(" ").first()
}