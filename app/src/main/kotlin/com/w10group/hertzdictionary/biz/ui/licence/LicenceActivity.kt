package com.w10group.hertzdictionary.biz.ui.licence

import android.os.Bundle
import android.view.MenuItem
import com.w10group.hertzdictionary.biz.manager.readFileToKV
import com.w10group.hertzdictionary.core.architecture.CoroutineScopeActivity
import kotlinx.coroutines.launch

/**
 * Created by Administrator on 2018/6/26.
 * 开源许可证Activity
 */

class LicenceActivity : CoroutineScopeActivity<LicenceActivity>() {

    private companion object {
        const val OPEN_SOURCE_FILE_NAME = "licence.txt"
    }

    override val uiComponent = LicenceActivityUIComponent(this)
    override val implementer = this

    init { lifecycle.addObserver(uiComponent) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        launch {
            val data = readFileToKV(this@LicenceActivity, OPEN_SOURCE_FILE_NAME)
            uiComponent.setAdapter(OSLAdapter(this@LicenceActivity, data))
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }

}