package com.w10group.hertzdictionary.biz.bean

import kotlinx.serialization.Optional
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AlternativeTranslations(@SerialName("alternative")
                                   @Optional val words: List<Alternative>? = null)