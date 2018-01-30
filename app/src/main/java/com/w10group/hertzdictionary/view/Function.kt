package com.w10group.hertzdictionary.view

import android.support.v7.widget.AppCompatSpinner
import android.view.ViewManager
import org.jetbrains.anko.custom.ankoView

/**
 * Created by Administrator on 2018/1/30 0030.
 * 自定义的适用于Anko的扩展函数
 */

//AppCompatSpinner
inline fun ViewManager.appCompatSpineer(init: AppCompatSpinner.() -> Unit): AppCompatSpinner =
        ankoView({ AppCompatSpinner(it) }, theme = 0, init = init)