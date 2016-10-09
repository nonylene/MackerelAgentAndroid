package net.nonylene.mackerelagent.metric

import java.io.File
import java.util.*

fun getLoadAverage5min(): String {
    return File("/proc/loadavg").readText().split(" ")[1]
}

class LoadAverage {

}
