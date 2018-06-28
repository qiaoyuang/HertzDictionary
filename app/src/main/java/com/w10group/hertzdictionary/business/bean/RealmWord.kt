package com.w10group.hertzdictionary.business.bean

import io.realm.RealmObject
import io.realm.annotations.Ignore
import io.realm.annotations.PrimaryKey
import io.realm.annotations.RealmClass

@RealmClass
open class RealmWord(val ch: String = "",
                     @PrimaryKey val en: String = "",
                     @Ignore var isExist: Boolean = true,
                     var count: Int = 1) : RealmObject()