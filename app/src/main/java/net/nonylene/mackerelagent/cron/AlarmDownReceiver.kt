package net.nonylene.mackerelagent.cron

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class AlarmDownReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        createAlarm(context)
    }

}
