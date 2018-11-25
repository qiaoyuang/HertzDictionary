package com.w10group.hertzdictionary.biz.bean

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class DictInfo(@Json(name = "word") val word: String = "",
                    @Json(name = "reverse_translation") val reverses: List<String>? = null)