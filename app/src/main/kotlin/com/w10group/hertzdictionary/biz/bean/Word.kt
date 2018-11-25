package com.w10group.hertzdictionary.biz.bean

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * Created by Administrator on 2018/6/15 0030.
 * 单词数据类
 */

@JsonClass(generateAdapter = true)
data class Word(@Json(name = "trans") val ch: String = "",
                @Json(name = "orig") val en: String = "",
                @Json(name = "translit") val pronunciation: String = "",
                @Json(name = "src_translit") val srcPronunciation: String = "")