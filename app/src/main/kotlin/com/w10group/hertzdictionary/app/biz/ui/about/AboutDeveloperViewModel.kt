package com.w10group.hertzdictionary.app.biz.ui.about

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.w10group.hertzdictionary.manager.readAboutMeFile
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch

/**
 * 关于开发者 ViewModel
 * @author Qiao
 */

class AboutDeveloperViewModel : ViewModel() {

    private val updateChannel = Channel<Unit>()

    val textList = liveData {
        for (msg in updateChannel)
            emit(readAboutMeFile())
    }

    fun updateData() = viewModelScope.launch {
        updateChannel.send(Unit)
    }

}