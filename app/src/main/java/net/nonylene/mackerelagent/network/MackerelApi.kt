package net.nonylene.mackerelagent.network

import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object MackerelApi {

    private val client: OkHttpClient
    private val retrofit: Retrofit
    val service: MackerelService

    init {
        // todo: mackerel API Key
        client = OkHttpClient.Builder()
                .addInterceptor { chain ->
                    val request = if (chain.request().url().host() == "mackerel.io") {
                        chain.request().newBuilder()
                                .addHeader("X-Api-Key", "value")
                                .build()
                    } else {
                        chain.request()
                    }
                    chain.proceed(request)
                }
                .build()

        retrofit = Retrofit.Builder()
                .baseUrl("https://mackerel.io/")
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        service = retrofit.create(MackerelService::class.java)
    }
}
