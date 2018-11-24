package com.w10group.hertzdictionary.biz.manager

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.widget.EditText
import com.w10group.hertzdictionary.biz.bean.InquireResult
import kotlinx.coroutines.CoroutineScope

/**
 * Created by Qiao Yuang on 2018/11/20.
 * 用于将Activity和单次管理服务绑定的接口
 */

interface WordDisplayView {
    fun getEditText(): EditText
    fun getRecyclerView(): RecyclerView
    fun getContext(): Context
    fun getCoroutineScope(): CoroutineScope
    suspend fun displayInquireResult(inquireResult: InquireResult, word: String)
    suspend infix fun displayOtherTranslation(words: String)
    suspend infix fun displayRelatedWords(words: String)
}