package net.nonylene.mackerelagent.viewmodel

import android.databinding.ObservableField
import android.support.annotation.ColorRes
import net.nonylene.mackerelagent.AgentLog
import net.nonylene.mackerelagent.R
import java.text.SimpleDateFormat

private val DATE_FORMAT = SimpleDateFormat("yyyy/MM/dd HH:mm:ss")

class LogRecyclerItemViewModel {
    val text: ObservableField<String> = ObservableField()
    val dateText: ObservableField<String> = ObservableField()
    @ColorRes
    val colorRes: ObservableField<Int> = ObservableField()

    fun setRealmLog(agentLog: AgentLog) {
        text.set(agentLog.text)
        colorRes.set(if (agentLog.error) R.color.status_error else R.color.status_running)
        dateText.set(DATE_FORMAT.format(agentLog.timeStamp))
    }
}

