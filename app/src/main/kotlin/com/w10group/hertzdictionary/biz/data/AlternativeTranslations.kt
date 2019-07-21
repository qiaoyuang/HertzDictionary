package com.w10group.hertzdictionary.biz.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AlternativeTranslations(@SerialName("alternative") val words: List<Alternative>? = null)