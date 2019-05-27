package com.w10group.hertzdictionary.biz.ui.statistics

import android.os.Bundle
import android.view.MenuItem
import com.w10group.hertzdictionary.core.CoroutineScopeActivity
import org.jetbrains.anko.setContentView

class StatisticsActivity : CoroutineScopeActivity() {

    private val mStatisticsActivityUI = StatisticsActivityUI(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mStatisticsActivityUI.setContentView(this)
        mStatisticsActivityUI.initToolbar()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }

}