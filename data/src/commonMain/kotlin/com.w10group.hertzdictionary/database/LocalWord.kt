package com.w10group.hertzdictionary.database

/**
 * Created by Administrator on 2018/6/29.
 * 本地单词类，用于本地数据库存储
 */

expect class LocalWord() {
    var en: String
    var ch: String
    var count: Int
    var timeList: MutableList<Long>
}