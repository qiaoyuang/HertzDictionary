package com.w10group.hertzdictionary.biz.ui.statistics

import android.view.MenuItem
import com.w10group.hertzdictionary.R
import com.w10group.hertzdictionary.biz.bean.LocalWord
import com.w10group.hertzdictionary.biz.manager.CurveValue
import com.w10group.hertzdictionary.biz.manager.DateManagerService
import com.w10group.hertzdictionary.biz.manager.MostValue
import com.w10group.hertzdictionary.biz.manager.WordManagerServiceV3
import com.w10group.hertzdictionary.core.architecture.CoroutineScopeActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * 统计 Activity
 * @author Qiao
 */

class StatisticsActivity : CoroutineScopeActivity<StatisticsActivity>() {

    override val uiComponent = StatisticsActivityUIComponent(this)
    override val implementer = this

    fun weekSelected() = selected(7) { DateManagerService.createWeekValue(*it) }

    fun monthSelected() = selected(30) { DateManagerService.createMonthValue(*it) }

    private inline fun selected(count: Int, crossinline create: (Array<LocalWord>) -> CurveValue) = launch(Dispatchers.Default) {
        val (timeList, valueList, mostResult) = create(WordManagerServiceV3.allLocalWords.toTypedArray())
        val totalCount = valueList.sum()
        val totalCountText = getString(R.string.last_required, count, totalCount)
        val averageCount = totalCount / count
        val averageCountText = getString(R.string.average_required, averageCount)
        val mostWordText = mostResult.toText()
        withContext(Dispatchers.Main) {
            uiComponent.updateUI(totalCountText, averageCountText, mostWordText, timeList, valueList)
        }
    }

    private fun MostValue.toText(): String = StringBuilder().apply {
        append(getString(R.string.most_required))
        first.forEachIndexed { index, localWord ->
            append(if (index == 0) localWord.en else "、${localWord.en}")
        }
        append(getString(R.string.total_required, second))
    }.toString()

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }

}