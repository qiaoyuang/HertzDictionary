package com.w10group.hertzdictionary.biz.bean

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Created by Administrator on 2018/6/15.
 * 查询结果的相关词组
 */

@Serializable
data class RelatedWords(@SerialName("word") val words: List<String>? = null)