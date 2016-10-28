package net.nonylene.mackerelagent.utils

import java.util.*


inline fun <T> Iterable<T>.splitBy(predicate: (T) -> Boolean): List<List<T>> {
    val result = ArrayList<List<T>>()

    var entry = ArrayList<T>()
    for (item in this) {
       if (predicate(item)) {
           result.add(entry)
           entry = ArrayList<T>()
       } else {
           entry.add(item)
       }
    }
    result.add(entry)

    return result
}
