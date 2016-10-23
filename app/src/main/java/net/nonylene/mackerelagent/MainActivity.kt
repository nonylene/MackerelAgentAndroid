package net.nonylene.mackerelagent

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.TextView
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.functions.BiFunction
import io.reactivex.functions.Function
import io.reactivex.functions.Function3
import io.reactivex.schedulers.Schedulers
import io.realm.Realm
import net.nonylene.mackerelagent.host.metric.*
import net.nonylene.mackerelagent.host.spec.*
import net.nonylene.mackerelagent.network.createMetrics

class MainActivity : AppCompatActivity() {

    var disposable: Disposable? = null

    val textView by lazy {
        findViewById(R.id.text_view) as TextView
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        createAlarm(this)
    }

    override fun onStart() {
        super.onStart()
        val memoryMetrics = getMemoryMetrics()
        val loadavg = getLoadAverageMetrics()
        val fileSystemMCs = getFileSystemMetricsList()
        println(getKernelSpec())
        println(getBlockDevicesSpec())
        println(getFileSystemsSpec())
        println(getMemorySpec())
        println(getCPUSpec())
        disposable = Observable.combineLatest(getInterfaceMetricsListObservable(),
                getDiskMetricsListObservable(), getCPUMetricsObservable(),
                Function3 { interfaceDeltas: List<InterfaceDeltaMetrics>, diskDeltas: List<DiskDeltaMetrics>,
                            cpuPercentage: CPUPercentageMetrics ->
                    interfaceDeltas + diskDeltas + cpuPercentage + fileSystemMCs + memoryMetrics + loadavg
                })
                .retryWhen { observable ->
                    // retry once
                    Observable.zip(observable, Observable.range(1, 2), BiFunction { error: Throwable, count: Int ->
                        error to count
                    }).flatMap(Function<Pair<Throwable, Int>, Observable<Int>> {
                        if (it.second > 1) {
                            Observable.error(it.first)
                        } else {
                            Observable.just(it.second)
                        }
                    }).doOnNext {
                        // remove realm cache
                        Realm.getDefaultInstance().use {
                            it.executeTransaction(Realm::deleteAll)
                        }
                    }
                }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    textView.text = createMetrics(it).toString()
                }
    }

    override fun onStop() {
        super.onStop()
        disposable?.dispose()
    }
}
