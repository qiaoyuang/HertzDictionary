package com.w10group.hertzdictionary.biz.bean

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Dict(@Json(name = "pos") val posType: String = "",
                @Json(name = "entry") val dictInfo: List<DictInfo>? = null)