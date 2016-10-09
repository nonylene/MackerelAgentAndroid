package net.nonylene.mackerelagent.metric

import android.annotation.TargetApi
import android.os.Build
import io.reactivex.Observable
import io.reactivex.ObservableOnSubscribe
import io.reactivex.schedulers.Schedulers

fun getFileSystemStatsObservable(): Observable<List<FileSystemStat>> {
    return Observable.create(ObservableOnSubscribe<List<FileSystemStat>> { subscriber ->
        subscriber.onNext(getFileSystemStats())
    }).subscribeOn(Schedulers.newThread())
}

private fun getFileSystemStats(): List<FileSystemStat> {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        getToyboxDfStats()
    } else {
        getToolboxDfStats()
    }
}

// android Nougat (7.0) uses toybox's df command
// http://nonylene.hatenablog.jp/entry/2016/10/09/014419

// example:
// bullhead:/ $ df
// Filesystem                                             1K-blocks     Used Available Use% Mounted on
// tmpfs                                                     922680      428    922252   1% /dev
// tmpfs                                                     922680        0    922680   0% /mnt
// /dev/block/dm-0                                          2999516  2430552    552580  82% /system
// /dev/block/dm-1                                           241908   184160     52752  78% /vendor
// todo: send file mount path option
@TargetApi(Build.VERSION_CODES.N)
private fun getToyboxDfStats(): List<FileSystemStat> {
    val exec = Runtime.getRuntime().exec(arrayOf("df", "-P"))
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
                        it[0].removePrefix("/dev/"),
                        it[2].toLong() * 1024 + used,
                        used
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
                        it[0],
                        restoreBytes(it[1]),
                        restoreBytes(it[2])
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

//todo: sanitize
@MetricPrefix("filesystem")
data class FileSystemStat(
        @MetricName
        val name: String,
        @MetricVariable("size")
        val size: Long,
        @MetricVariable("used")
        val used: Long
): DefaultMetric
