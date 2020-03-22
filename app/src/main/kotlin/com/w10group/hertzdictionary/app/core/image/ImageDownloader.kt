package com.w10group.hertzdictionary.app.core.image

import android.content.Context
import java.io.File

/**
 * Created by Administrator on 2018/7/9.
 * 大图查看器图片下载器借口
 */

interface ImageDownloader {
    fun download(url: String, context: Context): File
}