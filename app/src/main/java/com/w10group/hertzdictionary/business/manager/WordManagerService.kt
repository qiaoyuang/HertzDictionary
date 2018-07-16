package com.w10group.hertzdictionary.business.manager

import com.w10group.hertzdictionary.business.bean.LocalWord
import com.w10group.hertzdictionary.core.RxBus
import org.litepal.LitePal

object WordManagerService {

    fun getAllWord() {
        RxBus.post(LitePal.order("count desc").find(LocalWord::class.java), RxBus.IO, RxBus.MAIN)
    }

}