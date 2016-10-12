package net.nonylene.mackerelagent.realm

import io.realm.Realm
import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.Index
import net.nonylene.mackerelagent.metric.DiskStat
import java.util.*

open class RealmDiskStat : RealmObject() {
    open var name = ""
    open var reads = 0.0
    open var readsMerged = 0.0
    open var sectorsRead = 0.0
    open var readTime = 0.0
    open var writes = 0.0
    open var writesMerged = 0.0
    open var sectorsWritten = 0.0
    open var writeTime = 0.0
    open var ioInProgress = 0.0
    open var ioTime = 0.0
    open var ioTimeWeighted = 0.0
    open var timeStamp = Date(0)

    fun createDiskStat(): DiskStat {
        return DiskStat(name, reads, readsMerged, sectorsRead, readTime,
                writes, writesMerged, sectorsWritten, writeTime,
                ioInProgress, ioTime, ioTimeWeighted, timeStamp
        )
    }
}

open class RealmDiskStats : RealmObject() {
    open var stats = RealmList<RealmDiskStat>()
    @Index
    open var timeStamp = Date(0)
}

fun Realm.createRealmDiskStats(diskStats: List<DiskStat>): RealmDiskStats {
    return createObject(RealmDiskStats::class.java).apply {
        diskStats.map { createRealmDiskStat(it) }.forEach {
            stats.add(it)
        }
    }
}

private fun Realm.createRealmDiskStat(diskStat: DiskStat): RealmDiskStat {
    return createObject(RealmDiskStat::class.java).apply {
        name = diskStat.name
        reads = diskStat.reads
        readsMerged = diskStat.readsMerged
        sectorsRead = diskStat.sectorsRead
        readTime = diskStat.readTime
        writes = diskStat.writes
        writesMerged = diskStat.writesMerged
        sectorsWritten = diskStat.sectorsWritten
        writeTime = diskStat.writeTime
        ioInProgress = diskStat.ioInProgress
        ioTime = diskStat.ioTime
        ioTimeWeighted = diskStat.ioTimeWeighted
        timeStamp = diskStat.timeStamp
    }
}
