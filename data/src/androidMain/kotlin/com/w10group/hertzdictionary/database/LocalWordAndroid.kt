package com.w10group.hertzdictionary.database

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Created by Administrator on 2018/6/29.
 * 本地单词类，用于本地数据库存储
 */

@Entity
actual data class LocalWord(@PrimaryKey actual var en: String = "",
                            actual var ch: String = "",
                            actual var count: Int = 1,
                            actual var timeList: MutableList<Long> = mutableListOf())