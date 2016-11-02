package net.nonylene.mackerelagent.utils

import android.content.Context
import android.content.Intent
import net.nonylene.mackerelagent.service.GatherMetricsService

fun createGatherMetricsService(context: Context) {
    context.startService(createGatherMetricsServiceIntent(context))
}

fun stopGatherMetricsService(context: Context) {
    context.stopService(createGatherMetricsServiceIntent(context))
}

fun createGatherMetricsServiceIntent(context: Context): Intent {
    return Intent(context, GatherMetricsService::class.java)
}
