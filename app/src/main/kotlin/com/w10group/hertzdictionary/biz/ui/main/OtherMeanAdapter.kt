package com.w10group.hertzdictionary.biz.ui.main

import android.content.Context
import android.os.Build
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.w10group.hertzdictionary.R
import com.w10group.hertzdictionary.biz.data.DictInfo
import com.w10group.hertzdictionary.biz.ui.main.OtherMeanAdapter.OtherMeanViewHolder
import com.w10group.hertzdictionary.core.view.createTouchFeedback
import org.jetbrains.anko.*

/**
 * Created by Administrator on 2018/6/25.
 * 展示其它义项的RecyclerView适配器
 */

class OtherMeanAdapter(private val mContext: Context,
                       private val mData: List<DictInfo>) : Adapter<OtherMeanViewHolder>() {

    private companion object {
        const val WORD = 1
        const val REVERSE = 2
    }

    private val black by lazy { ContextCompat.getColor(mContext, android.R.color.black) }
    private val gray600 by lazy { ContextCompat.getColor(mContext, R.color.gray600) }

    override fun getItemCount(): Int = mData.size

    override fun onBindViewHolder(holder: OtherMeanViewHolder, position: Int) = with(holder) {
        val data = mData[position]
        tvWord.text = data.word
        tvReverse.text = StringBuilder().apply {
            data.reverses?.let {
                val last = it.size - 1
                it.forEachIndexed { index, word ->
                    append(if (index == last) word else "$word, ")
                }
            }
        }.toString()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OtherMeanViewHolder =
            OtherMeanViewHolder(AnkoContext.create(mContext).apply {
                verticalLayout {
                    lparams(matchParent, wrapContent)
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        isClickable = true
                        foreground = createTouchFeedback(context)
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
            }.view)

    class OtherMeanViewHolder(itemView: View) : ViewHolder(itemView) {
        val tvWord = itemView.find<TextView>(WORD)
        val tvReverse = itemView.find<TextView>(REVERSE)
    }

}