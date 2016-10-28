package net.nonylene.mackerelagent.utils

import android.content.Context
import android.content.SharedPreferences
import net.nonylene.mackerelagent.R


fun SharedPreferences.getApiKey(context: Context): String? {
    return getString(context.getString(R.string.preference_api_key_key), null)
}

fun SharedPreferences.Editor.putApiKey(key: String?, context: Context): SharedPreferences.Editor {
    return putString(context.getString(R.string.preference_api_key_key), key)
}

fun SharedPreferences.getHostId(context: Context): String? {
    return getString(context.getString(R.string.preference_host_id_key), null)
}

fun SharedPreferences.Editor.putHostId(id: String?, context: Context): SharedPreferences.Editor {
    return putString(context.getString(R.string.preference_host_id_key), id)
}
