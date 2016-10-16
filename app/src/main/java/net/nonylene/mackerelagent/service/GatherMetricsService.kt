package net.nonylene.mackerelagent.service

import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.support.v4.content.WakefulBroadcastReceiver
import net.nonylene.mackerelagent.MainActivity
import net.nonylene.mackerelagent.R

class GatherMetricsService : Service() {

    companion object {
        const val NOTIFY_ID = 1
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // auto restart
        try {
            val notificationIntent = Intent(applicationContext, MainActivity::class.java)
            val pendingIntent = PendingIntent.getActivity(applicationContext, 0, notificationIntent, 0)
            val notification = Notification.Builder(this)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle("konnnitiha")
                    .setContentText("aaaa")
                    .setContentIntent(pendingIntent)
                    .build()
            startForeground(NOTIFY_ID, notification)
        } finally {
            intent?.let(WakefulBroadcastReceiver::completeWakefulIntent)
        }
        return START_STICKY
    }

    override fun onCreate() {
        super.onCreate()
        println("konnnitiha service!")
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}
