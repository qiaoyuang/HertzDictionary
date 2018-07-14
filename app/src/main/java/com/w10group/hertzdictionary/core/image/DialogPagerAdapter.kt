package com.w10group.hertzdictionary.core.image

import android.app.Dialog
import android.support.v4.view.PagerAdapter
import android.view.View
import android.view.ViewGroup

/**
 * Created by Administrator on 2018/7/9.
 * 大图查看器的ViewPager适配器
 */

internal class DialogPagerAdapter(private val mViews: List<View>,
                                  private val mDialog: Dialog) : PagerAdapter() {

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val view = mViews[position]
        container.addView(view)
        return view
    }

    override fun destroyItem(container: ViewGroup, position: Int, any: Any) {
        if (position == 0 && mViews.isEmpty()) {
            mDialog.dismiss()
            return
        }
        container.removeView(mViews[if (position == mViews.size) position - 1 else position])
    }

    override fun getCount(): Int = mViews.size

    override fun isViewFromObject(view: View, any: Any): Boolean = view === any

    override fun getItemPosition(any: Any): Int = POSITION_NONE

}