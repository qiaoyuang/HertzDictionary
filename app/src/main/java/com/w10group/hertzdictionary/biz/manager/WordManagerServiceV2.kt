package com.w10group.hertzdictionary.biz.manager

import com.w10group.hertzdictionary.biz.bean.LocalWord
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.litepal.LitePal

class WordManagerServiceV2 {

    /*fun getAllWordByCoroutines(coroutineScope: CoroutineScope) = coroutineScope.launch {
        val list = LitePal.order("count desc").find(LocalWord::class.java)
        list?.let { mData.addAll(it) }
        withContext(Dispatchers.Main) {
            mRecyclerView.adapter = mAdapter
        }
    }*/

}