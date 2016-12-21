package net.nonylene.mackerelagent.utils

import io.realm.Realm
import io.realm.Sort
import net.nonylene.mackerelagent.realm.*
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

val REALM_LOCK = ReentrantLock()

fun <R> realmUseWithLock(action: (Realm) -> R): R {
    REALM_LOCK.withLock {
        return Realm.getDefaultInstance().use(action)
    }
}

fun Realm.deleteExceptLog() {
    delete(RealmCPUStat::class.java)
    delete(RealmDiskStat::class.java)
    delete(RealmDiskStats::class.java)
    delete(RealmInterfaceStat::class.java)
    delete(RealmInterfaceStats::class.java)
}

fun realmLog(text: String?, error: Boolean) {
    realmUseWithLock {
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