package com.w10group.hertzdictionary.biz.manager

import android.content.Context
import kotlinx.coroutines.*
import java.io.BufferedReader
import java.io.InputStreamReader

/**
 * Created by Administrator on 2018/7/8.
 * 本地文件读取并显示服务
 */

fun CoroutineScope.readFileAsync(context: Context,
                                 fileName: String): Deferred<List<String>> = async(Dispatchers.IO) {
    BufferedReader(InputStreamReader(context.assets.open(fileName), "UTF-8")).use {
        val builder = StringBuilder()
        var line = it.readLine()
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
            line = it.readLine()
        }
        result
    }
}