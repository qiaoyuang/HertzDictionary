package com.w10group.hertzdictionary.biz.ui.features

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.w10group.hertzdictionary.biz.manager.readFileToKV
import com.w10group.hertzdictionary.biz.manager.readFileToString
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch

/**
 * 未来新功能 ViewModel
 * @author qiaoyuang
 */

class FeaturesViewModel(application: Application) : AndroidViewModel(application) {

    private companion object {
        const val FEATURE_FILE_NAME = "feature.txt"
        const val TECH_FILE_NAME = "technology.txt"
    }

    object UpdateMsg

    private val textListChannel = Channel<UpdateMsg>()

    val textList = liveData {
        for (msg in textListChannel)
            emit(fetchStringData())
    }

    private suspend fun fetchStringData() = readFileToString(getApplication(), FEATURE_FILE_NAME)

    private val kvListChannel = Channel<UpdateMsg>()

    val kvList = liveData {
        for (msg in kvListChannel)
            emit(fetchKVData())
    }

    private suspend fun fetchKVData() = readFileToKV(getApplication(), TECH_FILE_NAME)

    fun updateData() {
        viewModelScope.launch {
            textListChannel.send(UpdateMsg)
        }
        viewModelScope.launch {
            kvListChannel.send(UpdateMsg)
        }
    }

}