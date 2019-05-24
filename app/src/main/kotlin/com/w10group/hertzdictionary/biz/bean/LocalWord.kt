package com.w10group.hertzdictionary.biz.bean

import org.litepal.crud.LitePalSupport

/**
 * Created by Administrator on 2018/6/29.
 * 本地单词类，用于本地数据库存储
 */

data class LocalWord(val id: Int = 0,
                     val ch: String = "",
                     val en: String = "",
                     var count: Int = 1,
                     var timeList: ArrayList<Long>? = null) : LitePalSupport()