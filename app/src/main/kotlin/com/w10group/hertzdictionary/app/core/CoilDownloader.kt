package com.w10group.hertzdictionary.app.core

import android.content.Context
import android.graphics.Bitmap
import androidx.core.graphics.drawable.toBitmap
import coil.Coil
import coil.request.CachePolicy
import coil.request.GetRequest
import coil.size.Scale
import com.w10group.hertzdictionary.app.core.image.ImageDownloader
import com.w10group.hertzdictionary.app.core.view.getCoilDefaultConfig
import kotlinx.coroutines.Dispatchers
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

    override suspend fun download(url: String, context: Context): File = withContext(Dispatchers.IO) {
        val getRequest = GetRequest
            .Builder(context)
            .getCoilDefaultConfig()
            .data(url)
            .build()
        File(context.cacheDir, "${System.currentTimeMillis()}.jpg").also { file ->
            Coil.imageLoader(context)
                .execute(getRequest)
                .drawable
                ?.toBitmap(config = Bitmap.Config.RGB_565)
                ?.let {
                    val bos = BufferedOutputStream(FileOutputStream(file))
                    try {
                        it.compress(Bitmap.CompressFormat.WEBP, 100, bos)
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
        }
    }
}