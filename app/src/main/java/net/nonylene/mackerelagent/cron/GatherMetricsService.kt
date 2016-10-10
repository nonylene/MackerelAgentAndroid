package net.nonylene.mackerelagent.cron

import android.app.IntentService
import android.content.Intent
import android.support.v4.content.WakefulBroadcastReceiver

class GatherMetricsService(name: String) : IntentService(name) {

    // required constructor
    @Suppress("unused")
    constructor() : this(GatherMetricsService::class.java.name)

    override fun onHandleIntent(intent: Intent?) {
        try {
            println("handle!")
            createAlarm(applicationContext, 40 * 1000)
        } finally {
            WakefulBroadcastReceiver.completeWakefulIntent(intent)
        }
    }

}

