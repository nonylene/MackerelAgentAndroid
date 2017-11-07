package net.nonylene.mackerelagent.network

import android.content.Context
import android.preference.PreferenceManager
import net.nonylene.mackerelagent.BuildConfig
import net.nonylene.mackerelagent.utils.GSON_IGNORE_EXCLUDE_ANNOTATION
import net.nonylene.mackerelagent.utils.getApiKey
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
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

            val builder = OkHttpClient.Builder()
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

            if (BuildConfig.LOG_OKHTTP) {
                builder.addInterceptor(HttpLoggingInterceptor().apply {
                    level = HttpLoggingInterceptor.Level.BODY
                })
            }

            val retrofit = Retrofit.Builder()
                    .client(builder.build())
                    .baseUrl("https://api.mackerelio.com/")
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create(GSON_IGNORE_EXCLUDE_ANNOTATION))
                    .build()

            service = retrofit.create(MackerelService::class.java)
        }
        return service!!
    }
}
