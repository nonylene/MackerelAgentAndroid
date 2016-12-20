package net.nonylene.mackerelagent

import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceFragment
import android.support.v7.app.AppCompatActivity

class MackerelPreferenceActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // !! no support version !!
        fragmentManager.beginTransaction().replace(android.R.id.content, MackerelPreferenceFragment()).commit()
    }

    class MackerelPreferenceFragment : PreferenceFragment() {

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            addPreferencesFromResource(R.xml.preferences)
        }

        override fun onActivityCreated(savedInstanceState: Bundle?) {
            super.onActivityCreated(savedInstanceState)
            findPreference(activity.getString(R.string.preference_oss_key)).setOnPreferenceClickListener {
                startActivity(Intent(activity, LicenseShowActivity::class.java))
                true
            }
        }
    }
}

