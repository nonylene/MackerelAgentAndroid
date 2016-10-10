package net.nonylene.mackerelagent

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.TextView
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.functions.BiFunction
import io.reactivex.functions.Function
import io.reactivex.functions.Function4
import io.reactivex.schedulers.Schedulers
import io.realm.Realm
import net.nonylene.mackerelagent.metric.*

class MainActivity : AppCompatActivity() {

    var disposable: Disposable? = null

    val textView by lazy {
        findViewById(R.id.text_view) as TextView
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onStart() {
        super.onStart()
        val memText = "memory: ${getMemoryInfo()}"
        val loadavgText = "loadavg: ${getLoadAverage5min()}"
        disposable = Observable.combineLatest(getInterfaceDeltaObservable(), getDiskDeltaObservable(),
                getCPUPercentageObservable(), getFileSystemStatsObservable(),
                Function4 { interfaceDeltas: List<InterfaceDelta>, diskDeltas: List<DiskDelta>,
                            cpuPercentage: CPUPercentage, fileSystemStats: List<FileSystemStat> ->
                    interfaceDeltas to diskDeltas to cpuPercentage to fileSystemStats
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
                        Realm.getDefaultInstance().executeTransaction(Realm::deleteAll)
                    }
                }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    textView.text = "$loadavgText\n$memText\ninterface: ${it.first.first.first}\n" +
                            "disk: ${it.first.first.second}\ncpu: ${it.first.second}\nfile: ${it.second}"
                }
    }

    override fun onStop() {
        super.onStop()
        disposable?.dispose()
    }
}
