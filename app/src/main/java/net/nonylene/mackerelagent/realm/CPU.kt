package net.nonylene.mackerelagent.realm

import io.realm.Realm
import io.realm.RealmObject
import io.realm.annotations.Index
import net.nonylene.mackerelagent.metric.CPUStat
import java.util.*

open class RealmCPUStat : RealmObject() {
    open var user = 0.0
    open var nice = 0.0
    open var system = 0.0
    open var idle = 0.0
    open var iowait = 0.0
    open var irq = 0.0
    open var softirq = 0.0
    open var steal = 0.0
    open var guest = 0.0
    open var guestNice = 0.0
    @Index
    open var timeStamp = Date(0)

    fun createCPUStat(): CPUStat {
        return CPUStat(user, nice, system, idle, iowait, irq, softirq, steal, guest, guestNice, timeStamp)
    }
}

fun Realm.createRealmCPUStat(cpuStat: CPUStat): RealmCPUStat {
    return createObject(RealmCPUStat::class.java).apply {
        user = cpuStat.user
        nice = cpuStat.nice
        system = cpuStat.system
        idle = cpuStat.idle
        iowait = cpuStat.iowait
        irq = cpuStat.irq
        softirq = cpuStat.softirq
        steal = cpuStat.steal
        guest = cpuStat.guest
        guestNice = cpuStat.guestNice
        timeStamp = cpuStat.timeStamp
    }
}