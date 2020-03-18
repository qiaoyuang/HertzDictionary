package com.w10group.hertzdictionary.core.architecture

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory
import org.jetbrains.anko.AnkoComponent
import org.jetbrains.anko.setContentView

/**
 * 包含协程上下文的 Activity
 * @author Qiao
 */

abstract class BaseActivity<T : BaseActivity<T>> : AppCompatActivity() {

    abstract val uiComponent: AnkoComponent<T>

    abstract val implementer: T

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        uiComponent.setContentView(implementer)
    }

    protected inline fun <reified T : ViewModel> getViewModel(configLiveData: T.() -> Unit): T =
            ViewModelProvider(implementer)[T::class.java].apply {
                configLiveData()
            }

    protected inline fun <reified T : AndroidViewModel> getAndroidViewModel(configLiveData: T.() -> Unit): T =
            ViewModelProvider(implementer, AndroidViewModelFactory(application))[T::class.java].apply {
                configLiveData()
            }

}