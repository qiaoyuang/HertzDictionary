package com.w10group.hertzdictionary.core.architecture

import org.jetbrains.anko.AnkoComponent

/**
 * 带 CoroutineScope 的基础 AnkoComponent
 * @author Qiao
 */

abstract class UIComponent<T : CoroutineScopeActivity<T>> : AnkoComponent<T> {

    // 初始化
    open fun init() = Unit

    // 资源回收
    open fun recycler() = Unit

}