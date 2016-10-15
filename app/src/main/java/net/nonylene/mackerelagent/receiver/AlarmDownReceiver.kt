package net.nonylene.mackerelagent.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import net.nonylene.mackerelagent.createAlarm

class AlarmDownReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        createAlarm(context)
    }

}
