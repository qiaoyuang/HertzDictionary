package com.w10group.hertzdictionary.app.biz.ui.features

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.w10group.hertzdictionary.manager.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch

/**
 * 未来新功能 ViewModel
 * @author qiaoyuang
 */

class FeaturesViewModel : ViewModel() {

    private val textListChannel = Channel<Unit>()

    val textList = liveData {
        for (msg in textListChannel)
            emit(readFeatureFile())
    }

    private val kvListChannel = Channel<Unit>()

    val kvList = liveData {
        for (msg in kvListChannel)
            emit(readTechFile())
    }

    fun updateData() {
        viewModelScope.launch {
            textListChannel.send(Unit)
        }
        viewModelScope.launch {
            kvListChannel.send(Unit)
        }
    }

}