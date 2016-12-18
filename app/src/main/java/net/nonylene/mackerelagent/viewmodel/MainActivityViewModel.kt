package net.nonylene.mackerelagent.viewmodel

import android.content.Context
import android.databinding.ObservableField
import android.support.v4.content.ContextCompat
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import net.nonylene.mackerelagent.MainActivity
import net.nonylene.mackerelagent.R

class MainActivityViewModel {

    val status: ObservableField<MainActivity.Status> = ObservableField(MainActivity.Status.NOT_RUNNING)

    fun getStatusText(status: MainActivity.Status, context: Context): SpannableStringBuilder {
        val statusText = context.getString(R.string.status_base, context.getString(status.text))
        return SpannableStringBuilder(statusText).apply {
            setSpan(ForegroundColorSpan(
                    ContextCompat.getColor(context, status.color)),
                    statusText.indexOf("●"),
                    statusText.length,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }
    }
}

