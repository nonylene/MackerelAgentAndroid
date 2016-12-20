package net.nonylene.mackerelagent.utils

import com.google.gson.ExclusionStrategy
import com.google.gson.FieldAttributes
import com.google.gson.GsonBuilder
import com.jakewharton.retrofit2.adapter.rxjava2.HttpException
import net.nonylene.mackerelagent.network.Exclude

fun createErrorMessage(error: Throwable): String? {
    return when (error) {
        is HttpException -> {
            "${error.message} - ${error.response().errorBody().string()}"
        }
        else -> error.message
    }
}

val GSON_IGNORE_EXCLUDE_ANNOTATION = GsonBuilder().setExclusionStrategies(object : ExclusionStrategy {
    override fun shouldSkipClass(clazz: Class<*>?) = false
    override fun shouldSkipField(f: FieldAttributes?) = f?.getAnnotation(Exclude::class.java) != null
}).create()
