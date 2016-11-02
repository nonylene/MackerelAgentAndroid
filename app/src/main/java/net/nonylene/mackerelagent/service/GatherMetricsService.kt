package net.nonylene.mackerelagent.service

import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.preference.PreferenceManager
import android.support.v4.content.WakefulBroadcastReceiver
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import io.realm.Realm
import net.nonylene.mackerelagent.MainActivity
import net.nonylene.mackerelagent.R
import net.nonylene.mackerelagent.network.MackerelApi
import net.nonylene.mackerelagent.network.model.createMetrics
import net.nonylene.mackerelagent.utils.createHostSpecRequest
import net.nonylene.mackerelagent.utils.createMetricsCombineLatestObservable
import net.nonylene.mackerelagent.utils.getHostId
import net.nonylene.mackerelagent.utils.retryWith
import java.util.concurrent.TimeUnit

class GatherMetricsService : Service() {

    var disposable: Disposable? = null

    companion object {
        const val NOTIFY_ID = 1
    }

    override fun onCreate() {
        super.onCreate()
        val notificationIntent = Intent(applicationContext, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(applicationContext, 0, notificationIntent, 0)
        val notification = Notification.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("konnnitiha")
                .setContentText("aaaa")
                .setContentIntent(pendingIntent)
                .build()
        startForeground(NOTIFY_ID, notification)

        // update host spec onCreate
        MackerelApi.getService(this)
                .updateHostSpec(PreferenceManager.getDefaultSharedPreferences(this).getHostId(this)!!, createHostSpecRequest())
                .subscribeOn(Schedulers.io())
                .subscribe({
                    // do nothing
                }, Throwable::printStackTrace)

        disposable = Observable.interval(0, 1, TimeUnit.MINUTES)
                .flatMap {
                    createMetricsCombineLatestObservable() }
                .retryWith(1) {
                    // remove realm cache
                    Realm.getDefaultInstance().use {
                        it.executeTransaction(Realm::deleteAll)
                    }
                }
                .flatMap { MackerelApi.getService(this).postMetrics(createMetrics(it, this)) }
                .subscribeOn(Schedulers.io())
                // do nothing (error -> crash)
                .subscribe()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let(WakefulBroadcastReceiver::completeWakefulIntent)
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        disposable?.dispose()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}
