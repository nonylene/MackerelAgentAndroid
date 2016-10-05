package net.nonylene.mackerelagent.realm

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
