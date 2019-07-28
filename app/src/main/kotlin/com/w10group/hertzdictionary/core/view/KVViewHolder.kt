package com.w10group.hertzdictionary.core.view

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import org.jetbrains.anko.find

/**
 * 标题-内容格式的 ViewHolder
 * @author Qiao
 */

class KVViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    companion object {
        const val TITLE_ID = 1
        const val CONTENT_ID = 2
    }

    val tvTitle = itemView.find<TextView>(TITLE_ID)
    val tvContent = itemView.find<TextView>(CONTENT_ID)

}