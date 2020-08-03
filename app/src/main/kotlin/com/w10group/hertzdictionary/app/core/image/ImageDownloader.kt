package com.w10group.hertzdictionary.app.core.image

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

/**
 * Created by Administrator on 2018/7/9.
 * 大图查看器图片下载器借口
 */

interface ImageDownloader {

    companion object {
        private fun bytesToHexString(bytes: ByteArray): String {
            val sb = StringBuilder()
            for (b in bytes) {
                val hex = Integer.toHexString(0xFF and b.toInt())
                if (hex.length == 1)
                    sb.append('0')
                sb.append(hex)
            }
            return sb.toString()
        }

        suspend fun getDefaultFileName(url: String): String = withContext(Dispatchers.Default) {
            try {
                val digest = MessageDigest.getInstance("MD5")
                digest.update(url.toByteArray())
                bytesToHexString(digest.digest())
            } catch (e: NoSuchAlgorithmException) {
                url.hashCode().toString()
            }
        }
    }

    suspend fun download(url: String, context: Context): File

}