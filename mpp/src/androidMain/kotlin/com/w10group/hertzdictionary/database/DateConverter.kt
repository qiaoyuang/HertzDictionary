package com.w10group.hertzdictionary.database

import androidx.room.TypeConverter
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.serializer
import com.w10group.hertzdictionary.core.KJson

/**
 * 转换器，用于序列化以及反序列化时间 List
 * @author Qiao
 */

internal class DateConverter {

    private val serializer = ListSerializer(Long.serializer())

    @TypeConverter
    fun serialization(list: MutableList<Long>): String = KJson.encodeToString(serializer, list)

    @TypeConverter
    fun deserialization(str: String): MutableList<Long> = KJson.decodeFromString(serializer, str).toMutableList()

}