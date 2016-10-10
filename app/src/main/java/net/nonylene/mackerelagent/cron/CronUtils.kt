package net.nonylene.mackerelagent.cron

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build

fun createAlarm(context: Context, afterMills: Long = 40 * 1000) {
    val sender = createAlarmReceiverIntent(context)
    val alarmManager = context.getAlarmManager()
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        // allow in doze
        // todo: add preference for power save
        alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + afterMills, sender)
    } else {
        alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + afterMills, sender)
    }
}

fun cancelAlarm(context: Context) {
    context.getAlarmManager().cancel(createAlarmReceiverIntent(context))
}

private fun createAlarmReceiverIntent(context: Context): PendingIntent {
    return PendingIntent.getBroadcast(context, 0,
            Intent(context, AlarmReceiver::class.java), PendingIntent.FLAG_UPDATE_CURRENT)
}

private fun Context.getAlarmManager(): AlarmManager {
    return getSystemService(Context.ALARM_SERVICE) as AlarmManager
}
