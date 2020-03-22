package com.w10group.hertzdictionary.core

import android.content.Context

/**
 * 用于在模块初始化的时候从 UI 层传入 Application Context 对象
 * @author qiaoyuang
 */

object DataModule {

    internal lateinit var appContext: Context

    fun init(context: Context) {
        appContext = context
    }

}