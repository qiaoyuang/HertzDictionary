package com.w10group.hertzdictionary.business.bean

import com.google.gson.annotations.SerializedName

/**
 * Created by Administrator on 2018/6/15 0030.
 * 单词数据类
 */

data class Word(@SerializedName("trans") val ch: String = "",
                @SerializedName("orig") val en: String = "",
                @SerializedName("translit") val pronunciation: String = "",
                @SerializedName("src_translit") val srcPronunciation: String = "")