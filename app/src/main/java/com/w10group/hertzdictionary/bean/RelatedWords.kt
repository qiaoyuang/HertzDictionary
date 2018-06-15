package com.w10group.hertzdictionary.bean

import com.google.gson.annotations.SerializedName

/**
 * Created by Administrator on 2018/6/15.
 * 查询结果的相关词组
 */


data class RelatedWords(@SerializedName("word") val words: List<String>? = null)