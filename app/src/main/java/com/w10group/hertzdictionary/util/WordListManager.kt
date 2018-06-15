package com.w10group.hertzdictionary.util

import android.content.Context
import android.support.v7.widget.RecyclerView.Adapter
import android.support.v7.widget.RecyclerView.ViewHolder
import android.view.View
import com.w10group.hertzdictionary.bean.Word

abstract class WordListManager(val mContext: Context,
                               val mView: View,
                               val mAdapter: Adapter<out ViewHolder>,
                               val mData: MutableList<Word>,
                               var index: Int) {

    abstract fun getWordList(model: Int)
}

internal const val FIRST = 1
internal const val LOAD_MORE = 2