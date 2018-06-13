package com.w10group.hertzdictionary.model

import com.google.gson.annotations.SerializedName

/**
 * Created by Administrator on 2018/1/30 0030.
 * 单词数据类
 */

data class InquireResult(@SerializedName("sentences")val words: List<Word>? = null,
                         @SerializedName("src")val source: String = "en")

data class Word(@SerializedName("trans")val ch: String = "",
                @SerializedName("orig")val en: String = "",
                @SerializedName("backend")var backend: Int)