package net.nonylene.mackerelagent.receiver

import android.content.Context
import android.content.Intent
import android.support.v4.content.WakefulBroadcastReceiver
import io.realm.Realm
import net.nonylene.mackerelagent.utils.createGatherMetricsServiceIntent
import net.nonylene.mackerelagent.utils.deleteExceptLog

class RebootUpdateReceiver : WakefulBroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        // reset realm database (not async !!)
        Realm.getDefaultInstance().use {
            it.executeTransaction(Realm::deleteExceptLog)
        }
        startWakefulService(context, createGatherMetricsServiceIntent(context))
    }

}
