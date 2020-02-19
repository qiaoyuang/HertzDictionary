package com.w10group.hertzdictionary.biz.ui.statistics

import android.graphics.Color
import android.view.View
import android.widget.AdapterView
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.AppBarLayout.LayoutParams.*
import com.w10group.hertzdictionary.R
import com.w10group.hertzdictionary.biz.ui.main.DateSpinnerAdapter
import com.w10group.hertzdictionary.core.view.*
import org.jetbrains.anko.*
import org.jetbrains.anko.appcompat.v7.toolbar
import org.jetbrains.anko.cardview.v7.cardView
import org.jetbrains.anko.design.appBarLayout
import org.jetbrains.anko.design.coordinatorLayout
import org.jetbrains.anko.support.v4.nestedScrollView

/**
 * StatisticsActivity 的 UI
 * @author Qiao
 */

class StatisticsActivityUIComponent(private val mStatisticsActivity: StatisticsActivity) : AnkoComponent<StatisticsActivity>, LifecycleObserver {

    private lateinit var mToolbar: Toolbar
    private lateinit var mCurveView: CurveView

    private lateinit var tvTotalCount: TextView
    private lateinit var tvAverageCount: TextView
    private lateinit var tvMostWord: TextView

    private val gray600 by lazy { ContextCompat.getColor(mStatisticsActivity, R.color.gray600) }

    override fun createView(ui: AnkoContext<StatisticsActivity>): View = ui.apply {
        coordinatorLayout {
            backgroundColorResource = R.color.deepWhite
            appBarLayout {
                translationZ = dip(8).toFloat()
                mToolbar = toolbar {
                    title = context.getString(R.string.statistics)
                    backgroundColorResource = R.color.blue1
                }.lparams(matchParent, getActionBarSize(context)) {
                    scrollFlags = SCROLL_FLAG_SCROLL or SCROLL_FLAG_ENTER_ALWAYS or SCROLL_FLAG_SNAP
                }
            }.lparams(matchParent, wrapContent)

            nestedScrollView {
                verticalLayout {
                    cardView {
                        elevation = dip(4).toFloat()
                        isClickable = true
                        backgroundColor = Color.WHITE
                        foreground = createTouchFeedbackBorderless(context)
                        verticalLayout {
                            appCompatSpinner {
                                adapter = DateSpinnerAdapter(context)
                                onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                                    override fun onNothingSelected(parent: AdapterView<*>?) = Unit
                                    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                                        when (position) {
                                            0 -> mStatisticsActivity.weekSelected()
                                            1 -> mStatisticsActivity.monthSelected()
                                        }
                                    }
                                }
                            }.lparams(wrapContent, wrapContent)

                            tvTotalCount = textView {
                                textSize = 14f
                                textColor = gray600
                            }.lparams(wrapContent, wrapContent) {
                                topMargin = dip(16)
                            }

                            tvAverageCount = textView {
                                textSize = 14f
                                textColor = gray600
                            }.lparams(wrapContent, wrapContent) {
                                topMargin = dip(16)
                            }

                            tvMostWord = textView {
                                textSize = 14f
                                textColor = gray600
                            }.lparams(wrapContent, wrapContent) {
                                topMargin = dip(16)
                            }

                        }.lparams(matchParent, wrapContent) {
                            margin = dip(16)
                        }
                    }.lparams(matchParent, wrapContent) {
                        marginStart = dip(8)
                        marginEnd = dip(8)
                        topMargin = dip(8)
                    }

                    cardView {
                        elevation = dip(4).toFloat()
                        isClickable = true
                        backgroundColor = Color.WHITE
                        foreground = createTouchFeedbackBorderless(context)
                        mCurveView = curveView().lparams(matchParent, dip(256)) {
                            marginStart = dip(16)
                            marginEnd = dip(16)
                        }
                    }.lparams(matchParent, wrapContent) {
                        marginStart = dip(8)
                        marginEnd = dip(8)
                        topMargin = dip(8)
                        bottomMargin = dip(16)
                    }

                }.lparams(matchParent, wrapContent)
            }.lparams(matchParent, matchParent) {
                behavior = AppBarLayout.ScrollingViewBehavior()
            }
        }
    }.view

    fun updateUI(totalCountText: String,
                 averageCountText: String,
                 mostWordText: String,
                 timeList: List<Long>,
                 valueList: List<Int>) {
        tvTotalCount.text = totalCountText
        tvAverageCount.text = averageCountText
        tvMostWord.text = mostWordText
        mCurveView.setData(timeList, valueList)
    }

    @Suppress("unused")
    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    private fun init() {
        mStatisticsActivity.setSupportActionBar(mToolbar)
        mStatisticsActivity.supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

}