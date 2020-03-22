package com.w10group.hertzdictionary.app.biz.ui.licence

import android.content.Context
import android.os.Build
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView.Adapter
import com.w10group.hertzdictionary.manager.KV
import com.w10group.hertzdictionary.app.core.view.KVViewHolder
import com.w10group.hertzdictionary.app.core.view.KVViewHolder.Companion.CONTENT_ID
import com.w10group.hertzdictionary.app.core.view.KVViewHolder.Companion.TITLE_ID
import com.w10group.hertzdictionary.app.core.view.createTouchFeedbackBorderless
import org.jetbrains.anko.*

/**
 * Created by Administrator on 2018/6/26.
 * 开源许可证的RecyclerView的Adapter
 */

class OSLAdapter(private val mContext: Context, private val mData: List<KV>) : Adapter<KVViewHolder>() {

    private val black = ContextCompat.getColor(mContext, android.R.color.black)

    override fun getItemCount(): Int = mData.size

    override fun onBindViewHolder(holder: KVViewHolder, position: Int) {
        val data = mData[position]
        holder.apply {
            tvTitle.text = data.first
            tvContent.text = data.second
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): KVViewHolder =
            KVViewHolder(mContext.UI {
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
            }.view)

}