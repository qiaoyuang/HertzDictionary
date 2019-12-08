package com.w10group.hertzdictionary.biz.ui.about

import android.os.Bundle
import android.view.MenuItem
import com.w10group.hertzdictionary.biz.manager.ImageManagerService
import com.w10group.hertzdictionary.biz.manager.readFileToString
import com.w10group.hertzdictionary.core.architecture.CoroutineScopeActivity
import kotlinx.coroutines.launch

/**
 * Created by Administrator on 2018/6/25.
 * 关于开发者Activity
 */

class AboutDeveloperActivity : CoroutineScopeActivity<AboutDeveloperActivity>() {

    private companion object {
        const val ABOUT_ME_FILE_NAME = "about_me.txt"
    }

    override val uiComponent = AboutDeveloperActivityUIComponent(this)
    override val implementer = this

    init { lifecycle.addObserver(uiComponent) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        launch {
            ImageManagerService.loadBackground(implementer, uiComponent.mIMBackground)
        }
        launch {
            val list = readFileToString(implementer, ABOUT_ME_FILE_NAME)
            uiComponent.updateTextView(list)
        }
        ImageManagerService.loadAvatar(this, uiComponent.mIMAvatar)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) =
        uiComponent.requestPermissionsResult(requestCode, grantResults)

}