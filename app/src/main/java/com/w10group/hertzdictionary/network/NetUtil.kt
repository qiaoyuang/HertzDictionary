package com.w10group.hertzdictionary.network

import android.content.Context
import android.net.ConnectivityManager
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Created by Administrator on 2018/2/6 0006.
 * 网络工具类
 */
object NetUtil {

    val BASE_URL: String

    val mRetrofit: Retrofit

    init {
        BASE_URL = "http://101.37.37.36/"
        mRetrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()
    }

    fun checkNetwork(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        return networkInfo != null && networkInfo.isAvailable
    }

}