package com.w10group.hertzdictionary.biz.manager

import android.content.Context
import kotlinx.coroutines.*
import okio.Okio

/**
 * Created by Administrator on 2018/7/8.
 * 本地文件读取并显示服务
 */

suspend fun readFileToString(context: Context, fileName: String): List<String> = withContext(Dispatchers.IO) {
    Okio.buffer(Okio.source(context.assets.open(fileName))).use {
        val builder = StringBuilder()
        var line = it.readUtf8Line()
        var isFirst = true
        val result = ArrayList<String>()
        while (line != null) {
            if (line == "*") {
                result.add(builder.toString())
                builder.clear()
                isFirst = true
            } else {
                if (isFirst) isFirst = false
                else builder.append("\n\n")
                builder.append(line)
            }
            line = it.readUtf8Line()
        }
        result
    }
}

typealias KV = Pair<String, String>

suspend fun readFileToKV(context: Context, fileName: String): List<KV> = withContext(Dispatchers.IO) {
    Okio.buffer(Okio.source(context.assets.open(fileName))).use {
        val builder = StringBuilder()
        var line = it.readUtf8Line()
        var isFirst = true
        val result = ArrayList<KV>()
        var key = ""
        while (line != null) {
            when (line) {
                "&" -> {
                    key = builder.toString()
                    builder.clear()
                    isFirst = true
                }
                "*" -> {
                    val kv = key to builder.toString()
                    result.add(kv)
                    builder.clear()
                    isFirst = true
                }
                else -> {
                    if (isFirst) isFirst = false
                    else builder.append("\n")
                    builder.append(line)
                }
            }
            line = it.readUtf8Line()
        }
        result
    }
}