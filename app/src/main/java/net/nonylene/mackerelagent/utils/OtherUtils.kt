package net.nonylene.mackerelagent.utils

import com.jakewharton.retrofit2.adapter.rxjava2.HttpException

fun createErrorMessage(error: Throwable): String? {
    return when(error) {
        is HttpException -> {
            "${error.message} - ${error.response().errorBody().string()}"
        }
        else -> error.message
    }
}

