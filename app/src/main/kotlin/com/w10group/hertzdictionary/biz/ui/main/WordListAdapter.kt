package com.w10group.hertzdictionary.biz.ui.main

import android.content.Context
import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout.LayoutParams.PARENT_ID
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.w10group.hertzdictionary.R
import com.w10group.hertzdictionary.database.LocalWordDatabase
import com.w10group.hertzdictionary.biz.manager.WordManagerServiceV3
import org.jetbrains.anko.*
import org.jetbrains.anko.cardview.v7.cardView
import com.w10group.hertzdictionary.biz.ui.main.WordListAdapter.WordListViewHolder
import com.w10group.hertzdictionary.core.view.createTouchFeedbackBorderless
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.jetbrains.anko.constraint.layout.constraintLayout
import java.text.NumberFormat

/**
 * Created by Administrator on 2018/4/20 0020
 * MainFragment中单词RecyclerView的Adapter.
 */
class WordListAdapter(private val mContext: Context,
                      private val mData: MutableList<LocalWord>,
                      private val itemOnClickListener: (String) -> Unit) : Adapter<WordListViewHolder>() {

    private companion object {
        const val TV_ENGLISH_ID = 1
        const val TV_CHINESE_ID = 2
        const val TV_COUNT_ID = 3
        const val TV_INQUIRE_RATE_ID = 4
        const val CARD_ITEM_ID = 5
    }

    private val gray by lazy { ContextCompat.getColor(mContext, R.color.gray600) }

    private val mFormat = NumberFormat.getPercentInstance().apply {
        maximumFractionDigits = 2
    }

    private val dao = LocalWordDatabase.getDAO(mContext)

    var sumCount = mData.sumBy { it.count }

    override fun getItemCount(): Int = mData.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WordListViewHolder =
            WordListViewHolder(mContext.UI {
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
            }.view)

    override fun onBindViewHolder(holder: WordListViewHolder, position: Int) = with(holder) {
        val word = mData[position]
        tvEnglish.text = word.en
        tvChinese.text = word.ch
        tvCount.text = mContext.getString(R.string.required_count, word.count)
        tvInquireRate.text = mContext.getString(R.string.required_rate,
                mFormat.format(word.count.toDouble() / sumCount.toDouble()))
        cardView.setOnClickListener { itemOnClickListener(word.en) }
        cardView.setOnLongClickListener { onLongClick(word, position) }
    }

    private fun onLongClick(localWord: LocalWord, index: Int): Boolean {
        mContext.selector("",
                listOf(mContext.getString(R.string.delete_word, localWord.en),
                        mContext.getString(R.string.delete_all_word))) { dialog, which ->
            val alert = if (which == 0) mContext.alert {
                title = mContext.getString(R.string.are_you_sure_delete, localWord.en)
                message = mContext.getString(R.string.delete_clear_word)
                okButton { alertDialog ->
                    mData.removeAt(index)
                    sumCount -= localWord.count
                    notifyItemRemoved(index)
                    notifyItemRangeChanged(0, mData.size)
                    GlobalScope.launch(Dispatchers.IO) { dao.delete(localWord) }
                    alertDialog.dismiss()
                }
                cancelButton { it.dismiss() }
            } else mContext.alert {
                title = mContext.getString(R.string.are_you_sure_delete_all)
                message = mContext.getString(R.string.delete_clear_word_all)
                okButton { alertDialog ->
                    mData.clear()
                    sumCount = 0
                    notifyDataSetChanged()
                    GlobalScope.launch(Dispatchers.IO) {
                        dao.delete(*WordManagerServiceV3.getAllLocalWord(mContext).toTypedArray())
                    }
                    alertDialog.dismiss()
                }
                cancelButton { it.dismiss() }
            }
            alert.show()
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