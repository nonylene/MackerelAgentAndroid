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
import com.jakewharton.retrofit2.adapter.rxjava2.HttpException
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import io.realm.Sort
import net.nonylene.mackerelagent.MainActivity
import net.nonylene.mackerelagent.R
import net.nonylene.mackerelagent.network.MackerelApi
import net.nonylene.mackerelagent.network.model.Metric
import net.nonylene.mackerelagent.network.model.createMetrics
import net.nonylene.mackerelagent.realm.RealmMetricJson
import net.nonylene.mackerelagent.realm.createRealmMetricJson
import net.nonylene.mackerelagent.utils.*
import java.util.concurrent.TimeUnit




class GatherMetricsService : Service() {

    lateinit var collectMetricsDisposable: Disposable
    lateinit var sendMetricsDisposable: Disposable

    val error = PublishSubject.create<Throwable>()

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
                }, { error.onNext(it) })

        collectMetricsDisposable = Observable.interval(0, 60, TimeUnit.SECONDS)
                .subscribe {
                    createMetricsCombineLatestObservable()
                            .retryWith(1) {
                                realmLog("Failed to create metrics; retry once", true)
                            }
                            .subscribeOn(Schedulers.io())
                            .map { createMetrics(it, this) }
                            .subscribe({ metrics ->
                                realmUseWithLock {
                                    it.executeTransactionAsync { realm ->
                                        // limit logs less than 3000
                                        val logs = realm.where(RealmMetricJson::class.java)
                                                .findAllSorted("timeStamp", Sort.DESCENDING)
                                        val count = logs.count()
                                        (3000 until count).forEach {
                                            logs.deleteLastFromRealm()
                                        }
                                        metrics.forEach { realm.createRealmMetricJson(it) }
                                    }
                                }
                                realmLog("Metrics collected", false)
                                updateNotification(false)
                            }, { error.onNext(it) })
                }

        class MetricWrapper(val id: Long, val metric: Metric)

        sendMetricsDisposable = Observable.interval(60, 300, TimeUnit.SECONDS)
                .filter { isNetworkAvailable(this) }
                .subscribe {
                    Observable
                            .fromIterable(
                                    // realm does not support rxjava2 currently.
                                    realmUseWithLock { realm ->
                                        realm.where(RealmMetricJson::class.java)
                                                .findAll()
                                                .map {
                                                    MetricWrapper(it.cacheId, it.createMetric())
                                                }
                                    }
                            )
                            .subscribeOn(Schedulers.io())
                            .buffer(400)
                            .subscribe({ wrappers ->
                                val max = wrappers.last().id
                                val min = wrappers.first().id
                                val response = MackerelApi.getService(this).postMetrics(wrappers.map(MetricWrapper::metric)).execute()
                                if (response.isSuccessful) {
                                    realmLog("Metrics posted", false)
                                    updateNotification(false)
                                    realmUseWithLock {
                                        it.executeTransactionAsync { realm ->
                                            realm.where(RealmMetricJson::class.java)
                                                    .greaterThanOrEqualTo("cacheId", min)
                                                    .lessThanOrEqualTo("cacheId", max)
                                                    .findAll()
                                                    .deleteAllFromRealm()
                                        }
                                    }
                                } else {
                                    error.onNext(HttpException(response))
                                }
                            }, { error.onNext(it) })
                }

        error.subscribe {
            realmLog(createErrorMessage(it), true)
            updateNotification(true)
        }

        realmLog("Started monitoring service", false)
    }

    fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return connectivityManager.activeNetworkInfo?.isConnected ?: false
    }

    private fun createNotification(error: Boolean): Notification {
        val notificationIntent = Intent(applicationContext, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(applicationContext, 0, notificationIntent, 0)

        return NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.notification)
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
        error.onComplete()
        realmLog("Stopped monitoring service", false)
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}
