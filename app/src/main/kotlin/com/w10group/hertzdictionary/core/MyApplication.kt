package com.w10group.hertzdictionary.core

import android.app.Application
import android.content.Context

/**
 * Application
 * @author Qiao
 */

class MyApplication : Application() {

    companion object {

        lateinit var context: Context
            private set

    }

    override fun onCreate() {
        super.onCreate()
        context = applicationContext
    }
}