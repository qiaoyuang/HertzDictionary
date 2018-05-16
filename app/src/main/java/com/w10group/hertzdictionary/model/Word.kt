package com.w10group.hertzdictionary.model

import com.google.gson.annotations.SerializedName

/**
 * Created by Administrator on 2018/1/30 0030.
 * 单词数据类
 */
data class Word(@SerializedName("word_english") val english: String,
                @SerializedName("word_chinese") val chinese: String,
                @SerializedName("word_count") var count: Int)