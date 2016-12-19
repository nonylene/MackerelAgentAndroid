package net.nonylene.mackerelagent.realm

import io.realm.Realm
import io.realm.RealmObject
import io.realm.annotations.Index
import java.util.*

open class RealmLog: RealmObject() {
    open var text: String? = null
    open var error = false
    @Index
    open var timeStamp = Date(0)
}

fun Realm.createRealmLog(text: String?, error: Boolean): RealmLog {
    return createObject(RealmLog::class.java).apply {
        this.text = text
        this.error = error
        this.timeStamp = Date()
    }
}
