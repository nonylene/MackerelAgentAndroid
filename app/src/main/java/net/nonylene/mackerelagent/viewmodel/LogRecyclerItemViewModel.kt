package net.nonylene.mackerelagent.viewmodel

import android.databinding.ObservableField
import android.support.annotation.ColorRes
import net.nonylene.mackerelagent.R
import net.nonylene.mackerelagent.realm.RealmAgentLog

class LogRecyclerItemViewModel {
    val text: ObservableField<String> = ObservableField()
    @ColorRes
    val color: ObservableField<Int> = ObservableField()

    fun setRealmLog(agentLog: RealmAgentLog) {
        text.set(agentLog.text)
        color.set(if (agentLog.error) R.color.status_error else R.color.status_running)
    }
}

