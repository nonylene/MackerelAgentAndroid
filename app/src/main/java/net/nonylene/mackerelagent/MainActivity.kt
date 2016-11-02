package net.nonylene.mackerelagent

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.preference.Preference
import android.preference.PreferenceManager
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.functions.BiFunction
import io.reactivex.functions.Function
import io.reactivex.functions.Function3
import io.reactivex.schedulers.Schedulers
import io.realm.Realm
import net.nonylene.mackerelagent.host.metric.*
import net.nonylene.mackerelagent.host.spec.*
import net.nonylene.mackerelagent.network.MackerelApi
import net.nonylene.mackerelagent.network.model.HostSpecRequest
import net.nonylene.mackerelagent.network.model.createMetrics
import net.nonylene.mackerelagent.utils.createGatherMetricsService
import net.nonylene.mackerelagent.utils.getHostId
import net.nonylene.mackerelagent.utils.putApiKey
import net.nonylene.mackerelagent.utils.putHostId

class MainActivity : AppCompatActivity() {

    val textView by lazy {
        findViewById(R.id.text_view) as TextView
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        createGatherMetricsService(this)
    }

    override fun onStart() {
        super.onStart()
        val req = HostSpecRequest(
                "${Build.MANUFACTURER} ${Build.MODEL}",
                HostSpecRequest.Meta(
                        BuildConfig.VERSION_NAME,
                        getBlockDevicesSpec(),
                        getCPUSpec(),
                        getFileSystemsSpec(),
                        getKernelSpec(),
                        getMemorySpec()
                )
        )

//        MackerelApi.getService(this).updateHostSpec(PreferenceManager.getDefaultSharedPreferences(this).getHostId(this)!!, req).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe {
//            println(it.hostId)
//            PreferenceManager.getDefaultSharedPreferences(this).edit().putHostId(it.hostId, this).apply()
//        }
//        MackerelApi.getService(this).test().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe {
//            println(it.string())
//        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.activity_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item?.itemId) {
            R.id.menu_activity_main_preference -> startActivity(Intent(this, MackerelPreferenceActivity::class.java))
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onStop() {
        super.onStop()
//        disposable?.dispose()
    }
}
