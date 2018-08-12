package com.w10group.hertzdictionary.business.manager

import android.content.Context
import android.widget.TextView
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import java.io.BufferedReader
import java.io.InputStreamReader

/**
 * Created by Administrator on 2018/7/8.
 * 本地文件读取并显示服务
 */

object FileReadManagerService {

    fun process(fileName: String, context: Context, vararg list: TextView) {
        var i = 0
        Observable.create<String> {
            val inputStream = context.assets.open(fileName)
            val bufferedReader = BufferedReader(InputStreamReader(inputStream, "UTF-8"))
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
            it.onComplete()
            bufferedReader.close()
            inputStream.close()
        }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeBy { list[i++].text = it }
    }

}