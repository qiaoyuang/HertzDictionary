package com.w10group.hertzdictionary.biz.bean

import com.google.gson.annotations.SerializedName

data class Dict(@SerializedName("pos") val posType: String = "",
                @SerializedName("entry") val dictInfo: List<DictInfo>? = null)