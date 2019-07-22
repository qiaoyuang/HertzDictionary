package com.w10group.hertzdictionary.biz.data.database

import androidx.room.TypeConverter
import kotlinx.serialization.internal.ArrayListSerializer
import kotlinx.serialization.internal.LongSerializer
import kotlinx.serialization.json.Json

/**
 * 转换器，用于序列化以及反序列化时间 List
 * @author Qiao
 */

class DateConverter {

    private val serializer = ArrayListSerializer(LongSerializer)

    @TypeConverter
    fun serialization(list: ArrayList<Long>): String = Json.nonstrict.stringify(serializer, list)

    @TypeConverter
    fun deserialization(str: String): ArrayList<Long> = ArrayList(Json.nonstrict.parse(serializer, str))

}