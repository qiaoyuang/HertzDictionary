package com.w10group.hertzdictionary.util

import android.content.Context
import android.support.v7.widget.RecyclerView.Adapter
import android.support.v7.widget.RecyclerView.ViewHolder
import android.view.View
import com.w10group.hertzdictionary.bean.Word

class LocalWordListManager(context: Context,
                           view: View,
                           adapter: Adapter<ViewHolder>,
                           data: MutableList<Word>,
                           index: Int) : WordListManager(context, view, adapter, data, index) {

    init {

    }


    override fun getWordList(model: Int) {

    }

}