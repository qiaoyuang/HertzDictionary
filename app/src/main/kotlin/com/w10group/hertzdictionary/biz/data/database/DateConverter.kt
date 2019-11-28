package com.w10group.hertzdictionary.biz.data.database

import androidx.room.TypeConverter
import kotlinx.serialization.internal.LongSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.list

/**
 * 转换器，用于序列化以及反序列化时间 List
 * @author Qiao
 */

class DateConverter {

    private val serializer = LongSerializer.list

    @TypeConverter
    fun serialization(list: MutableList<Long>): String = Json.nonstrict.stringify(serializer, list)

    @TypeConverter
    fun deserialization(str: String): MutableList<Long> = Json.nonstrict.parse(serializer, str).toMutableList()

}