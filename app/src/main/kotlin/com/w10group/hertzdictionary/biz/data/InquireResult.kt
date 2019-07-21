package com.w10group.hertzdictionary.biz.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Created by Administrator on 2018/6/15.
 * 查询结果数据类
 */

@Serializable
data class InquireResult(@SerialName("sentences") val word: List<Word>? = null,
                         @SerialName("dict") val dict: List<Dict>? = null,
                         @SerialName("alternative_translations") val alternativeTranslations: List<AlternativeTranslations>? = null,
                         @SerialName("related_words") val relatedWords: RelatedWords? = null)