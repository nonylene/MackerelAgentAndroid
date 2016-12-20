package net.nonylene.mackerelagent.receiver

import android.content.Context
import android.content.Intent
import android.preference.PreferenceManager
import android.support.v4.content.WakefulBroadcastReceiver
import net.nonylene.mackerelagent.utils.createGatherMetricsServiceIntent
import net.nonylene.mackerelagent.utils.getApiKey
import net.nonylene.mackerelagent.utils.getHostId

class RebootUpdateReceiver : WakefulBroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val pref = PreferenceManager.getDefaultSharedPreferences(context)
        if (pref.getApiKey(context) != null && pref.getHostId(context) != null) {
            startWakefulService(context, createGatherMetricsServiceIntent(context))
        }
    }

}
