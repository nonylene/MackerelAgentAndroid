package net.nonylene.mackerelagent.cron

import android.content.Context
import android.content.Intent
import android.support.v4.content.WakefulBroadcastReceiver

class AlarmReceiver: WakefulBroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        startWakefulService(context, Intent(context, GatherMetricsService::class.java))
    }
}

