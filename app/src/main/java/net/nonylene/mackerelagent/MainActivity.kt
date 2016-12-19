package net.nonylene.mackerelagent

import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.annotation.ColorRes
import android.support.annotation.StringRes
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import net.nonylene.mackerelagent.databinding.ActivityMainBinding
import net.nonylene.mackerelagent.utils.getApiKey
import net.nonylene.mackerelagent.utils.getHostId
import net.nonylene.mackerelagent.utils.isGatherMetricsServiceRunning
import net.nonylene.mackerelagent.viewmodel.MainActivityViewModel

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val preference = PreferenceManager.getDefaultSharedPreferences(this)
        if (preference.getApiKey(this) == null || preference.getHostId(this) == null) {
            startActivity(Intent(this, SetupActivity::class.java))
            finish()
        }

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.model = MainActivityViewModel()
    }

    override fun onStart() {
        super.onResume()
        binding.model.status.set(if (isGatherMetricsServiceRunning(this)) Status.RUNNING else Status.NOT_RUNNING)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.activity_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.menu_activity_main_preference -> startActivity(Intent(this, MackerelPreferenceActivity::class.java))
        }
        return super.onOptionsItemSelected(item)
    }

    enum class Status(@StringRes val text: Int, @ColorRes val color: Int) {
        RUNNING(R.string.status_running, R.color.status_running),
        ERROR(R.string.status_error, R.color.status_error),
        NOT_CONFIGURED(R.string.status_not_configured, R.color.status_not_configured),
        NOT_RUNNING(R.string.status_not_running, R.color.status_not_running),
    }
}
