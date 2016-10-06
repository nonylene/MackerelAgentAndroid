package net.nonylene.mackerelagent.realm

import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.Index
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
}

open class RealmInterfaceStats: RealmObject() {
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
}

