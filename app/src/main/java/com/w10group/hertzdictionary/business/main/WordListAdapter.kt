package com.w10group.hertzdictionary.business.main

import android.content.Context
import android.support.v4.content.ContextCompat
import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.w10group.hertzdictionary.R
import com.w10group.hertzdictionary.business.bean.LocalWord
import org.jetbrains.anko.*
import org.jetbrains.anko.cardview.v7.cardView
import com.w10group.hertzdictionary.business.main.WordListAdapter.WordListViewHolder
import java.text.NumberFormat

/**
 * Created by Administrator on 2018/4/20 0020
 * MainFragment中单词RecyclerView的Adapter.
 */
class WordListAdapter(private val mContext: Context,
                      private val mData: List<LocalWord>,
                      private val itemOnClickListener: (View, String) -> Unit,
                      private val itemOnLongClickListener: (View) -> Unit) : RecyclerView.Adapter<WordListViewHolder>() {

    companion object {
        const val TV_ENGLISH_ID = 1
        const val TV_CHINESE_ID = 2
        const val TV_COUNT_ID = 3
        const val TV_INQUIRE_RATE_ID = 4
        const val CARD_ITEM_ID = 5
        var sumCount = 0
    }

    private val black by lazy { ContextCompat.getColor(mContext, android.R.color.black) }
    private val white by lazy { ContextCompat.getColor(mContext, android.R.color.white) }
    private val gray by lazy { ContextCompat.getColor(mContext, R.color.gray600) }

    private val mFormat = NumberFormat.getPercentInstance()

    init {
        mFormat.maximumFractionDigits = 2
        sumCount = mData.sumBy { it.count }
    }

    override fun getItemCount(): Int = mData.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WordListViewHolder {
        val styledAttributes = mContext.theme.obtainStyledAttributes(
                intArrayOf(android.R.attr.selectableItemBackground))
        val touchFeedback = styledAttributes.getDrawable(0)
        styledAttributes.recycle()

        val view = AnkoContext.create(mContext).apply {
            verticalLayout {
                cardView {
                    id = CARD_ITEM_ID
                    elevation = dip(8).toFloat()
                    translationZ = dip(8).toFloat()
                    isClickable = true
                    backgroundColor = white
                    foreground = touchFeedback

                    relativeLayout {
                        textView {
                            id = TV_ENGLISH_ID
                            textColor = black
                            textSize = 20f
                        }.lparams(wrapContent, wrapContent) {
                            alignParentTop()
                            alignParentStart()
                            topMargin = dip(16)
                            marginStart = dip(16)
                        }
                        textView {
                            id = TV_CHINESE_ID
                            textColor = gray
                            textSize = 16f
                        }.lparams(wrapContent, wrapContent) {
                            alignStart(TV_ENGLISH_ID)
                            below(TV_ENGLISH_ID)
                            topMargin = dip(4)
                            bottomMargin = dip(16)
                        }
                        textView {
                            id = TV_COUNT_ID
                            textColor = gray
                        }.lparams(wrapContent, wrapContent) {
                            endOf(TV_CHINESE_ID)
                            sameBottom(TV_CHINESE_ID)
                            marginStart = dip(32)
                        }
                        textView {
                            id = TV_INQUIRE_RATE_ID
                            textColor = gray
                        }.lparams(wrapContent, wrapContent) {
                            alignParentEnd()
                            sameBottom(TV_COUNT_ID)
                            marginEnd = dip(16)
                        }
                    }.lparams(matchParent, wrapContent)
                }.lparams(matchParent, wrapContent) {
                    topMargin = dip(4)
                    bottomMargin = dip(4)
                    marginStart = dip(8)
                    marginEnd = dip(8)
                }
            }
        }.view
        return WordListViewHolder(view)
    }

    override fun onBindViewHolder(holder: WordListViewHolder, position: Int) {
        val word = mData[position]
        holder.tvEnglish.text = word.en
        holder.tvChinese.text = word.ch
        val countStr = "已查询次数：${word.count}"
        holder.tvCount.text = countStr
        val rateStr = "查询比例：${mFormat.format(word.count.toDouble() / sumCount.toDouble())}"
        holder.tvInquireRate.text = rateStr
        holder.cardView.setOnClickListener { itemOnClickListener(it, word.en) }
        holder.cardView.setOnLongClickListener {
            itemOnLongClickListener(it)
            false
        }
    }

    class WordListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvEnglish = itemView.find<TextView>(TV_ENGLISH_ID)
        val tvChinese = itemView.find<TextView>(TV_CHINESE_ID)
        val tvCount = itemView.find<TextView>(TV_COUNT_ID)
        val tvInquireRate = itemView.find<TextView>(TV_INQUIRE_RATE_ID)
        val cardView = itemView.find<CardView>(CARD_ITEM_ID)
    }

}