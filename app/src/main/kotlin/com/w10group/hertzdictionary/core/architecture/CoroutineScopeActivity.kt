package com.w10group.hertzdictionary.core.architecture

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import org.jetbrains.anko.setContentView
import kotlin.coroutines.CoroutineContext

/**
 * 包含协程上下文的 Activity
 * @author Qiao
 */

abstract class CoroutineScopeActivity<T : CoroutineScopeActivity<T>> : AppCompatActivity(), CoroutineScope {

    abstract val uiComponent: UIComponent<T>

    abstract val implementer: T

    private lateinit var job: Job

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        job = Job()
        uiComponent.setContentView(implementer)
        uiComponent.init()
    }

    override fun onRestart() {
        super.onRestart()
        job = Job()
    }

    override fun onStop() {
        super.onStop()
        job.cancel()
    }

    override fun onDestroy() {
        super.onDestroy()
        uiComponent.recycler()
    }

}