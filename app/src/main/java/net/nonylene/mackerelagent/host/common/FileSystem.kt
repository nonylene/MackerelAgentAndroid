package net.nonylene.mackerelagent.host.common

import android.annotation.TargetApi
import android.os.Build
import net.nonylene.mackerelagent.host.metric.MetricVariable
import net.nonylene.mackerelagent.host.metric.MetricsContainer
import java.util.*

// df command returns immediately (within 10 msec)
fun getFileSystemStats(): List<FileSystemStat> {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        getToyboxDfStats()
    } else {
        getToolboxDfStats()
    }
}

// android Nougat (7.0) uses toybox's df command
// http://nonylene.hatenablog.jp/entry/2016/10/09/014419

// example:
// vbox86p:/ # df -Pk
// Filesystem      1K-blocks   Used Available Use% Mounted on
// tmpfs             1026300    444   1025856   1% /dev
// tmpfs             1026300      0   1026300   0% /mnt
// /dev/block/sda6   2031440 718556   1312884  36% /system
// tmpfs             1026300      0   1026300   0% /storage
//
// todo: send file mount path option /
@TargetApi(Build.VERSION_CODES.N)
private fun getToyboxDfStats(): List<FileSystemStat> {
    val exec = Runtime.getRuntime().exec(arrayOf("df", "-P", "-k"))
    exec.waitFor()
    return exec.inputStream.reader().readLines()
            // remove first header
            .drop(1)
            .map(String::trim)
            .map { it.split(Regex("\\s+")) }
            .filter { it[0].contains("/dev/") }
            .map {
                val used = it[2].toLong() * 1024
                FileSystemStat(
                        it[2].toLong() * 1024 + used,
                        used,
                        it[3].toLong(),
                        it[0].removePrefix("/dev/"),
                        Date()
                )
            }
}

// example:
// shell@SO-02H:/ $ df
// Filesystem               Size     Used     Free   Blksize
// /dev                   889.4M   104.0K   889.3M   4096
// /sys/fs/cgroup         889.4M     0.0K   889.4M   4096
// /sys/fs/cgroup/memory: Permission denied
private fun getToolboxDfStats(): List<FileSystemStat> {
    val exec = Runtime.getRuntime().exec("df")
    exec.waitFor()
    return exec.inputStream.reader().readLines()
            // remove first header
            .drop(1)
            .filterNot { it.contains("Permission denied") }
            .map(String::trim)
            .map { it.split(Regex("\\s+")) }
            .map {
                FileSystemStat(
                        restoreBytes(it[1]),
                        restoreBytes(it[2]),
                        restoreBytes(it[3]),
                        it[0],
                        Date()
                )
            }
}

// 12.3(K,M,G) -> 12300, 12300000, 12300000000
private fun restoreBytes(value: String): Long {
    return (value.dropLast(1).toDouble() * when (value.last()) {
        'G' -> 1024 * 1024 * 1024
        'M' -> 1024 * 1024
        'K' -> 1024
        else -> 1
    }).toLong()
}

@Suppress("unused")
class FileSystemStat(
        @MetricVariable("size")
        val size: Long,
        @MetricVariable("used")
        val used: Long,
        val available: Long,
        name: String,
        timeStamp: Date
) : MetricsContainer.Default("filesystem", name, timeStamp)
