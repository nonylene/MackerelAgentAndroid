package net.nonylene.mackerelagent.service

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.os.IBinder
import android.preference.PreferenceManager
import android.support.v4.app.NotificationCompat
import android.support.v4.content.WakefulBroadcastReceiver
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import net.nonylene.mackerelagent.MainActivity
import net.nonylene.mackerelagent.R
import net.nonylene.mackerelagent.network.MackerelApi
import net.nonylene.mackerelagent.network.model.createMetrics
import net.nonylene.mackerelagent.utils.*
import java.util.concurrent.TimeUnit



class GatherMetricsService : Service() {

    var disposable: Disposable? = null

    companion object {
        const val NOTIFY_ID = 1
    }

    override fun onCreate() {
        super.onCreate()
        realmLog("Starting monitoring service", false)
        startForeground(NOTIFY_ID, createNotification(false))

        // update host spec onCreate
        MackerelApi.getService(this)
                .updateHostSpec(PreferenceManager.getDefaultSharedPreferences(this).getHostId(this)!!, createHostSpecRequest())
                .subscribeOn(Schedulers.io())
                .subscribe({
                    realmLog("Host updated", false)
                    updateNotification(false)
                }, { error ->
                    realmLog(createErrorMessage(error), true)
                    updateNotification(true)
                })

        disposable = Observable.interval(0, 1, TimeUnit.MINUTES)
                .flatMap {
                    createMetricsCombineLatestObservable()
                }
                .retryWith(1) {
                    realmLog("Failed to create metrics; retry once", true)
                }
                // request per 5 minutes
                .buffer(5)
                .flatMap { MackerelApi.getService(this).postMetrics(createMetrics(it.flatten(), this)) }
                .subscribeOn(Schedulers.io())
                .subscribe({
                    realmLog("Metrics posted", false)
                    updateNotification(false)
                }, { error ->
                    realmLog(createErrorMessage(error), true)
                    updateNotification(true)
                })
    }

    private fun isNetworkAvailable(): Boolean {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetworkInfo = connectivityManager.activeNetworkInfo
        return activeNetworkInfo != null && activeNetworkInfo.isConnected
    }

    private fun createNotification(error: Boolean): Notification {
        val notificationIntent = Intent(applicationContext, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(applicationContext, 0, notificationIntent, 0)

        return NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("MackerelAgent")
                .setContentText("Monitoring" + if (error) " - error occurred!" else "")
                .setContentIntent(pendingIntent)
                .build()
    }

    private fun updateNotification(error: Boolean) {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(NOTIFY_ID, createNotification(error))
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let(WakefulBroadcastReceiver::completeWakefulIntent)
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        realmLog("Stopping monitoring service", false)
        disposable?.dispose()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}
