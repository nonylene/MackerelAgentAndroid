package net.nonylene.mackerelagent

import android.app.Application
import io.realm.Realm

class MackerelAgentApplication: Application() {

    override fun onCreate() {
        super.onCreate()
        Realm.init(this)
    }
}
