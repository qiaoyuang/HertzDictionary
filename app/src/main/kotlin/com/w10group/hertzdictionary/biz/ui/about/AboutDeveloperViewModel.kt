package com.w10group.hertzdictionary.biz.ui.about

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.w10group.hertzdictionary.biz.manager.readFileToString
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch

/**
 * 关于开发者 ViewModel
 * @author Qiao
 */

class AboutDeveloperViewModel(application: Application) : AndroidViewModel(application) {

    private companion object {
        const val ABOUT_ME_FILE_NAME = "about_me.txt"
    }

    private val updateChannel = Channel<Unit>()

    val textList = liveData {
        for (msg in updateChannel)
            emit(fetchData())
    }

    private suspend fun fetchData(): List<String> = readFileToString(getApplication(), ABOUT_ME_FILE_NAME)

    fun updateData() = viewModelScope.launch {
        updateChannel.send(Unit)
    }

}