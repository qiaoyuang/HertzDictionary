package com.w10group.hertzdictionary.app.biz.manager

import android.content.Context
import android.content.SharedPreferences
import android.widget.ImageView
import androidx.core.content.edit
import androidx.lifecycle.Lifecycle
import coil.Coil
import coil.request.ImageRequest
import com.w10group.hertzdictionary.app.core.view.getCoilDefaultConfig
import com.w10group.hertzdictionary.app.core.view.loadURL
import com.w10group.hertzdictionary.manager.getBackgroundUrl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
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
    private const val AVATAR_URL = "http://q.qlogo.cn/headimg_dl?dst_uin=1205173348&spec=100"
    private const val AVATAR_HD_URL = "https://upload-images.jianshu.io/upload_images/12354730-135b08eece7d74e3.jpg?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240"

    val urlList by lazy {
        LinkedList<String>().apply {
            add(AVATAR_HD_URL)
            add(todayURL)
        }
    }

    private lateinit var todayURL: String

    suspend infix fun loadAvatar(imageView: ImageView) {
        val getRequest = ImageRequest
            .Builder(imageView.context)
            .getCoilDefaultConfig()
            .data(AVATAR_URL)
            .build()
        Coil.imageLoader(imageView.context)
            .execute(getRequest)
            .drawable
            ?.let { imageView.setImageDrawable(it) }
    }

    suspend fun loadBackground(imageView: ImageView, lifecycle: Lifecycle) {
        val sharedPreferences = imageView.context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE)
        if (!::todayURL.isInitialized)
            todayURL = sharedPreferences.getString(KEY_URL, DEFAULT_VALUE)!!
        imageView.loadURL(todayURL, lifecycle)
        getURLOnInternetByCoroutines(imageView, sharedPreferences, lifecycle)
    }

    private suspend fun getURLOnInternetByCoroutines(imageView: ImageView,
                                                     sharedPreferences: SharedPreferences,
                                                     lifecycle: Lifecycle) = withContext(Dispatchers.IO) {
        val url = try {
            getBackgroundUrl()
        } catch (e: IOException) {
            e.printStackTrace()
            return@withContext
        }
        if (url != todayURL) {
            imageView.loadURL(url, lifecycle)
            todayURL = url
            sharedPreferences.edit { putString(KEY_URL, todayURL) }
        }
    }

}