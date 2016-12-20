package net.nonylene.mackerelagent.utils

import io.realm.Realm
import io.realm.Sort
import net.nonylene.mackerelagent.realm.*

fun Realm.deleteExceptLog() {
    delete(RealmCPUStat::class.java)
    delete(RealmDiskStat::class.java)
    delete(RealmDiskStats::class.java)
    delete(RealmInterfaceStat::class.java)
    delete(RealmInterfaceStats::class.java)
}

fun realmLog(text: String?, error: Boolean) {
    Realm.getDefaultInstance().use {
        it.executeTransactionAsync { realm ->
            // limit logs less than 200
            val logs = realm.where(RealmAgentLog::class.java)
                    .findAllSorted("timeStamp", Sort.DESCENDING)
            val count = logs.count()
            (200 until count).forEach {
                logs.deleteLastFromRealm()
            }
            realm.createRealmAgentLog(text, error)
        }
    }
}