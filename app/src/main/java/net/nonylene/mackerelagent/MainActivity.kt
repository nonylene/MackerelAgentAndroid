package net.nonylene.mackerelagent

import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.annotation.ColorRes
import android.support.annotation.StringRes
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import net.nonylene.mackerelagent.databinding.ActivityMainBinding
import net.nonylene.mackerelagent.utils.isGatherMetricsServiceRunning
import net.nonylene.mackerelagent.viewmodel.MainActivityViewModel

class MainActivity : AppCompatActivity() {

    val textView by lazy {
        findViewById(R.id.text_view) as TextView
    }

    lateinit var binding: ActivityMainBinding
    // todo: after bug fixed (kotlin 1.0.5?), remove model from member variables
    val model: MainActivityViewModel = MainActivityViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        @Suppress("MISSING_DEPENDENCY_CLASS")
        binding.model = model
        model.status.set(if (isGatherMetricsServiceRunning(this)) Status.RUNNING else Status.NOT_RUNNING)
    }

    override fun onStart() {
        super.onStart()
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

    override fun onStop() {
        super.onStop()
//        disposable?.dispose()
    }

    enum class Status(@StringRes val text: Int, @ColorRes val color: Int) {
        RUNNING(R.string.status_running, R.color.status_running),
        ERROR(R.string.status_error, R.color.status_error),
        NOT_CONFIGURED(R.string.status_not_configured, R.color.status_not_configured),
        NOT_RUNNING(R.string.status_not_running, R.color.status_not_running),
    }
}
