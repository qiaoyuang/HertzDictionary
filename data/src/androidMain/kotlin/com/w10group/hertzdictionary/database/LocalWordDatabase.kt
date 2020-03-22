package com.w10group.hertzdictionary.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

/**
 * LocalWord 数据库
 * @author Qiao
 */

@Database(entities = [LocalWord::class], version = 1)
@TypeConverters(DateConverter::class)
internal abstract class LocalWordDatabase : RoomDatabase() {

    abstract fun localWordDAORoom(): LocalWordDAORoom

    companion object {

        private const val DATABASE_NAME = "WordStore"

        private lateinit var dao: LocalWordDAORoom

        fun getDAO(context: Context): LocalWordDAORoom {
            if (!this::dao.isInitialized)
                dao = Room.databaseBuilder(context,
                        LocalWordDatabase::class.java,
                    DATABASE_NAME
                ).build().localWordDAORoom()
            return dao
        }

    }

}