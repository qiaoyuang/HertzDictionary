package com.w10group.hertzdictionary.biz.bean

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * Created by Administrator on 2018/6/15.
 * 查询结果数据类
 */

@JsonClass(generateAdapter = true)
data class InquireResult(@Json(name = "sentences") val word: List<Word>? = null,
                         @Json(name = "dict") val dict: List<Dict>? = null,
                         @Json(name = "alternative_translations") val alternativeTranslations: List<AlternativeTranslations>? = null,
                         @Json(name = "related_words") val relatedWords: RelatedWords? = null)