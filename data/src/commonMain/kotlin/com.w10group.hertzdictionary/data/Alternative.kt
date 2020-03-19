package com.w10group.hertzdictionary.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Alternative(@SerialName("word_postproc") val word: String = "")