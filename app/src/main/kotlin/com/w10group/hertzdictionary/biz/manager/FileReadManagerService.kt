package com.w10group.hertzdictionary.biz.manager

import android.content.Context
import android.widget.TextView
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.*
import java.io.BufferedReader
import java.io.InputStreamReader

/**
 * Created by Administrator on 2018/7/8.
 * 本地文件读取并显示服务
 */

object FileReadManagerService {

    /**
     * 使用RxJava异步流读取
     */
    @Suppress("CheckResult")
    fun process(fileName: String, context: Context, vararg list: TextView) {
        var i = 0
        Observable.create<String> {
            BufferedReader(InputStreamReader(context.assets.open(fileName), "UTF-8")).use { bufferedReader ->
                val builder = StringBuilder()
                var line = bufferedReader.readLine()
                var isFirst = true
                while (line != null) {
                    if (line == "*") {
                        it.onNext(builder.toString())
                        builder.delete(0, builder.length)
                        isFirst = true
                    } else {
                        if (isFirst) isFirst = false
                        else builder.append("\n\n")
                        builder.append(line)
                    }
                    line = bufferedReader.readLine()
                }
            }
            it.onComplete()
        }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeBy { list[i++].text = it }
    }

    /**
     * 使用协程非阻塞异步读取
     */
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