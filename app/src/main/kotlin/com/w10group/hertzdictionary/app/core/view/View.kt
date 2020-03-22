package com.w10group.hertzdictionary.app.core.view

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.TypedValue
import android.view.ViewManager
import androidx.appcompat.widget.AppCompatSpinner
import androidx.core.content.ContextCompat
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView
import com.w10group.hertzdictionary.app.R
import de.hdodenhof.circleimageview.CircleImageView
import org.jetbrains.anko.attr
import org.jetbrains.anko.custom.ankoView

/**
 * Created by Administrator on 2018/1/30 0030.
 * 自定义的适用于Anko的扩展函数
 */

// AppCompatSpinner
inline fun ViewManager.appCompatSpinner(init: AppCompatSpinner.() -> Unit): AppCompatSpinner =
        ankoView({ AppCompatSpinner(it) }, theme = R.style.SpinnerStyle, init = init)

// CircleImageView
inline fun ViewManager.circleImageView(init: CircleImageView.() -> Unit): CircleImageView =
        ankoView({ CircleImageView(it) }, theme = 0, init = init)

// SubsamplingImageView
inline fun ViewManager.subsamplingImageView(init: SubsamplingScaleImageView.() -> Unit): SubsamplingScaleImageView =
        ankoView({ SubsamplingScaleImageView(it) }, theme = 0, init = init)

// CurveView
inline fun ViewManager.curveView(init: CurveView.() -> Unit = {}): CurveView =
        ankoView({ CurveView(it) }, theme = 0, init = init)

// 创建触摸反馈效果 Drawable
fun createTouchFeedback(context: Context): Drawable? {
    val typedValue = TypedValue()
    context.theme.resolveAttribute(android.R.attr.selectableItemBackground, typedValue, true)
    return ContextCompat.getDrawable(context, typedValue.resourceId)
}

// 创建触摸反馈效果 Drawable（超出边界）
fun createTouchFeedbackBorderless(context: Context): Drawable? {
    val typedValue = TypedValue()
    context.theme.resolveAttribute(android.R.attr.selectableItemBackgroundBorderless, typedValue, true)
    return ContextCompat.getDrawable(context, typedValue.resourceId)
}

// 获取系统的 ActionBarSize
fun getActionBarSize(context: Context): Int {
    val typedValue = context.attr(android.R.attr.actionBarSize).data
    return TypedValue.complexToDimension(typedValue, context.resources.displayMetrics).toInt()
}

// 获取系统 StatusBarSize
fun getStatusBarSize(context: Context): Int {
    val resourceId = context.resources.getIdentifier("status_bar_height", "dimen", "android")
    return context.resources.getDimensionPixelSize(resourceId)
}