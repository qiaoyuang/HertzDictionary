package com.w10group.hertzdictionary.biz.bean

import kotlinx.serialization.Optional
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DictInfo(@SerialName("word")
                    @Optional val word: String = "",
                    @SerialName("reverse_translation")
                    @Optional val reverses: List<String>? = null)