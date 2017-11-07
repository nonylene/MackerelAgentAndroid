package net.nonylene.mackerelagent

import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.annotation.ColorRes
import android.support.annotation.StringRes
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuItem
import io.realm.Sort
import net.nonylene.mackerelagent.databinding.ActivityMainBinding
import net.nonylene.mackerelagent.realm.RealmAgentLog
import net.nonylene.mackerelagent.utils.*
import net.nonylene.mackerelagent.viewmodel.MainActivityViewModel

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    val adapter = LogRecyclerAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val preference = PreferenceManager.getDefaultSharedPreferences(this)
        if (preference.getApiKey(this) == null || preference.getHostId(this) == null) {
            startActivity(Intent(this, SetupActivity::class.java))
        }

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.model = MainActivityViewModel()

        binding.button.setOnClickListener {
            when (binding.model!!.status.get().action) {
                Action.START -> startGatherMetricsService(this)
                Action.STOP -> stopGatherMetricsService(this)
                Action.SETUP -> startActivity(Intent(this, SetupActivity::class.java))
            }
            updateStatus()
        }

        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = LinearLayoutManager(this)

        updateStatus()
    }

    private fun updateStatus() {
        val logs = realmUseWithLock {
            it.where(RealmAgentLog::class.java)
                    .findAllSorted("timeStamp", Sort.DESCENDING)
                    .map(RealmAgentLog::createAgentLog)
        }
        val preference = PreferenceManager.getDefaultSharedPreferences(this)
        if (preference.getApiKey(this) == null || preference.getHostId(this) == null) {
            binding.model!!.status.set(Status.NOT_CONFIGURED)
        } else {
            if (logs.firstOrNull()?.error == true) {
                binding.model!!.status.set(Status.ERROR)
            }
            binding.model!!.status.set(if (isGatherMetricsServiceRunning(this)) Status.RUNNING else Status.NOT_RUNNING)
        }
        adapter.logs = logs
    }

    override fun onStart() {
        super.onStart()
        updateStatus()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.activity_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.menu_activity_main_preference -> startActivity(Intent(this, MackerelPreferenceActivity::class.java))
            R.id.menu_activity_reload -> updateStatus()
        }
        return super.onOptionsItemSelected(item)
    }

    enum class Status(@StringRes val text: Int, @ColorRes val color: Int, val action: Action) {
        RUNNING(R.string.status_running, R.color.status_running, Action.STOP),
        ERROR(R.string.status_error, R.color.status_error, Action.STOP),
        NOT_CONFIGURED(R.string.status_not_configured, R.color.status_not_configured, Action.SETUP),
        NOT_RUNNING(R.string.status_not_running, R.color.status_not_running, Action.START),
    }

    enum class Action(@StringRes val text: Int) {
        SETUP(R.string.action_setup),
        START(R.string.action_start),
        STOP(R.string.action_stop)
    }
}
