package net.nonylene.mackerelagent.host.metric

import net.nonylene.mackerelagent.host.common.getFileSystemStats
import java.util.*

fun getFileSystemStateMCs(): List<FileSystemStateMetricsContainer> {
        val date = Date()
        return getFileSystemStats().filter {
            it.name.contains("/dev/")
        }.map {
               FileSystemStateMetricsContainer(
                       it.kbSize * 1024,
                       it.kbUsed * 1024,
                       it.name.removePrefix("/dev/"),
                       date
               )
        }
}

@Suppress("unused")
class FileSystemStateMetricsContainer(
        @MetricVariable("size")
        val size: Long,
        @MetricVariable("used")
        val used: Long,
        name: String,
        timeStamp: Date
) : MetricsContainer.Default("filesystem", name, timeStamp)
