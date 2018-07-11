package com.w10group.hertzdictionary.business.manager

import android.content.Context
import android.content.SharedPreferences
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.w10group.hertzdictionary.core.GlideApp
import com.w10group.hertzdictionary.core.NetworkUtil
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import org.jetbrains.anko.design.snackbar
import java.util.*

/**
 * Created by Administrator on 2018/6/15.
 * 背景图片加载管理类
 */

object ImageManagerService {

    private const val FILE_NAME = "BGImageInfo"
    private const val KEY_TODAY = "today"
    private const val KEY_URL = "URL"
    private const val DEFAULT_VALUE = "null"
    private const val GET_URL = "http://guolin.tech/api/bing_pic"
    private const val AVATAR_URL = "http://q.qlogo.cn/headimg_dl?dst_uin=1205173348&spec=100"

    private lateinit var todayURL: String

    fun loadAvatar(context: Context, imageView: ImageView) {
        GlideApp.with(context).load(AVATAR_URL).dontAnimate().into(imageView)
    }

    fun loadBackground(context: Context, imageView: ImageView) {
        getURLOnLocal(context, imageView)
    }

    val urlList by lazy {
        val list = LinkedList<String>()
        list.add(AVATAR_URL)
        list.add(todayURL)
        list
    }

    private fun getURLOnLocal(context: Context, imageView: ImageView) {
        if (!ImageManagerService::todayURL.isLateinit) {
            Glide.with(context).load(todayURL).into(imageView)
            return
        }
        val sharedPreferences = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE)
        val calendar = Calendar.getInstance()
        val today = "${calendar.get(Calendar.YEAR)}-${calendar.get(Calendar.MONTH) + 1}-${calendar.get(Calendar.DAY_OF_MONTH)}"
        val date = sharedPreferences.getString(KEY_TODAY, DEFAULT_VALUE)
        if (today == date) {
            todayURL = sharedPreferences.getString(KEY_URL, DEFAULT_VALUE)
            GlideApp.with(context).load(todayURL).dontAnimate().into(imageView)
            return
        }
        getURLOnInternet(context, imageView, today, sharedPreferences)
    }

    private fun getURLOnInternet(context: Context, imageView: ImageView, today: String, sharedPreferences: SharedPreferences) {
        if (!NetworkUtil.checkNetwork(context)) {
            snackbar(imageView, "当前无网络连接")
            return
        }
        NetworkUtil.create<NetworkService>()
                .getImageURL(GET_URL)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeBy {
                    todayURL = it.charStream().readText()
                    GlideApp.with(context).load(todayURL).dontAnimate().into(imageView)
                    val edit = sharedPreferences.edit()
                    edit.putString(KEY_TODAY, today)
                    edit.putString(KEY_URL, todayURL)
                    edit.apply()
                }
    }

}