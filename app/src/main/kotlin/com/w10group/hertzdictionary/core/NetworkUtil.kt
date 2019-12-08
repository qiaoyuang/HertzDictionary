package com.w10group.hertzdictionary.core

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.w10group.hertzdictionary.biz.manager.NetworkService
import kotlinx.serialization.UnstableDefault
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import java.lang.ref.SoftReference

/**
 * Created by Administrator on 2018/2/6 0006.
 * 网络工具类
 */

object NetworkUtil {

    private const val BASE_URL = "http://translate.google.cn/"
    private const val MEDIA_TYPE_JSON = "application/json"

    @UseExperimental(UnstableDefault::class)
    private val mRetrofit: Retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(Json.nonstrict.asConverterFactory(MEDIA_TYPE_JSON.toMediaType()))
            .build()

    private var mInstanceSoftReference = initInstanceSoftReference()

    val instance: NetworkService
        get() {
            if (mInstanceSoftReference.get() == null)
                mInstanceSoftReference = initInstanceSoftReference()
            return mInstanceSoftReference.get()!!
        }

    private fun initInstanceSoftReference() = SoftReference(mRetrofit.create(NetworkService::class.java))

}