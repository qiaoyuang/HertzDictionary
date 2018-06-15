package com.w10group.hertzdictionary.ui

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.w10group.hertzdictionary.R
import com.w10group.hertzdictionary.bean.Word
import org.jetbrains.anko.*
import org.jetbrains.anko.cardview.v7.cardView
import com.w10group.hertzdictionary.ui.WordListAdapter.WordListViewHolder

/**
 * Created by Administrator on 2018/4/20 0020
 * MainFragment中单词RecyclerView的Adapter.
 */
class WordListAdapter(private val mContext: Context, private val mData: List<Word>) : RecyclerView.Adapter<WordListViewHolder>() {

    companion object {
        const val TV_ENGLISH_ID = 1
        const val TV_CHINESE_ID = 2
        const val TV_COUNT_ID = 3
    }

    override fun getItemCount(): Int = mData.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WordListViewHolder {
        val view = AnkoContext.create(mContext).apply {
            cardView {
                elevation = dip(4).toFloat()
                translationZ = dip(4).toFloat()
                isClickable = true
                isFocusable = true
                radius = dip(4).toFloat()
                backgroundResource = R.color.deepWhite
                relativeLayout {
                    textView {
                        id = TV_ENGLISH_ID
                    }
                    textView {
                        id = TV_CHINESE_ID
                    }
                    textView {
                        id = TV_COUNT_ID
                    }
                }.lparams(matchParent, wrapContent)
            }
        }.view
        return WordListViewHolder(view)
    }

    override fun onBindViewHolder(holder: WordListViewHolder, position: Int) {
        val word = mData[position]
        //holder.tvEnglish.text = word.english
        //holder.tvChinese.text = word.chinese
        //val countStr = "已查询次数：${word.count}"
        //holder.tvCount.text = countStr
    }

    class WordListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvEnglish = itemView.find<TextView>(TV_ENGLISH_ID)
        val tvChinese = itemView.find<TextView>(TV_CHINESE_ID)
        val tvCount = itemView.find<TextView>(TV_COUNT_ID)
    }

}