package com.w10group.hertzdictionary.core

import android.content.Context
import android.net.ConnectivityManager
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.w10group.hertzdictionary.biz.manager.NetworkService
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.lang.ref.SoftReference

/**
 * Created by Administrator on 2018/2/6 0006.
 * 网络工具类
 */

object NetworkUtil {

    private const val BASE_URL = "http://translate.google.cn/"

    private val mRetrofit: Retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create())
            .addCallAdapterFactory(CoroutineCallAdapterFactory())
            .build()

    private var mInstanceSoftReference = initInstanceSoftReference()

    fun getInstance(): NetworkService {
        if (mInstanceSoftReference.get() == null) {
            mInstanceSoftReference = initInstanceSoftReference()
        }
        return mInstanceSoftReference.get()!!
    }

    private fun initInstanceSoftReference() = SoftReference(mRetrofit.create(NetworkService::class.java))

    fun checkNetwork(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnected
    }

}