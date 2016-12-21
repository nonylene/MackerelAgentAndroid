package net.nonylene.mackerelagent

import android.app.Application
import io.realm.Realm
import io.realm.RealmConfiguration

class MackerelAgentApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        Realm.init(this)
        Realm.setDefaultConfiguration(RealmConfiguration.Builder()
                .schemaVersion(2L)
                .deleteRealmIfMigrationNeeded()
                .build()
        )
    }
}
