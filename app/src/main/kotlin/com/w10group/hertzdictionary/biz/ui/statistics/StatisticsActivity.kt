package com.w10group.hertzdictionary.biz.ui.statistics

import android.os.Bundle
import android.view.MenuItem
import androidx.lifecycle.Observer
import com.w10group.hertzdictionary.core.architecture.CoroutineScopeActivity

/**
 * 统计 Activity
 * @author Qiao
 */

class StatisticsActivity : CoroutineScopeActivity<StatisticsActivity>() {

    override val uiComponent = StatisticsActivityUIComponent(this)
    override val implementer = this

    init { lifecycle.addObserver(uiComponent) }

    private lateinit var viewModel: StatisticsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = getAndroidViewModel {
            uiUpdateData.observe(implementer, Observer {
                uiComponent.updateUI(it)
            })
        }
    }

    fun weekSelected() = viewModel.weekSelected()

    fun monthSelected() = viewModel.monthSelected()

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }

}