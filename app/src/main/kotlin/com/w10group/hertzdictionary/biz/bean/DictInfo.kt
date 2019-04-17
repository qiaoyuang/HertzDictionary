package com.w10group.hertzdictionary.biz.bean

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DictInfo(@SerialName("word") val word: String = "",
                    @SerialName("reverse_translation") val reverses: List<String>? = null)