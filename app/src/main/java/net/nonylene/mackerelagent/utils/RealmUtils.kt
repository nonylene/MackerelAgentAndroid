package net.nonylene.mackerelagent.utils

import io.realm.Realm
import net.nonylene.mackerelagent.realm.*

fun Realm.deleteExceptLog() {
    delete(RealmCPUStat::class.java)
    delete(RealmDiskStat::class.java)
    delete(RealmDiskStats::class.java)
    delete(RealmInterfaceStat::class.java)
    delete(RealmInterfaceStats::class.java)
}

fun realmLog(text: String?, error: Boolean) {
    Realm.getDefaultInstance().use { realm ->
        realm.executeTransactionAsync {
            realm.createRealmLog(text, error)
        }
    }
}