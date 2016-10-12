package net.nonylene.mackerelagent.realm

import io.realm.Realm
import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.Index
import net.nonylene.mackerelagent.metric.InterfaceStat
import java.util.*

open class RealmInterfaceStats : RealmObject() {
    open var stats = RealmList<RealmInterfaceStat>()
    @Index
    open var timeStamp = Date(0)
}

open class RealmInterfaceStat : RealmObject() {
    open var name = ""
    open var receiveBytes = 0.0
    open var receivePackets = 0.0
    open var receiveErrs = 0.0
    open var receiveDrop = 0.0
    open var receiveFifo = 0.0
    open var receiveFrame = 0.0
    open var receiveCompressed = 0.0
    open var receiveMulticast = 0.0
    open var transmitBytes = 0.0
    open var transmitPackets = 0.0
    open var transmitErrs = 0.0
    open var transmitDrop = 0.0
    open var transmitFifo = 0.0
    open var transmitColls = 0.0
    open var transmitCarrier = 0.0
    open var transmitCompressed = 0.0
    open var timeStamp = Date(0)

    fun createInterfaceStat(): InterfaceStat {
        return InterfaceStat(name, receiveBytes, receivePackets, receiveErrs, receiveDrop,
                receiveFifo, receiveFrame, receiveCompressed, receiveMulticast, transmitBytes,
                transmitPackets, transmitErrs, transmitDrop, transmitFifo, transmitColls,
                transmitCarrier, transmitCompressed, timeStamp
        )
    }

}

fun Realm.createRealmInterfaceStats(interfaceStats: List<InterfaceStat>): RealmInterfaceStats {
    return createObject(RealmInterfaceStats::class.java).apply {
        interfaceStats.map { createRealmInterfaceStat(it) }.forEach {
            stats.add(it)
        }
    }
}

private fun Realm.createRealmInterfaceStat(interfaceStat: InterfaceStat): RealmInterfaceStat {
    return createObject(RealmInterfaceStat::class.java).apply {
        name = interfaceStat.name
        receiveBytes = interfaceStat.receiveBytes
        receivePackets = interfaceStat.receivePackets
        receiveErrs = interfaceStat.receiveErrs
        receiveDrop = interfaceStat.receiveDrop
        receiveFifo = interfaceStat.receiveFifo
        receiveFrame = interfaceStat.receiveFrame
        receiveCompressed = interfaceStat.receiveCompressed
        receiveMulticast = interfaceStat.receiveMulticast
        transmitBytes = interfaceStat.transmitBytes
        transmitPackets = interfaceStat.transmitPackets
        transmitErrs = interfaceStat.transmitErrs
        transmitDrop = interfaceStat.transmitDrop
        transmitFifo = interfaceStat.transmitFifo
        transmitColls = interfaceStat.transmitColls
        transmitCarrier = interfaceStat.transmitCarrier
        transmitCompressed = interfaceStat.transmitCompressed
        timeStamp = interfaceStat.timeStamp
    }
}
