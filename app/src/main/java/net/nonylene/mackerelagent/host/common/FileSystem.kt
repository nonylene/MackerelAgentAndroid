package net.nonylene.mackerelagent.host.common

import android.annotation.TargetApi
import android.os.Build

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
            .map {
                val used = it[2].toLong()
                val available = it[3].toLong()
                FileSystemStat(
                        it[0],
                        available + used,
                        used,
                        available,
                        it[4].removeSuffix("%").toDouble(),
                        it[5]
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
                val name = it[0]
                val size = restoreKBytes(it[1])
                val used = restoreKBytes(it[2])
                FileSystemStat(
                        name,
                        size,
                        used,
                        restoreKBytes(it[3]),
                        used.toDouble() / size.toDouble() * 100,
                        name
                )
            }
}

// 12.3(K,M,G) -> 12300, 12300000, 12300000000
private fun restoreKBytes(value: String): Long {
    return (value.dropLast(1).toDouble() * when (value.last()) {
        'G' -> 1024 * 1024
        'M' -> 1024
        'K' -> 1
        else -> 1
    }).toLong()
}

// all units of sizes are kByte
data class FileSystemStat(
        val name: String,
        val kbSize: Long,
        val kbUsed: Long,
        val kbAvailable: Long,
        val percentUsed: Double,
        val mount: String
)
