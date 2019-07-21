package com.w10group.hertzdictionary.biz.data.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.w10group.hertzdictionary.core.MyApplication

/**
 * Localword 数据库
 * @author Qiao
 */

@Database(entities = [LocalWord::class], version = 1)
abstract class LocalWordDatabase : RoomDatabase() {

    abstract fun localWordDAO(): LocalWordDAO

    companion object {
        val db = Room
                .databaseBuilder(MyApplication.context,
                        LocalWordDatabase::class.java,
                        "WordStore")
                .build().localWordDAO()
    }

}