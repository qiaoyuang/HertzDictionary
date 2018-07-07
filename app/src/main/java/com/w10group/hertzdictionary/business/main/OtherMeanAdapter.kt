package com.w10group.hertzdictionary.business.main

import android.content.Context
import android.os.Build
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView.Adapter
import android.support.v7.widget.RecyclerView.ViewHolder
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.w10group.hertzdictionary.R
import com.w10group.hertzdictionary.business.bean.DictInfo
import com.w10group.hertzdictionary.business.main.OtherMeanAdapter.OtherMeanViewHolder
import com.w10group.hertzdictionary.core.createTouchFeedback
import org.jetbrains.anko.*

class OtherMeanAdapter(private val mContext: Context,
                       private val mData: List<DictInfo>) : Adapter<OtherMeanViewHolder>() {

    private companion object {
        const val WORD = 1
        const val REVERSE = 2
    }

    private val black by lazy { ContextCompat.getColor(mContext, android.R.color.black) }
    private val gray600 by lazy { ContextCompat.getColor(mContext, R.color.gray600) }

    override fun getItemCount(): Int = mData.size

    override fun onBindViewHolder(holder: OtherMeanViewHolder, position: Int) {
        val data = mData[position]
        holder.tvWord.text = data.word
        val builder = StringBuilder()
        data.reverses?.let {
            val last = it.size - 1
            it.forEachIndexed { index, word ->
                if (index == last) {
                    builder.append(word)
                } else {
                    builder.append("$word, ")
                }
            }
        }
        holder.tvReverse.text = builder.toString()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OtherMeanViewHolder {
        val view = AnkoContext.create(mContext).apply {
            verticalLayout {
                lparams(matchParent, wrapContent)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    isClickable = true
                    foreground = createTouchFeedback(mContext)
                }
                textView {
                    id = WORD
                    textColor = black
                    textSize = 14f
                }.lparams(wrapContent, wrapContent)
                textView {
                    id = REVERSE
                    textColor = gray600
                    textSize = 14f
                }.lparams(wrapContent, wrapContent) {
                    bottomMargin = dip(8)
                }
            }
        }.view
        return OtherMeanViewHolder(view)
    }

    class OtherMeanViewHolder(itemView: View) : ViewHolder(itemView) {
        val tvWord = itemView.find<TextView>(WORD)
        val tvReverse = itemView.find<TextView>(REVERSE)
    }

}