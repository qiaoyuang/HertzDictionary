package com.w10group.hertzdictionary.biz.ui.licence

import android.support.design.widget.AppBarLayout
import android.support.design.widget.AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL
import android.support.design.widget.AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS
import android.support.design.widget.AppBarLayout.LayoutParams.SCROLL_FLAG_SNAP
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.view.View
import com.w10group.hertzdictionary.R
import com.w10group.hertzdictionary.core.architecture.UIComponent
import com.w10group.hertzdictionary.core.view.getActionBarSize
import org.jetbrains.anko.*
import org.jetbrains.anko.appcompat.v7.toolbar
import org.jetbrains.anko.design.appBarLayout
import org.jetbrains.anko.design.coordinatorLayout
import org.jetbrains.anko.recyclerview.v7.recyclerView

/**
 * LicenceActivity 的 UI
 * @author Qiao
 */

class LicenceActivityUIComponent(private val mLicenceActivity: LicenceActivity) : UIComponent<LicenceActivity>() {

    private lateinit var mToolbar: Toolbar

    private lateinit var mRecyclerView: RecyclerView

    override fun createView(ui: AnkoContext<LicenceActivity>): View = ui.apply {
        coordinatorLayout {
            appBarLayout {
                mToolbar = toolbar {
                    title = context.getString(R.string.open_source_license)
                    backgroundColorResource = R.color.blue1
                }.lparams(matchParent, getActionBarSize(context)) {
                    scrollFlags = SCROLL_FLAG_SCROLL or SCROLL_FLAG_ENTER_ALWAYS or SCROLL_FLAG_SNAP
                }
            }.lparams(matchParent, wrapContent)

            mRecyclerView = recyclerView {
                layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                itemAnimator = DefaultItemAnimator()
            }.lparams(matchParent, wrapContent) {
                marginStart = dip(16)
                marginEnd = dip(16)
                behavior = AppBarLayout.ScrollingViewBehavior()
            }
        }
    }.view

    override fun init() {
        mLicenceActivity.setSupportActionBar(mToolbar)
        mLicenceActivity.supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    fun <T : RecyclerView.ViewHolder> setAdapter(adapter: RecyclerView.Adapter<T>) {
        mRecyclerView.adapter = adapter
    }

}