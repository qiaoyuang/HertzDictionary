package com.w10group.hertzdictionary.biz.bean

import kotlinx.serialization.Optional
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Dict(@SerialName("pos")
                @Optional val posType: String = "",
                @SerialName("entry")
                @Optional val dictInfo: List<DictInfo>? = null)