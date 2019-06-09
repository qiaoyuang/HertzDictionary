package com.w10group.hertzdictionary.biz.ui.features

import android.os.Bundle
import android.view.MenuItem
import com.w10group.hertzdictionary.biz.manager.readFileAsync
import com.w10group.hertzdictionary.core.architecture.CoroutineScopeActivity
import kotlinx.coroutines.launch

/**
 * Created by Administrator on 2018/6/25.
 * 未来新功能Activity
 */

class FeaturesActivity : CoroutineScopeActivity<FeaturesActivity>() {

    private companion object {
        const val FEATURE_FILE_NAME = "feature.txt"
    }

    override val uiComponent = FeaturesActivityUIComponent(this)
    override val implementer = this

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        launch {
            val deferred = readFileAsync(implementer, FEATURE_FILE_NAME)
            uiComponent.updateTextView(deferred.await())
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray)
            = uiComponent.requestPermissionsResult(requestCode, grantResults)

}