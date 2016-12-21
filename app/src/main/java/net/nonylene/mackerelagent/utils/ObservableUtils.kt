package net.nonylene.mackerelagent.utils

import io.reactivex.Observable
import io.reactivex.functions.BiFunction
import io.reactivex.functions.Function

fun <T> Observable<T>.retryWith(count: Int = 0, doOnNext: ((Int) -> Unit)?): Observable<T> {
    return retryWhen { observable ->
        // retry once
        val obs = Observable.zip(observable, Observable.range(1, count + 1),
                BiFunction { error: Throwable, count: Int ->
                    error to count
                }).flatMap(Function<Pair<Throwable, Int>, Observable<Int>> {
            if (it.second > count) {
                Observable.error(it.first)
            } else {
                Observable.just(it.second)
            }
        })
        doOnNext?.let { obs.doOnNext(it) } ?: obs
    }
}

