package net.nonylene.mackerelagent

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import net.nonylene.mackerelagent.databinding.ActivityLicenseShowBinding

class LicenseShowActivity : AppCompatActivity() {

    lateinit var binding: ActivityLicenseShowBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_license_show)
        binding.licenseView.loadUrl("file:///android_asset/licenses.html")
    }
}
