package com.w10group.hertzdictionary.biz.ui.licence

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.w10group.hertzdictionary.biz.manager.KV
import com.w10group.hertzdictionary.biz.manager.readFileToKV
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch

/**
 * 开源许可 ViewModel
 * @author Qiao
 */
class LicenceViewModel(application: Application) : AndroidViewModel(application) {

    private companion object {
        const val OPEN_SOURCE_FILE_NAME = "licence.txt"
    }

    private val updateChannel = Channel<Unit>()

    val kvList = liveData {
        for (msg in updateChannel)
            emit(fetchData())
    }

    private suspend fun fetchData(): List<KV> = readFileToKV(getApplication(), OPEN_SOURCE_FILE_NAME)

    fun updateData() = viewModelScope.launch {
        updateChannel.send(Unit)
    }

}