package com.w10group.hertzdictionary.database

import com.w10group.hertzdictionary.core.DataModule

/**
 * LocalWord 的 DAO
 * @author Qiao
 */

actual object LocalWordDAO {

    actual suspend fun insert(vararg localWord: LocalWord) = LocalWordDatabase.getDAO(DataModule.appContext).insert(*localWord)

    actual suspend fun delete(vararg localWord: LocalWord) = LocalWordDatabase.getDAO(DataModule.appContext).delete(*localWord)

    actual suspend fun queryAll(): List<LocalWord> = LocalWordDatabase.getDAO(DataModule.appContext).queryAll()

    actual suspend fun update(vararg localWord: LocalWord) = LocalWordDatabase.getDAO(DataModule.appContext).update(*localWord)

}