package com.w10group.hertzdictionary.app.biz.ui.main

import com.w10group.hertzdictionary.data.InquireResult

/**
 * 单词查询结果
 * @author qiaoyuang
 */

sealed class InquireResponse

class InquireResponseSuccess(val inquireResult: InquireResult, val word: String) : InquireResponse()

class InquireResponseError(val exception: Exception) : InquireResponse()

class InquireResponseEmpty(val msg: String = "查询完成后立即将 inquireResult 状态还原") : InquireResponse()