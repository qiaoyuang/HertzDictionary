package com.w10group.hertzdictionary.business.bean

import com.google.gson.annotations.SerializedName

data class DictInfo(@SerializedName("word") val word: String = "",
                    @SerializedName("reverse_translation") val reverses: List<String>? = null)