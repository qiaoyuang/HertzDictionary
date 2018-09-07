package com.w10group.hertzdictionary.biz.bean

import org.litepal.crud.LitePalSupport

data class LocalWord(val id: Int = 0,
                     val ch: String = "",
                     val en: String = "",
                     var count: Int = 1) : LitePalSupport()