package com.w10group.hertzdictionary.app.core

import android.content.Context
import com.bumptech.glide.request.target.Target
import com.w10group.hertzdictionary.app.core.image.ImageDownloader
import java.io.File

/**
 * Created by Administrator on 2018/7/9.
 * Glide实现的图片下载器
 */

object GlideDownloader : ImageDownloader {

    override fun download(url: String, context: Context): File =
            GlideApp.with(context).asFile().load(url).override(Target.SIZE_ORIGINAL).submit().get()
}