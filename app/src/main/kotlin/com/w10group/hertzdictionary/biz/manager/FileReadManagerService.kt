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
        val result = ArrayList<String>()
        with(StringBuilder()) {
            var line = it.readUtf8Line()
            var isFirst = true
            while (line != null) {
                if (line == "*") {
                    result.add(toString())
                    clear()
                    isFirst = true
                } else {
                    if (isFirst) isFirst = false
                    else append("\n\n")
                    append(line)
                }
                line = it.readUtf8Line()
            }
        }
        result
    }
}

typealias KV = Pair<String, String>

suspend fun readFileToKV(context: Context, fileName: String): List<KV> = withContext(Dispatchers.IO) {
    Okio.buffer(Okio.source(context.assets.open(fileName))).use {
        val result = ArrayList<KV>()
        with(StringBuilder()) {
            var line = it.readUtf8Line()
            var isFirst = true
            var key = ""
            while (line != null) {
                when (line) {
                    "&" -> {
                        key = toString()
                        clear()
                        isFirst = true
                    }
                    "*" -> {
                        result.add(key to toString())
                        clear()
                        isFirst = true
                    }
                    else -> {
                        if (isFirst) isFirst = false
                        else append("\n")
                        append(line)
                    }
                }
                line = it.readUtf8Line()
            }
        }
        result
    }
}