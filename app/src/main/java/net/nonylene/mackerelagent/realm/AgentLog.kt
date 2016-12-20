package net.nonylene.mackerelagent.realm

import io.realm.Realm
import io.realm.RealmObject
import io.realm.annotations.Index
import net.nonylene.mackerelagent.AgentLog
import java.util.*

open class RealmAgentLog : RealmObject() {
    open var text: String? = null
    open var error = false
    @Index
    open var timeStamp = Date(0)

    fun createAgentLog(): AgentLog {
        return AgentLog(text, error, timeStamp)
    }
}

fun Realm.createRealmAgentLog(text: String?, error: Boolean): RealmAgentLog {
    return createObject(RealmAgentLog::class.java).apply {
        this.text = text
        this.error = error
        this.timeStamp = Date()
    }
}
