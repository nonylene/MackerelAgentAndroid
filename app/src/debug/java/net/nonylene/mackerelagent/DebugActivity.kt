package net.nonylene.mackerelagent

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.TextView
import io.reactivex.disposables.Disposable
import net.nonylene.mackerelagent.utils.startGatherMetricsService

class DebugActivity : AppCompatActivity() {

    var disposable: Disposable? = null

    val textView by lazy {
        findViewById(R.id.text_view) as TextView
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_debug)
        startGatherMetricsService(this)
    }

    override fun onStart() {
        super.onStart()
//        val memoryMetrics = getMemoryMetrics()
//        val loadavg = getLoadAverageMetrics()
//        val fileSystemMCs = getFileSystemMetricsList()
//        val req = HostSpecRequest(
//                "${Build.MANUFACTURER} ${Build.MODEL}",
//                HostSpecRequest.Meta(
//                        BuildConfig.VERSION_NAME,
//                        getBlockDevicesSpec(),
//                        getCPUSpec(),
//                        getFileSystemsSpec(),
//                        getKernelSpec(),
//                        getMemorySpec()
//                )
//        )
//        disposable = createMetricsCombineLatestObservable()
//                .retryWith(1) {
//                    // remove realm cache
//                    Realm.getDefaultInstance().use {
//                        it.executeTransaction(Realm::deleteExceptLog)
//                    }
//                }
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe {
//                    textView.text = createMetrics(it, this).toString() + req.toString()
//                    MackerelApi.getService(this).postMetrics(createMetrics(it, this))
//                            .subscribeOn(Schedulers.io())
//                            .observeOn(AndroidSchedulers.mainThread())
//                            .subscribe(::println)
//                }

//        MackerelApi.getService(this).updateHostSpec(PreferenceManager.getDefaultSharedPreferences(this).getHostId(this)!!, req).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe {
//            println(it.hostId)
//            PreferenceManager.getDefaultSharedPreferences(this).edit().putHostId(it.hostId, this).apply()
//        }
//        MackerelApi.getService(this).test().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe {
//            println(it.string())
//        }
    }

    override fun onStop() {
        super.onStop()
        disposable?.dispose()
    }
}
