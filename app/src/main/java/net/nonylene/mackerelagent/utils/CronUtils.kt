package net.nonylene.mackerelagent.utils

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import net.nonylene.mackerelagent.service.GatherMetricsService

fun startGatherMetricsService(context: Context) {
    context.startService(createGatherMetricsServiceIntent(context))
    realmLog("Start monitoring service", false)
}

fun stopGatherMetricsService(context: Context) {
    context.stopService(createGatherMetricsServiceIntent(context))
    realmLog("Stop monitoring service", false)
}

fun createGatherMetricsServiceIntent(context: Context): Intent {
    return Intent(context, GatherMetricsService::class.java)
}

fun isGatherMetricsServiceRunning(context: Context): Boolean {
    val manager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
    return manager.getRunningServices(Integer.MAX_VALUE).any { service ->
        GatherMetricsService::class.java.name == service.service.className
    }
}