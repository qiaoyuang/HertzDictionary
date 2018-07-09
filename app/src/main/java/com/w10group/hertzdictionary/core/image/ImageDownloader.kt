package com.w10group.hertzdictionary.core.image

import android.content.Context
import java.io.File

interface ImageDownloader {
    fun download(url: String, context: Context): File
}