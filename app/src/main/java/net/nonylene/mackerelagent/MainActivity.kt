package net.nonylene.mackerelagent

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.TextView
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Function3
import io.reactivex.schedulers.Schedulers
import io.realm.Realm
import net.nonylene.mackerelagent.metric.*

class MainActivity : AppCompatActivity() {

    var disposable : Disposable? = null

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
        disposable = Observable.combineLatest(getInterfaceDeltaObservable(), getDiskDeltaObservable(), getCPUPercentageObservable(),
                Function3 { interfaceDeltas: List<InterfaceDelta>, diskDeltas: List<DiskDelta>, cpuPercentage: CPUPercentage ->
                    interfaceDeltas to diskDeltas to cpuPercentage
                })
                .retryWhen { observable ->
                    // retry once
                    observable.doOnNext {
                        // remove realm cache
                        Realm.getDefaultInstance().executeTransaction(Realm::deleteAll)
                    }.take(1)
                }
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    textView.text = "$loadavgText\n$memText\ninterface: ${it.first.first}\ndisk: ${it.first.second}\ncpu: ${it.second}"
                }
    }

    override fun onStop() {
        super.onStop()
        disposable?.dispose()
    }
}
