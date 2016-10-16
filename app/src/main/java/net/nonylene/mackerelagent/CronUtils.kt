package net.nonylene.mackerelagent

import android.content.Context
import android.content.Intent
import net.nonylene.mackerelagent.service.GatherMetricsService

fun createAlarm(context: Context) {
    context.startService(createGatherMetricServiceIntent(context))
}

fun cancelAlarm(context: Context) {
    context.stopService(createGatherMetricServiceIntent(context))
}

fun createGatherMetricServiceIntent(context: Context): Intent {
    return Intent(context, GatherMetricsService::class.java)
}
