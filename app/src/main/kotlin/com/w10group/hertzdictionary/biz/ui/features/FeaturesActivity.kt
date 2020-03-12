package com.w10group.hertzdictionary.biz.ui.features

import android.os.Bundle
import android.view.MenuItem
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.w10group.hertzdictionary.core.architecture.CoroutineScopeActivity

/**
 * Created by Administrator on 2018/6/25.
 * 未来新功能 Activity
 */

class FeaturesActivity : CoroutineScopeActivity<FeaturesActivity>() {

    override val uiComponent = FeaturesActivityUIComponent(this)
    override val implementer = this

    init { lifecycle.addObserver(uiComponent) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val factory = ViewModelProvider.AndroidViewModelFactory(application)
        ViewModelProvider(implementer, factory)[FeaturesViewModel::class.java].run  {
            textList.observe(implementer, Observer {
                uiComponent.updateTextView(it)
            })
            kvList.observe(implementer, Observer {
                uiComponent.setTechSelectionData(it)
            })
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