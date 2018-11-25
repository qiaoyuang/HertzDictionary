package com.w10group.hertzdictionary.biz.bean

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class AlternativeTranslations(@Json(name = "alternative") val words: List<Alternative>? = null)