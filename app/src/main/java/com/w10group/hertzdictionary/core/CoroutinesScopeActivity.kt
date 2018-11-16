package com.w10group.hertzdictionary.core

import android.support.v7.app.AppCompatActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job

abstract class CoroutinesScopeActivity : AppCompatActivity(), CoroutineScope {

    private val job = Job()

    override val coroutineContext = Dispatchers.IO + job

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }
}