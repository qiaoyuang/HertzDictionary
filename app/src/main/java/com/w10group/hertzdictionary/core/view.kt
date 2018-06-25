package com.w10group.hertzdictionary.core

import android.support.v7.widget.AppCompatSpinner
import android.view.ViewManager
import de.hdodenhof.circleimageview.CircleImageView
import org.jetbrains.anko.custom.ankoView

/**
 * Created by Administrator on 2018/1/30 0030.
 * 自定义的适用于Anko的扩展函数
 */

//AppCompatSpinner
inline fun ViewManager.appCompatSpineer(init: AppCompatSpinner.() -> Unit): AppCompatSpinner =
        ankoView({ AppCompatSpinner(it) }, theme = 0, init = init)

//CircleImageView
inline fun ViewManager.circleImageView(init: CircleImageView.() -> Unit): CircleImageView =
        ankoView({ CircleImageView(it) }, theme = 0, init = init)