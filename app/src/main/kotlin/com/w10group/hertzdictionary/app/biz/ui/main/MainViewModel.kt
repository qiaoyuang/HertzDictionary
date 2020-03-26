package com.w10group.hertzdictionary.app.biz.ui.main

import androidx.lifecycle.*
import com.w10group.hertzdictionary.app.biz.ui.main.MainActivity.Companion.CURVE_STATUS_MONTH
import com.w10group.hertzdictionary.app.biz.ui.main.MainActivity.Companion.CURVE_STATUS_WEEK
import com.w10group.hertzdictionary.manager.DateManagerService
import com.w10group.hertzdictionary.manager.WordManagerService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * 主页面 ViewModel
 * @author qiaoyuang
 */

@OptIn(ExperimentalCoroutinesApi::class)
class MainViewModel : ViewModel() {

    private val allWordListChannel = Channel<Unit>()

    val allWordList = liveData {
        for (msg in allWordListChannel) {
            val wordList = WordManagerService.getAllLocalWord()
            emit(wordList)
        }
    }

    fun updateAllWordList() = viewModelScope.launch {
        allWordListChannel.send(Unit)
    }

    private val inquireResultChannel = Channel<String>()

    val inquireResult = liveData {
        for (word in inquireResultChannel) {
            val inquireResult = try {
                InquireResponseSuccess(WordManagerService.inquire(word), word)
            } catch (e: Exception) {
                e.printStackTrace()
                InquireResponseError(e)
            }
            emit(inquireResult)
        }
    }

    val otherTranslationAndRelateWords = inquireResult.asFlow().map {
        when (it) {
            is InquireResponseSuccess -> WordManagerService.getOtherTranslationAndRelateWords(it.inquireResult)
            is InquireResponseError -> null
        }
    }.buffer().flowOn(Dispatchers.Default).asLiveData(viewModelScope.coroutineContext)


    val recyclerViewAdjustmentCoordinate = inquireResult.asFlow().map {
        when (it) {
            is InquireResponseSuccess -> WordManagerService.updateRecyclerViewData(it.inquireResult)
            is InquireResponseError -> null
        }
    }.buffer().flowOn(Dispatchers.Default).asLiveData(viewModelScope.coroutineContext)


    fun sendInquireMsg(word: String) = viewModelScope.launch {
        inquireResultChannel.send(word)
    }

    private val curveUpdateChannel = Channel<Int>()

    val curveData = liveData {
        for (msg in curveUpdateChannel) WordManagerService.currentLocalWord?.let {
            withContext(Dispatchers.Default) {
                val curveValue = when (msg) {
                    CURVE_STATUS_WEEK -> DateManagerService.createWeekValue(it)
                    CURVE_STATUS_MONTH -> DateManagerService.createMonthValue(it)
                    else -> throw IllegalStateException("参数传入错误")
                }
                emit(curveValue)
            }
        }
    }

    fun updateCurveData(curveStatus: Int) = viewModelScope.launch {
        curveUpdateChannel.send(curveStatus)
    }

}