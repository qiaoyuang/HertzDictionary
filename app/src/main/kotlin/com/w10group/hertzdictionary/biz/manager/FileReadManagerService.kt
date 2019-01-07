package com.w10group.hertzdictionary.biz.manager

import android.content.Context
import android.widget.TextView
import kotlinx.coroutines.*
import java.io.BufferedReader
import java.io.InputStreamReader

/**
 * Created by Administrator on 2018/7/8.
 * 本地文件读取并显示服务
 */

object FileReadManagerService {

    fun processByCoroutines(coroutineScope: CoroutineScope,
                            context: Context,
                            fileName: String,
                            vararg list: TextView) = coroutineScope.launch(Dispatchers.IO) {
        BufferedReader(InputStreamReader(context.assets.open(fileName), "UTF-8")).use {
            var i = 0
            val builder = StringBuilder()
            var line = it.readLine()
            var isFirst = true
            while (line != null) {
                if (line == "*") {
                    val text = builder.toString()
                    launch(Dispatchers.Main) { list[i++].text = text }
                    builder.delete(0, builder.length)
                    isFirst = true
                } else {
                    if (isFirst) isFirst = false
                    else builder.append("\n\n")
                    builder.append(line)
                }
                line = it.readLine()
            }
        }
    }

}