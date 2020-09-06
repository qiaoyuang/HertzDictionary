package com.w10group.hertzdictionary.database

/**
 * LocalWord 的 DAO
 * @author Qiao
 */

expect object LocalWordDAO {

    suspend fun insert(vararg localWord: LocalWord)

    suspend fun delete(vararg localWord: LocalWord)

    suspend fun queryAll(): List<LocalWord>

    suspend fun update(vararg localWord: LocalWord)

}