package com.w10group.hertzdictionary.biz.data.database

import androidx.room.*

/**
 * LocalWord 的 DAO
 * @author Qiao
 */

@Dao
interface LocalWordDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(vararg localWord: LocalWord)

    @Delete
    suspend fun delete(vararg localWord: LocalWord)

    @Query("SELECT * FROM LocalWord ORDER BY count DESC")
    suspend fun queryAll(): List<LocalWord>

    @Update
    suspend fun update(vararg localWord: LocalWord)

}