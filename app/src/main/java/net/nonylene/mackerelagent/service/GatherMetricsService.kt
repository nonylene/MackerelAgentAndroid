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
import io.realm.Realm
import io.realm.Sort
import net.nonylene.mackerelagent.MainActivity
import net.nonylene.mackerelagent.R
import net.nonylene.mackerelagent.network.MackerelApi
import net.nonylene.mackerelagent.network.model.createMetrics
import net.nonylene.mackerelagent.realm.RealmMetricJson
import net.nonylene.mackerelagent.realm.createMetricJson
import net.nonylene.mackerelagent.utils.*
import java.util.concurrent.TimeUnit


class GatherMetricsService : Service() {

    lateinit var collectMetricsDisposable: Disposable
    lateinit var sendMetricsDisposable: Disposable

    companion object {
        const val NOTIFY_ID = 1
    }

    override fun onCreate() {
        super.onCreate()
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

        collectMetricsDisposable = Observable.interval(0, 60, TimeUnit.SECONDS)
                .flatMap {
                    createMetricsCombineLatestObservable()
                }
                .retryWith(1) {
                    realmLog("Failed to create metrics; retry once", true)
                }
                .subscribeOn(Schedulers.io())
                .map { createMetrics(it, this) }
                .subscribe({ metrics ->
                    Realm.getDefaultInstance().use {
                        it.executeTransactionAsync { realm ->
                            // limit logs less than 1000
                            val logs = realm.where(RealmMetricJson::class.java)
                                    .findAllSorted("timeStamp", Sort.DESCENDING)
                            val count = logs.count()
                            (1000 until count).forEach {
                                logs.deleteLastFromRealm()
                            }
                            metrics.forEach { realm.createMetricJson(it) }
                        }
                    }
                    realmLog("Metrics collected", false)
                    updateNotification(false)
                }, { error ->
                    realmLog(createErrorMessage(error), true)
                    updateNotification(true)
                })

        sendMetricsDisposable = Observable.interval(150, 300, TimeUnit.SECONDS)
                .filter { isNetworkAvailable() }
                .flatMap {
                    val metrics = Realm.getDefaultInstance().use { realm ->
                        realm.where(RealmMetricJson::class.java)
                                .findAll()
                                .map(RealmMetricJson::createMetric)
                    }
                    MackerelApi.getService(this).postMetrics(metrics)
                }
                .subscribeOn(Schedulers.io())
                .subscribe({
                    realmLog("Metrics posted", false)
                    updateNotification(false)
                }, { error ->
                    realmLog(createErrorMessage(error), true)
                    updateNotification(true)
                })

        realmLog("Started monitoring service", false)
    }

    private fun isNetworkAvailable(): Boolean {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetworkInfo = connectivityManager.activeNetworkInfo
        return activeNetworkInfo?.isConnected ?: false
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
        collectMetricsDisposable.dispose()
        sendMetricsDisposable.dispose()
        realmLog("Stopped monitoring service", false)
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}
