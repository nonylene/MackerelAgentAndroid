package net.nonylene.mackerelagent.viewmodel

import android.databinding.ObservableField
import net.nonylene.mackerelagent.AgentLog
import net.nonylene.mackerelagent.R
import java.text.SimpleDateFormat
import java.util.*

private val DATE_FORMAT = SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault())

class LogRecyclerItemViewModel {
    val text: ObservableField<String> = ObservableField()
    val dateText: ObservableField<String> = ObservableField()
    val colorRes: ObservableField<Int> = ObservableField()

    fun setAgentLog(agentLog: AgentLog) {
        text.set(agentLog.text)
        colorRes.set(if (agentLog.error) R.color.status_error else R.color.status_running)
        dateText.set(DATE_FORMAT.format(agentLog.timeStamp))
    }
}

