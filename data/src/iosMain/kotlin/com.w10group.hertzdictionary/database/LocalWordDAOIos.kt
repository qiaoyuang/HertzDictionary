package com.w10group.hertzdictionary.database

/**
 * LocalWord 的 DAO
 * @author Qiao
 */

actual object LocalWordDAO {

    actual suspend fun insert(vararg localWord: LocalWord) = Unit

    actual suspend fun delete(vararg localWord: LocalWord) = Unit

    actual suspend fun queryAll(): List<LocalWord> = listOf()

    actual suspend fun update(vararg localWord: LocalWord) = Unit

}