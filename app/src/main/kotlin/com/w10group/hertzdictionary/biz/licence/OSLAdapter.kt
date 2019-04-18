package com.w10group.hertzdictionary.biz.licence

import android.content.Context
import android.os.Build
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView.Adapter
import android.support.v7.widget.RecyclerView.ViewHolder
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.w10group.hertzdictionary.core.createTouchFeedbackBorderless
import org.jetbrains.anko.*

/**
 * Created by Administrator on 2018/6/26.
 * 开源许可证的RecyclerView的Adapter
 */

class OSLAdapter(private val mContext: Context, private val mData: List<OSL>) : Adapter<OSLAdapter.OSLViewHolder>() {

    private companion object {
        const val TITLE_ID = 1
        const val CONTENT_ID = 2
    }

    private val black = ContextCompat.getColor(mContext, android.R.color.black)

    override fun getItemCount(): Int = mData.size

    override fun onBindViewHolder(holder: OSLViewHolder, position: Int) {
        val data = mData[position]
        holder.apply {
            tvTitle.text = data.title
            tvContent.text = data.content
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OSLViewHolder {
        val view = AnkoContext.create(mContext).apply {
            verticalLayout {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    isClickable = true
                    foreground = createTouchFeedbackBorderless(context)
                }
                textView {
                    id = TITLE_ID
                    textSize = 22f
                    textColor = black
                }.lparams(wrapContent, wrapContent) {
                    topMargin = dip(16)
                    bottomMargin = dip(16)
                }
                textView {
                    id = CONTENT_ID
                    textColor = black
                }.lparams(wrapContent, wrapContent) {
                    marginStart = dip(16)
                    bottomMargin = dip(16)
                }
            }
        }.view
        return OSLViewHolder(view)
    }

    class OSLViewHolder(itemView: View) : ViewHolder(itemView) {
        val tvTitle = itemView.find<TextView>(TITLE_ID)
        val tvContent = itemView.find<TextView>(CONTENT_ID)
    }

    data class OSL(var title: String = "", var content: String = "")

}