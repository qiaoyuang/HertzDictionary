package com.w10group.hertzdictionary.biz.ui.statistics

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.w10group.hertzdictionary.R
import com.w10group.hertzdictionary.manager.CurveValue
import com.w10group.hertzdictionary.manager.DateManagerService
import com.w10group.hertzdictionary.manager.MostValue
import com.w10group.hertzdictionary.biz.manager.WordManagerServiceV3
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * 统计 ViewModel
 * @author Qiao
 */

class StatisticsViewModel(application: Application) : AndroidViewModel(application) {

    private val updateChannel = Channel<Int>()

    val uiUpdateData = liveData {
        for (count in updateChannel) emit(when (count) {
            7 -> selected(7) { DateManagerService.createWeekValue(*it) }
            30 -> selected(30) { DateManagerService.createMonthValue(*it) }
            else -> throw IllegalArgumentException("参数传入错误")
        })
    }

    private suspend inline fun selected(count: Int, crossinline create: (Array<LocalWord>) -> CurveValue): UIUpdateData = withContext(Dispatchers.Default) {
        val context = getApplication<Application>()
        val (timeList, valueList, mostResult) = create(WordManagerServiceV3.getAllLocalWord(context).toTypedArray())
        val totalCount = valueList.sum()
        val totalCountText = context.getString(R.string.last_required, count, totalCount)
        val averageCount = totalCount / count
        val averageCountText = context.getString(R.string.average_required, averageCount)
        val mostWordText = mostResult.toText()
        UIUpdateData(totalCountText, averageCountText, mostWordText, timeList, valueList)
    }

    private fun MostValue.toText(): String = StringBuilder().apply {
        val context = getApplication<Application>()
        append(context.getString(R.string.most_required))
        first.forEachIndexed { index, localWord ->
            append(if (index == 0) localWord.en else "、${localWord.en}")
        }
        append(context.getString(R.string.total_required, second))
    }.toString()

    fun weekSelected() = viewModelScope.launch {
        updateChannel.send(7)
    }

    fun monthSelected() = viewModelScope.launch {
        updateChannel.send(30)
    }

}