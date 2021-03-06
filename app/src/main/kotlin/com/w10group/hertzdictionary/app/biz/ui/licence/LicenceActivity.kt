package com.w10group.hertzdictionary.app.biz.ui.licence

import android.os.Bundle
import android.view.MenuItem
import com.w10group.hertzdictionary.app.core.architecture.BaseActivity

/**
 * Created by Administrator on 2018/6/26.
 * 开源许可证Activity
 */

class LicenceActivity : BaseActivity<LicenceActivity>() {

    override val uiComponent = LicenceActivityUIComponent(this)
    override val implementer = this

    init { lifecycle.addObserver(uiComponent) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getViewModel<LicenceViewModel> {
            kvList.observe(implementer) {
                uiComponent.setAdapter(OSLAdapter(implementer, it))
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

}