package com.w10group.hertzdictionary.app.biz.ui.licence

import android.view.View
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.AppBarLayout.LayoutParams.*
import com.w10group.hertzdictionary.app.R
import com.w10group.hertzdictionary.app.core.view.getActionBarSize
import org.jetbrains.anko.*
import org.jetbrains.anko.appcompat.v7.toolbar
import org.jetbrains.anko.design.appBarLayout
import org.jetbrains.anko.design.coordinatorLayout
import org.jetbrains.anko.recyclerview.v7.recyclerView

/**
 * LicenceActivity 的 UI
 * @author Qiao
 */

class LicenceActivityUIComponent(private val mLicenceActivity: LicenceActivity) : AnkoComponent<LicenceActivity>, LifecycleObserver {

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

    @Suppress("unused")
    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    private fun init() = with(mLicenceActivity) {
        setSupportActionBar(mToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        Unit
    }

    fun <T : RecyclerView.ViewHolder> setAdapter(adapter: RecyclerView.Adapter<T>) {
        mRecyclerView.adapter = adapter
    }

}