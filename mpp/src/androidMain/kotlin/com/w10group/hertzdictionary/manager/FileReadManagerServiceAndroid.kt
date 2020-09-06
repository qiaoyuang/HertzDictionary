package com.w10group.hertzdictionary.manager

import com.w10group.hertzdictionary.core.DataModule
import kotlinx.coroutines.*
import okio.buffer
import okio.source

/**
 * Created by Administrator on 2018/7/8.
 * 本地文件读取并显示服务
 */

internal actual suspend fun readFileToString(fileName: String): List<String> = withContext(Dispatchers.IO) {
    DataModule.appContext.assets.open(fileName).source().buffer().use {
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

internal actual suspend fun readFileToKV(fileName: String): List<KV> = withContext(Dispatchers.IO) {
    DataModule.appContext.assets.open(fileName).source().buffer().use {
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