package com.w10group.hertzdictionary.biz.main

import android.content.Context
import android.graphics.Color
import android.support.constraint.ConstraintLayout.LayoutParams.PARENT_ID
import android.support.v4.content.ContextCompat
import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView.Adapter
import android.support.v7.widget.RecyclerView.ViewHolder
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.w10group.hertzdictionary.R
import com.w10group.hertzdictionary.biz.bean.LocalWord
import org.jetbrains.anko.*
import org.jetbrains.anko.cardview.v7.cardView
import com.w10group.hertzdictionary.biz.main.WordListAdapter.WordListViewHolder
import com.w10group.hertzdictionary.core.createTouchFeedbackBorderless
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.jetbrains.anko.constraint.layout.constraintLayout
import org.litepal.LitePal
import org.litepal.extension.deleteAll
import java.text.NumberFormat

/**
 * Created by Administrator on 2018/4/20 0020
 * MainFragment中单词RecyclerView的Adapter.
 */
class WordListAdapter(private val mContext: Context,
                      private val mData: MutableList<LocalWord>,
                      private val mCoroutineScope: CoroutineScope,
                      private val itemOnClickListener: (String) -> Unit) : Adapter<WordListViewHolder>() {

    private companion object {
        const val TV_ENGLISH_ID = 1
        const val TV_CHINESE_ID = 2
        const val TV_COUNT_ID = 3
        const val TV_INQUIRE_RATE_ID = 4
        const val CARD_ITEM_ID = 5
    }

    private val gray by lazy { ContextCompat.getColor(mContext, R.color.gray600) }

    private val mFormat = NumberFormat.getPercentInstance()

    var sumCount = mData.sumBy { it.count }

    init {
        mFormat.maximumFractionDigits = 2
    }

    override fun getItemCount(): Int = mData.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WordListViewHolder {
        val view = AnkoContext.create(mContext).apply {
            cardView {
                lparams(matchParent, wrapContent) {
                    topMargin = dip(4)
                    marginStart = dip(4)
                    marginEnd = dip(4)
                }
                id = CARD_ITEM_ID
                elevation = dip(4).toFloat()
                isClickable = true
                backgroundColor = Color.WHITE
                foreground = createTouchFeedbackBorderless(context)
                constraintLayout {
                    textView {
                        id = TV_ENGLISH_ID
                        textColor = Color.BLACK
                        textSize = 20f
                    }.lparams(wrapContent, wrapContent) {
                        topToTop = PARENT_ID
                        startToStart = PARENT_ID
                        topMargin = dip(16)
                        marginStart = dip(16)
                    }
                    textView {
                        id = TV_CHINESE_ID
                        textColor = gray
                        textSize = 16f
                    }.lparams(wrapContent, wrapContent) {
                        topToBottom = TV_ENGLISH_ID
                        bottomToBottom = PARENT_ID
                        startToStart = TV_ENGLISH_ID
                        topMargin = dip(4)
                        bottomMargin = dip(16)
                    }
                    textView {
                        id = TV_COUNT_ID
                        textColor = gray
                    }.lparams(wrapContent, wrapContent) {
                        startToStart = PARENT_ID
                        endToEnd = PARENT_ID
                        bottomToBottom = TV_CHINESE_ID
                        horizontalBias = 0.45f
                    }
                    textView {
                        id = TV_INQUIRE_RATE_ID
                        textColor = gray
                    }.lparams(wrapContent, wrapContent) {
                        endToEnd = PARENT_ID
                        bottomToBottom = TV_CHINESE_ID
                        marginEnd = dip(16)
                    }
                }.lparams(matchParent, wrapContent)
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
        holder.cardView.setOnClickListener { itemOnClickListener(word.en) }
        holder.cardView.setOnLongClickListener { onLongClick(word, position) }
    }

    private fun onLongClick(localWord: LocalWord, index: Int): Boolean {
        mContext.selector("", listOf("删除：${localWord.en}", "清空所有单词")) { dialog, which ->
            if (which == 0) {
                mContext.alert {
                    title = "您确定要删除${localWord.en}吗？"
                    message = "删除单词会使单词的查询次数清零，且不可恢复，请您确认。"
                    okButton { alertDialog ->
                        mData.removeAt(index)
                        sumCount -= localWord.count
                        notifyItemRemoved(index)
                        notifyItemRangeChanged(0, mData.size)
                        mCoroutineScope.launch(Dispatchers.IO) { localWord.delete() }
                        alertDialog.dismiss()
                    }
                    cancelButton { it.dismiss() }
                }.show()
            } else {
                mContext.alert {
                    title = "您确定要清空所有单词吗？"
                    message = "清空所有单词会使所有保存的已查询单词数据全部清零，且不可恢复，请您确认。"
                    okButton { alertDialog ->
                        mData.clear()
                        sumCount = 0
                        notifyDataSetChanged()
                        mCoroutineScope.launch(Dispatchers.IO) { LitePal.deleteAll<LocalWord>() }
                        alertDialog.dismiss()
                    }
                    cancelButton { it.dismiss() }
                }.show()
            }
            dialog.dismiss()
        }
        return false
    }

    class WordListViewHolder(itemView: View) : ViewHolder(itemView) {
        val tvEnglish = itemView.find<TextView>(TV_ENGLISH_ID)
        val tvChinese = itemView.find<TextView>(TV_CHINESE_ID)
        val tvCount = itemView.find<TextView>(TV_COUNT_ID)
        val tvInquireRate = itemView.find<TextView>(TV_INQUIRE_RATE_ID)
        val cardView = itemView.find<CardView>(CARD_ITEM_ID)
    }

}