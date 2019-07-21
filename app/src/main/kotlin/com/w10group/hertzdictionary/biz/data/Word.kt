package com.w10group.hertzdictionary.biz.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Created by Administrator on 2018/6/15 0030.
 * 单词数据类
 */

@Serializable
data class Word(@SerialName("trans") val ch: String = "",
                @SerialName("orig") val en: String = "",
                @SerialName("translit") val pronunciation: String = "",
                @SerialName("src_translit") val srcPronunciation: String = "")