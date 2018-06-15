package com.w10group.hertzdictionary.bean

import com.google.gson.annotations.SerializedName

data class Alternative(@SerializedName("word_postproc") val word: String = "")