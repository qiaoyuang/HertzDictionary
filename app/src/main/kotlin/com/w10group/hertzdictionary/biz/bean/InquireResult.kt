package com.w10group.hertzdictionary.biz.bean

import kotlinx.serialization.Optional
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Created by Administrator on 2018/6/15.
 * 查询结果数据类
 */

@Serializable
data class InquireResult(@SerialName("sentences")
                         @Optional val word: List<Word>? = null,
                         @SerialName("dict")
                         @Optional val dict: List<Dict>? = null,
                         @SerialName("alternative_translations")
                         @Optional val alternativeTranslations: List<AlternativeTranslations>? = null,
                         @SerialName("related_words")
                         @Optional val relatedWords: RelatedWords? = null)