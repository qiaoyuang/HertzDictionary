package com.w10group.hertzdictionary.biz.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

/**
 * Localword 数据库
 * @author Qiao
 */

@Database(entities = [LocalWord::class], version = 1)
@TypeConverters(DateConverter::class)
abstract class LocalWordDatabase : RoomDatabase() {

    abstract fun localWordDAO(): LocalWordDAO

    companion object {

        private const val DATABASE_NAME = "WordStore"

        private lateinit var dao: LocalWordDAO

        fun getDAO(context: Context): LocalWordDAO {
            if (!this::dao.isInitialized)
                dao = Room.databaseBuilder(context,
                        LocalWordDatabase::class.java,
                        DATABASE_NAME).build().localWordDAO()
            return dao
        }

    }

}