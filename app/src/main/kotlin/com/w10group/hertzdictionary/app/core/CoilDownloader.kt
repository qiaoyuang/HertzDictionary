package com.w10group.hertzdictionary.app.core

import android.content.Context
import android.graphics.Bitmap
import androidx.core.graphics.drawable.toBitmap
import coil.Coil
import coil.request.GetRequest
import com.w10group.hertzdictionary.app.core.image.ImageDownloader
import com.w10group.hertzdictionary.app.core.view.getCoilDefaultConfig
import com.w10group.hertzdictionary.manager.DateManagerService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.withContext
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

/**
 * Created by Administrator on 2018/7/9.
 * Glide 实现的图片下载器
 */

object CoilDownloader : ImageDownloader {

    // 过期时间：3 天
    private const val FILE_EXPIRED_TIME = DateManagerService.ONE_DAY * 3

    override suspend fun download(url: String, context: Context): File = withContext(Dispatchers.IO) {
        File(context.cacheDir, "${ImageDownloader.getDefaultFileName(url)}.jpeg").also { file ->
            try {
                if (!file.exists()) {
                    file.createNewFile()
                    val getRequest = GetRequest
                        .Builder(context)
                        .getCoilDefaultConfig()
                        .data(url)
                        .build()
                    Coil.imageLoader(context)
                        .execute(getRequest)
                        .drawable
                        ?.toBitmap(config = Bitmap.Config.RGB_565)
                        ?.let {
                            val bos = BufferedOutputStream(FileOutputStream(file))
                            it.compress(Bitmap.CompressFormat.JPEG, 100, bos)
                        }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    suspend infix fun checkCacheAndClear(context: Context) = withContext(Dispatchers.IO) {
        try {
            context.cacheDir
                .listFiles()
                ?.asFlow()
                ?.buffer()
                ?.collect {
                    if (it.exists()) {
                        val today = System.currentTimeMillis()
                        if (today - it.lastModified() > FILE_EXPIRED_TIME)
                            it.delete()
                    }
                }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}