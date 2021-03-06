package com.w10group.hertzdictionary.app.biz.ui.features

import android.os.Bundle
import android.view.MenuItem
import com.w10group.hertzdictionary.app.core.architecture.BaseActivity

/**
 * Created by Administrator on 2018/6/25.
 * 未来新功能 Activity
 */

class FeaturesActivity : BaseActivity<FeaturesActivity>() {

    override val uiComponent = FeaturesActivityUIComponent(this)
    override val implementer = this

    init { lifecycle.addObserver(uiComponent) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getViewModel<FeaturesViewModel> {
            textList.observe(implementer) {
                uiComponent.updateTextView(it)
            }
            kvList.observe(implementer) {
                uiComponent.setTechSelectionData(it)
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