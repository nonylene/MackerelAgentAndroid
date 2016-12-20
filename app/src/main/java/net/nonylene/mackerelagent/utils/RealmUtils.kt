package net.nonylene.mackerelagent.utils

import io.realm.Realm
import io.realm.Sort
import net.nonylene.mackerelagent.realm.RealmAgentLog
import net.nonylene.mackerelagent.realm.createRealmAgentLog

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