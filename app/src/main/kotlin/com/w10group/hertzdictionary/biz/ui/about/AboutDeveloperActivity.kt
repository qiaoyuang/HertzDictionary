package com.w10group.hertzdictionary.biz.ui.about

import android.os.Bundle
import android.view.MenuItem
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.w10group.hertzdictionary.biz.manager.ImageManagerService
import com.w10group.hertzdictionary.core.architecture.CoroutineScopeActivity
import kotlinx.coroutines.launch

/**
 * Created by Administrator on 2018/6/25.
 * 关于开发者Activity
 */

class AboutDeveloperActivity : CoroutineScopeActivity<AboutDeveloperActivity>() {

    override val uiComponent = AboutDeveloperActivityUIComponent(this)
    override val implementer = this

    init { lifecycle.addObserver(uiComponent) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycleScope.launch {
            ImageManagerService.loadBackground(implementer, uiComponent.mIMBackground)
        }
        getAndroidViewModel<AboutDeveloperViewModel> {
            textList.observe(implementer, Observer {
                uiComponent.updateTextView(it)
            })
            updateData()
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