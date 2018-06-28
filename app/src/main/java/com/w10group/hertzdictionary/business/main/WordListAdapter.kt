package com.w10group.hertzdictionary.business.main

import android.content.Context
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.w10group.hertzdictionary.R
import com.w10group.hertzdictionary.business.bean.RealmWord
import org.jetbrains.anko.*
import org.jetbrains.anko.cardview.v7.cardView
import com.w10group.hertzdictionary.business.main.WordListAdapter.WordListViewHolder
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers

/**
 * Created by Administrator on 2018/4/20 0020
 * MainFragment中单词RecyclerView的Adapter.
 */
class WordListAdapter(private val mContext: Context, private val mData: List<RealmWord>) : RecyclerView.Adapter<WordListViewHolder>() {

    companion object {
        const val TV_ENGLISH_ID = 1
        const val TV_CHINESE_ID = 2
        const val TV_COUNT_ID = 3
        const val TV_INQUIRE_RATE_ID = 4
        var sumCount = 0
    }

    private val black by lazy { ContextCompat.getColor(mContext, android.R.color.black) }
    private val gray by lazy { ContextCompat.getColor(mContext, R.color.gray600) }

    init {
        //在子线程计算所有单词的总查询次数
        Observable.just(mData)
                .subscribeOn(Schedulers.computation())
                .observeOn(Schedulers.computation())
                .map { mData.sumBy { it.count } }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeBy { sumCount = it }
    }

    override fun getItemCount(): Int = mData.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WordListViewHolder {
        val view = AnkoContext.create(mContext).apply {
            cardView {
                elevation = dip(8).toFloat()
                translationZ = dip(8).toFloat()
                isClickable = true
                isFocusable = true
                radius = dip(4).toFloat()
                backgroundResource = R.color.deepWhite
                relativeLayout {
                    textView {
                        id = TV_ENGLISH_ID
                        textColor = black
                    }.lparams(wrapContent, wrapContent) {
                        alignParentTop()
                        alignParentStart()
                        topMargin = dip(8)
                        marginStart = dip(8)
                    }
                    textView {
                        id = TV_CHINESE_ID
                        textColor = gray
                    }.lparams(wrapContent, wrapContent) {
                        below(TV_ENGLISH_ID)
                        alignParentStart()
                        marginStart = dip(8)
                    }
                    textView {
                        id = TV_COUNT_ID
                        textColor = gray
                    }.lparams(wrapContent, wrapContent) {
                        alignParentTop()
                        endOf(TV_ENGLISH_ID)
                        marginStart = dip(8)
                    }
                    textView {
                        id = TV_INQUIRE_RATE_ID
                        textColor = gray
                    }.lparams(wrapContent, wrapContent) {
                        below(TV_COUNT_ID)
                        alignStart(TV_COUNT_ID)
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
        val rateStr = "查询比例：${word.count / sumCount}"
        holder.tvInquireRate.text = rateStr
    }

    class WordListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvEnglish = itemView.find<TextView>(TV_ENGLISH_ID)
        val tvChinese = itemView.find<TextView>(TV_CHINESE_ID)
        val tvCount = itemView.find<TextView>(TV_COUNT_ID)
        val tvInquireRate = itemView.find<TextView>(TV_INQUIRE_RATE_ID)
    }

}