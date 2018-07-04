package com.w10group.hertzdictionary.core

import android.content.Context
import android.graphics.drawable.Drawable
import android.support.v4.content.ContextCompat
import android.support.v7.widget.AppCompatSpinner
import android.util.TypedValue
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

//创建触摸反馈效果Drawable
fun createTouchFeedback(context: Context): Drawable? {
    val typedValue = TypedValue()
    context.theme.resolveAttribute(android.R.attr.selectableItemBackground, typedValue, true)
    return ContextCompat.getDrawable(context, typedValue.resourceId)
}

//创建触摸反馈效果Drawable(超出边界)
fun createTouchFeedbackBorderless(context: Context): Drawable? {
    val typedValue = TypedValue()
    context.theme.resolveAttribute(android.R.attr.selectableItemBackgroundBorderless, typedValue, true)
    return ContextCompat.getDrawable(context, typedValue.resourceId)
}

object ActionBarSize {

    private var mSize = 0

    fun get(context: Context):Int =
            if (mSize == 0) {
                val styledAttributes = context.applicationContext
                        .obtainStyledAttributes(intArrayOf(android.R.attr.actionBarSize))
                val actionBarSize = styledAttributes.getDimension(0, 0f).toInt()
                styledAttributes.recycle()
                mSize = actionBarSize
                mSize
            } else mSize

}