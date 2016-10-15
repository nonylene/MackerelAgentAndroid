package net.nonylene.mackerelagent.service

import android.app.IntentService
import android.content.Intent
import android.support.v4.content.WakefulBroadcastReceiver
import net.nonylene.mackerelagent.createAlarm

class GatherMetricsService(name: String) : IntentService(name) {

    // required constructor
    @Suppress("unused")
    constructor() : this(GatherMetricsService::class.java.name)

    override fun onHandleIntent(intent: Intent?) {
        try {
            println("handle!")
            createAlarm(applicationContext)
        } finally {
            WakefulBroadcastReceiver.completeWakefulIntent(intent)
        }
    }

}

