package com.w10group.hertzdictionary.biz.manager

import android.content.Context
import android.content.SharedPreferences
import android.widget.ImageView
import com.w10group.hertzdictionary.core.GlideApp
import com.w10group.hertzdictionary.core.NetworkUtil
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import org.jetbrains.anko.design.snackbar
import java.io.IOException
import java.util.*

/**
 * Created by Administrator on 2018/6/15.
 * 背景图片加载管理服务
 */

object ImageManagerService {

    private const val FILE_NAME = "BGImageInfo"
    private const val KEY_URL = "URL"
    private const val DEFAULT_VALUE = "null"
    private const val GET_URL = "http://guolin.tech/api/bing_pic"
    private const val AVATAR_URL = "http://q.qlogo.cn/headimg_dl?dst_uin=1205173348&spec=100"
    private const val AVATAR_HD_URL = "https://upload-images.jianshu.io/upload_images/12354730-135b08eece7d74e3.jpg?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240"

    val urlList by lazy {
        val list = LinkedList<String>()
        list.add(AVATAR_HD_URL)
        list.add(todayURL)
        list
    }

    private lateinit var todayURL: String

    fun loadAvatar(context: Context, imageView: ImageView) {
        GlideApp.with(context).load(AVATAR_URL).dontAnimate().into(imageView)
    }

    suspend fun loadBackground(context: Context, imageView: ImageView) {
        getURL(context, imageView)
    }

    private suspend fun getURL(context: Context, imageView: ImageView) {
        val sharedPreferences = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE)
        if (!::todayURL.isInitialized) {
            todayURL = sharedPreferences.getString(KEY_URL, DEFAULT_VALUE)!!
        }
        GlideApp.with(context).load(todayURL).dontAnimate().into(imageView)
        getURLOnInternetByCoroutines(context, imageView, sharedPreferences)
    }

    @Deprecated("这个函数已经过时了，请使用协程重构的版本。")
    @Suppress("CheckResult")
    private fun getURLOnInternet(context: Context, imageView: ImageView, sharedPreferences: SharedPreferences) {
        if (!NetworkUtil.checkNetwork(context)) {
            imageView.snackbar("当前无网络连接")
            return
        }
        NetworkUtil.create<NetworkService>()
                .getImageURL(GET_URL)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeBy(onNext = {
                    val url = it.charStream().readText()
                    if (url != todayURL) {
                        todayURL = url
                        GlideApp.with(context).load(todayURL).dontAnimate().into(imageView)
                        val edit = sharedPreferences.edit()
                        edit.putString(KEY_URL, todayURL)
                        edit.apply()
                    }
                }, onError = { it.printStackTrace() })
    }

    private suspend fun getURLOnInternetByCoroutines(context: Context, imageView: ImageView, sharedPreferences: SharedPreferences) {
        val requestBody = try {
            NetworkUtil.create<NetworkService>().getImageURLByCoroutines(GET_URL).await()
        } catch (e: IOException) {
            e.printStackTrace()
            return
        }
        val url = requestBody.charStream().readText()
        if (url != todayURL) {
            todayURL = url
            GlideApp.with(context).load(todayURL).dontAnimate().into(imageView)
            val edit = sharedPreferences.edit()
            edit.putString(KEY_URL, todayURL)
            edit.apply()
        }
    }

}