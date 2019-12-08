package com.w10group.hertzdictionary.biz.ui.features

import android.content.Context
import android.view.Gravity
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import com.w10group.hertzdictionary.biz.manager.KV
import com.w10group.hertzdictionary.core.view.KVViewHolder
import com.w10group.hertzdictionary.core.view.KVViewHolder.Companion.CONTENT_ID
import com.w10group.hertzdictionary.core.view.KVViewHolder.Companion.TITLE_ID
import org.jetbrains.anko.*

/**
 * 技术选型 Adapter
 * @author Qiao
 */

class TechSelectionAdapter(private val mContext: Context,
                           private val mData: List<KV>) : Adapter<KVViewHolder>() {

    override fun getItemCount(): Int = mData.size

    override fun onBindViewHolder(holder: KVViewHolder, position: Int) = with(holder) {
        val data = mData[position]
        tvTitle.text = data.first
        tvContent.text = data.second
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): KVViewHolder =
            KVViewHolder(mContext.UI {
                frameLayout {
                    lparams(matchParent, wrapContent) {
                        topMargin = dip(2)
                        bottomMargin = dip(2)
                    }
                    textView {
                        id = TITLE_ID
                        textSize = 16f
                    }.lparams(wrapContent, wrapContent) {
                        gravity = Gravity.START
                    }
                    textView {
                        id = CONTENT_ID
                        textSize = 16f
                    }.lparams(wrapContent, wrapContent) {
                        gravity = Gravity.END
                    }
                }
            }.view)

}