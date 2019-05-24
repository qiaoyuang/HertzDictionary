package com.w10group.hertzdictionary.biz.manager

import android.content.Context
import android.support.v7.widget.RecyclerView
import com.w10group.hertzdictionary.biz.bean.InquireResult
import kotlinx.coroutines.CoroutineScope

/**
 * Created by Qiao Yuang on 2018/11/20.
 * 用于将Activity和单次管理服务绑定的接口
 */

interface WordDisplayView {
    fun snackBar(text: String)
    fun setAdapter(adapter: RecyclerView.Adapter<*>)
    fun setWordText(text: String)
    fun getContext(): Context
    fun getCoroutineScope(): CoroutineScope
    fun displayInquireResult(inquireResult: InquireResult, word: String)
    fun updateCurveView()
    infix fun displayOtherTranslation(words: String)
    infix fun displayRelatedWords(words: String)
}