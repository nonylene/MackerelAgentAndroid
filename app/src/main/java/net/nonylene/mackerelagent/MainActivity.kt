package net.nonylene.mackerelagent

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.TextView
import net.nonylene.mackerelagent.metric.getCPUPercentageObservable
import net.nonylene.mackerelagent.metric.getLoadAverage5min
import net.nonylene.mackerelagent.metric.getMemoryInfo

class MainActivity : AppCompatActivity() {

    val textView by lazy {
        findViewById(R.id.text_view) as TextView
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onStart() {
        super.onStart()
        val memText = "memory: ${getMemoryInfo()}"
        val loadavgText = "loadavg: ${getLoadAverage5min()}"
        getCPUPercentageObservable().subscribe {
            textView.text = "$loadavgText\n$memText\ncpu: $it"
        }
    }
}
