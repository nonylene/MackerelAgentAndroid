package net.nonylene.mackerelagent.host.metric

import net.nonylene.mackerelagent.host.common.getCommonFileSystemStats
import java.util.*

fun getFileSystemStates(): List<FileSystemState> {
        val date = Date()
        return getCommonFileSystemStats().filter {
            it.name.contains("/dev/")
        }.map {
               FileSystemState(
                       it.kbSize * 1024,
                       it.kbUsed * 1024,
                       it.name.removePrefix("/dev/"),
                       date
               )
        }
}

@Suppress("unused")
class FileSystemState(
        @MetricVariable("size")
        val size: Long,
        @MetricVariable("used")
        val used: Long,
        name: String,
        timeStamp: Date
) : MetricsContainer.Default("filesystem", name, timeStamp)
