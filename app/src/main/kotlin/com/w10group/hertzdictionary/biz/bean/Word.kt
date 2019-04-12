package com.w10group.hertzdictionary.biz.bean

import kotlinx.serialization.Optional
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Created by Administrator on 2018/6/15 0030.
 * 单词数据类
 */

@Serializable
data class Word(@SerialName("trans")
                @Optional val ch: String = "",
                @SerialName("orig")
                @Optional val en: String = "",
                @SerialName("translit")
                @Optional val pronunciation: String = "",
                @SerialName("src_translit")
                @Optional val srcPronunciation: String = "")