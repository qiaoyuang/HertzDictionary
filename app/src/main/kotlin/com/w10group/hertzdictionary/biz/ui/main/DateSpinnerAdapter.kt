package com.w10group.hertzdictionary.biz.ui.main

import android.content.Context
import android.database.DataSetObserver
import android.support.v4.content.ContextCompat
import android.view.View
import android.view.ViewGroup
import android.widget.SpinnerAdapter
import com.w10group.hertzdictionary.R
import org.jetbrains.anko.*

/**
 * 首页日期选择器 Adapter
 * @author Qiao
 */

class DateSpinnerAdapter(private val mContext: Context) : SpinnerAdapter {

    private val mData = listOf("最近一星期", "最近一个月")
    private val gray600 by lazy { ContextCompat.getColor(mContext, R.color.gray600) }
    private val blue1 by lazy { ContextCompat.getColor(mContext, R.color.blue1) }

    override fun registerDataSetObserver(observer: DataSetObserver?) = Unit
    override fun unregisterDataSetObserver(observer: DataSetObserver?) = Unit
    override fun getCount(): Int = mData.size
    override fun getItem(position: Int): Any = mData[position]
    override fun getItemId(position: Int): Long = position.toLong()
    override fun hasStableIds(): Boolean = false
    override fun getItemViewType(position: Int): Int = 0
    override fun getViewTypeCount(): Int = 1
    override fun isEmpty(): Boolean = false

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup?): View = createView(gray600, position, true)

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View = createView(blue1, position, false)

    private fun createView(color: Int, position: Int, isMargin: Boolean) = AnkoContext.create(mContext).apply {
        frameLayout {
            textView {
                text = mData[position]
                textSize = 16f
                textColor = color
            }.lparams(wrapContent, wrapContent) {
                if (isMargin) {
                    topMargin = dip(8)
                    bottomMargin = dip(8)
                    marginStart = dip(16)
                    marginEnd = dip(16)
                }
            }
        }
    }.view

}