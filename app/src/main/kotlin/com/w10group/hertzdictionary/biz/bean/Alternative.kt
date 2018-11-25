package com.w10group.hertzdictionary.biz.bean

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Alternative(@Json(name = "word_postproc") val word: String = "")