package net.nonylene.mackerelagent.receiver

import android.content.Context
import android.content.Intent
import android.preference.PreferenceManager
import android.support.v4.content.WakefulBroadcastReceiver
import io.realm.Realm
import net.nonylene.mackerelagent.utils.createGatherMetricsServiceIntent
import net.nonylene.mackerelagent.utils.deleteExceptLog
import net.nonylene.mackerelagent.utils.getApiKey
import net.nonylene.mackerelagent.utils.getHostId

class RebootUpdateReceiver : WakefulBroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        // reset realm database (not async !!)
        Realm.getDefaultInstance().use {
            it.executeTransaction(Realm::deleteExceptLog)
        }
        val pref = PreferenceManager.getDefaultSharedPreferences(context)
        if (pref.getApiKey(context) != null && pref.getHostId(context) != null) {
            startWakefulService(context, createGatherMetricsServiceIntent(context))
        }
    }

}
