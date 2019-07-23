package com.w10group.hertzdictionary.biz.ui.licence

import android.view.View
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.AppBarLayout.LayoutParams.*
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
                layoutManager = LinearLayoutManager(context)
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