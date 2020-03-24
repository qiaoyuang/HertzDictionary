package com.w10group.hertzdictionary.database

/**
 * Created by Administrator on 2018/6/29.
 * 本地单词类，用于本地数据库存储
 */

actual data class LocalWord actual constructor(actual var en: String,
                                               actual var ch: String,
                                               actual var count: Int,
                                               actual var timeList: MutableList<Long>)