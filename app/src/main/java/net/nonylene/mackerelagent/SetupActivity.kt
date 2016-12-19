package net.nonylene.mackerelagent

import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v7.app.AppCompatActivity
import android.text.Editable
import android.text.TextWatcher
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import net.nonylene.mackerelagent.databinding.ActivitySetupBinding
import net.nonylene.mackerelagent.network.MackerelApi
import net.nonylene.mackerelagent.utils.createHostSpecRequest
import net.nonylene.mackerelagent.utils.putApiKey
import net.nonylene.mackerelagent.utils.putHostId
import net.nonylene.mackerelagent.utils.startGatherMetricsService

class SetupActivity : AppCompatActivity() {

    lateinit var binding: ActivitySetupBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_setup)

        binding.apiKeyInputLayout.error = "Api Key is required"
        binding.apiKeyEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (s.isEmpty()) {
                    binding.apiKeyInputLayout.error = "API Key is required"
                    binding.button.isEnabled = false
                } else {
                    binding.apiKeyInputLayout.error = null
                    binding.button.isEnabled = true
                }
            }
        })

        binding.button.setOnClickListener { v ->
            val preference = PreferenceManager.getDefaultSharedPreferences(v.context)
            preference.edit().putApiKey(binding.apiKeyEditText.text.toString(), v.context).apply()

            val startService = { hostId: String ->
                preference.edit().putHostId(hostId, v.context).apply()
                startGatherMetricsService(v.context)
                startActivity(Intent(v.context, MainActivity::class.java))
                finish()
            }

            val hostId = binding.hostIdEditText.text
            if (hostId.isEmpty()) {
                binding.button.text = "Creating Host ..."
                binding.button.isEnabled = false
                MackerelApi.getService(this)
                        .postHostSpec(createHostSpecRequest())
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe {
                            binding.button.text = "Host created !"
                            startService(it.hostId)
                        }
            } else {
                startService(hostId.toString())
            }
        }
    }
}