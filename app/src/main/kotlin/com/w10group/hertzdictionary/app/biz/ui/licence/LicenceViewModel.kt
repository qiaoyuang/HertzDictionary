package com.w10group.hertzdictionary.app.biz.ui.licence

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.w10group.hertzdictionary.manager.readOpenSourceFile
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch

/**
 * 开源许可 ViewModel
 * @author Qiao
 */
class LicenceViewModel : ViewModel() {

    private val updateChannel = Channel<Unit>()

    val kvList = liveData {
        for (msg in updateChannel)
            emit(readOpenSourceFile())
    }

    fun updateData() = viewModelScope.launch {
        updateChannel.send(Unit)
    }

}