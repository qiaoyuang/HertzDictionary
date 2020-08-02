package com.w10group.hertzdictionary.app.core.view

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.util.TypedValue
import android.view.ViewManager
import android.widget.ImageView
import androidx.annotation.DrawableRes
import androidx.appcompat.widget.AppCompatSpinner
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import coil.api.load
import coil.request.CachePolicy
import coil.request.LoadRequestBuilder
import coil.request.RequestBuilder
import coil.request.RequestDisposable
import coil.size.Scale
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView
import com.w10group.hertzdictionary.app.R
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.coroutines.Dispatchers
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

fun <T : RequestBuilder<T>> RequestBuilder<T>.getCoilDefaultConfig(): RequestBuilder<T> {
    dispatcher(Dispatchers.IO)
    allowHardware(true)
    allowRgb565(true)
    bitmapConfig(Bitmap.Config.RGB_565)
    memoryCachePolicy(CachePolicy.ENABLED)
    diskCachePolicy(CachePolicy.ENABLED)
    networkCachePolicy(CachePolicy.ENABLED)
    scale(Scale.FILL)
    return this
}

fun getDefaultCoilBuilder(lifecycle: Lifecycle): LoadRequestBuilder.() -> Unit = {
    crossfade(true)
    lifecycle(lifecycle)
    getCoilDefaultConfig()
}

fun ImageView.loadResId(@DrawableRes resId: Int, lifecycle: Lifecycle): RequestDisposable =
    load(drawableResId = resId, builder = getDefaultCoilBuilder(lifecycle))

fun ImageView.loadURL(url: String, lifecycle: Lifecycle): RequestDisposable =
    load(uri = url, builder = getDefaultCoilBuilder(lifecycle))