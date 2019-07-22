package com.w10group.hertzdictionary.biz.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Created by Administrator on 2018/6/29.
 * 本地单词类，用于本地数据库存储
 */

@Entity
data class LocalWord(@PrimaryKey var en: String = "",
                     var ch: String = "",
                     var count: Int = 1,
                     var timeList: ArrayList<Long> = ArrayList())