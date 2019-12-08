package com.w10group.hertzdictionary.core.architecture

import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import org.jetbrains.anko.AnkoComponent

/**
 * 带 CoroutineScope 的基础 AnkoComponent
 * @author Qiao
 */

abstract class UIComponent<T : CoroutineScopeActivity<T>> : AnkoComponent<T>, LifecycleObserver {

    // 初始化
    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    open fun init() {
        Log.d("lifecycle", "初始化")
    }

    // 资源回收
    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    open fun recycler() {
        Log.d("lifecycle", "结束")
    }

}