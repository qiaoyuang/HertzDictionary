package com.w10group.hertzdictionary.database

import androidx.room.TypeConverter
import com.w10group.hertzdictionary.core.KJson
import kotlinx.serialization.UnstableDefault
import kotlinx.serialization.builtins.list
import kotlinx.serialization.builtins.serializer

/**
 * 转换器，用于序列化以及反序列化时间 List
 * @author Qiao
 */

@OptIn(UnstableDefault::class)
internal class DateConverter {

    private val serializer = Long.serializer().list

    @TypeConverter
    fun serialization(list: MutableList<Long>): String = KJson.stringify(serializer, list)

    @TypeConverter
    fun deserialization(str: String): MutableList<Long> = KJson.parse(serializer, str).toMutableList()

}