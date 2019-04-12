package com.w10group.hertzdictionary.biz.bean

import kotlinx.serialization.Optional
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Alternative(@SerialName("word_postproc")
                       @Optional val word: String = "")