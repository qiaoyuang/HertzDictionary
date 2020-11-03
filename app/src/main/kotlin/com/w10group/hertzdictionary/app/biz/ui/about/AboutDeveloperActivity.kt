package com.w10group.hertzdictionary.app.biz.ui.about

import android.os.Bundle
import android.view.MenuItem
import androidx.lifecycle.lifecycleScope
import com.w10group.hertzdictionary.app.biz.manager.ImageManagerService
import com.w10group.hertzdictionary.app.core.architecture.BaseActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Created by Administrator on 2018/6/25.
 * 关于开发者Activity
 */

class AboutDeveloperActivity : BaseActivity<AboutDeveloperActivity>() {

    override val uiComponent = AboutDeveloperActivityUIComponent(this)
    override val implementer = this

    init { lifecycle.addObserver(uiComponent) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycleScope.launch(Dispatchers.Default) {
            ImageManagerService.loadBackground(uiComponent.mIMBackground, lifecycle)
        }
        lifecycleScope.launch {
            ImageManagerService loadAvatar uiComponent.mIMAvatar
        }
        getViewModel<AboutDeveloperViewModel> {
            textList.observe(implementer) {
                uiComponent.updateTextView(it)
            }
            updateData()
        }
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