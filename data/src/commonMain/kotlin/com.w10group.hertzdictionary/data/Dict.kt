package com.w10group.hertzdictionary.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Dict(@SerialName("pos") val posType: String = "",
                @SerialName("entry") val dictInfo: List<DictInfo>? = null)