package com.w10group.hertzdictionary.business.bean

import com.google.gson.annotations.SerializedName

/**
 * Created by Administrator on 2018/6/15.
 * 查询结果数据类
 */

data class InquireResult(@SerializedName("sentences") val word: List<Word>? = null,
                         @SerializedName("dict") val dict: List<Dict>? = null,
                         @SerializedName("alternative_translations") val alternativeTranslations: List<AlternativeTranslations>? = null,
                         @SerializedName("related_words") val relatedWords: RelatedWords? = null)