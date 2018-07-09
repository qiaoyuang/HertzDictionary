package com.w10group.hertzdictionary.core

import android.content.Context
import com.bumptech.glide.request.target.Target
import com.w10group.hertzdictionary.core.image.ImageDownloader
import java.io.File

object GlideDownloader : ImageDownloader {

    override fun download(url: String, context: Context): File =
            GlideApp.with(context).asFile().load(url).override(Target.SIZE_ORIGINAL).submit().get()
}