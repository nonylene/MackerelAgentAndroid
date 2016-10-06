package net.nonylene.mackerelagent

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.TextView
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.BiFunction
import io.reactivex.schedulers.Schedulers
import io.realm.Realm
import net.nonylene.mackerelagent.metric.*

class MainActivity : AppCompatActivity() {

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
        Observable.combineLatest(getInterfaceDeltaObservable(), getCPUPercentageObservable(),
                BiFunction { interfaceDeltas: List<InterfaceDelta>, cpuPercentage: CPUPercentage ->
                    interfaceDeltas to cpuPercentage
                })
                .retryWhen { observable ->
                    // remove realm cache
                    observable.doOnNext {
                        Realm.getDefaultInstance().executeTransaction(Realm::deleteAll)
                    }
                    observable.take(1)
                }
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    textView.text = "$loadavgText\n$memText\ninterface: ${it.first}\ncpu: ${it.second}"
                }, { error ->
                    error.printStackTrace()
                    // remove realm cache
//                    Realm.getDefaultInstance().executeTransactionAsync(Realm::deleteAll)
                })
    }
}
