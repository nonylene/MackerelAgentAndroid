package net.nonylene.mackerelagent.network

import android.content.Context
import android.preference.PreferenceManager
import com.google.gson.ExclusionStrategy
import com.google.gson.FieldAttributes
import com.google.gson.GsonBuilder
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import net.nonylene.mackerelagent.utils.getApiKey
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object MackerelApi {

    private var service: MackerelService? = null

    fun getService(context: Context): MackerelService {
        if (service == null) {
            val appContext = context.applicationContext
            // called on every request
            val apiKeyGetter = {
                PreferenceManager.getDefaultSharedPreferences(appContext).getApiKey(appContext)
            }

            val client = OkHttpClient.Builder()
                    .addInterceptor { chain ->
                        val request = if (chain.request().url().host() == "mackerel.io") {
                            apiKeyGetter()?.let {
                                chain.request().newBuilder()
                                        .addHeader("X-Api-Key", it)
                                        .build()
                            } ?: chain.request()
                        } else {
                            chain.request()
                        }
                        chain.proceed(request)
                    }
                    .build()

            val retrofit = Retrofit.Builder()
                    .client(client)
                    .baseUrl("https://mackerel.io/")
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create(
                            GsonBuilder().setExclusionStrategies(object : ExclusionStrategy {
                                override fun shouldSkipClass(clazz: Class<*>?) = false
                                override fun shouldSkipField(f: FieldAttributes?) = f?.getAnnotation(Exclude::class.java) != null
                            }).create()
                    ))
                    .build()

            service = retrofit.create(MackerelService::class.java)
        }
        return service!!
    }
}
