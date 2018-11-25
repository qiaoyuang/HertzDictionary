package com.w10group.hertzdictionary.biz.bean

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * Created by Administrator on 2018/6/15.
 * 查询结果的相关词组
 */


@JsonClass(generateAdapter = true)
data class RelatedWords(@Json(name = "word") val words: List<String>? = null)