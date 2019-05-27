package com.w10group.hertzdictionary.biz.ui.statistics

import android.graphics.Color
import android.support.design.widget.AppBarLayout
import android.support.v7.widget.Toolbar
import android.view.View
import android.widget.AdapterView
import com.w10group.hertzdictionary.R
import com.w10group.hertzdictionary.biz.manager.DateManagerService
import com.w10group.hertzdictionary.biz.manager.WordManagerServiceV3
import com.w10group.hertzdictionary.biz.ui.main.DateSpinnerAdapter
import com.w10group.hertzdictionary.core.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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

class StatisticsActivityUI(private val mStatisticsActivity: StatisticsActivity) : AnkoComponent<StatisticsActivity> {

    private lateinit var mToolbar: Toolbar
    private lateinit var mCurveView: CurveView

    private lateinit var mWordManagerServiceV3: WordManagerServiceV3

    override fun createView(ui: AnkoContext<StatisticsActivity>): View = ui.apply {
        coordinatorLayout {
            backgroundColorResource = R.color.deepWhite
            appBarLayout {
                translationZ = dip(8).toFloat()
                mToolbar = toolbar {
                    title = context.getString(R.string.statistics)
                    backgroundColorResource = R.color.blue1
                }.lparams(matchParent, getActionBarSize(context)) {
                    scrollFlags = AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL or AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS or AppBarLayout.LayoutParams.SCROLL_FLAG_SNAP
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
                                            0 -> {
                                                mStatisticsActivity.launch(Dispatchers.Default) {
                                                    if (!::mWordManagerServiceV3.isInitialized)
                                                        mWordManagerServiceV3 = WordManagerServiceV3.instanceChannel.receive()
                                                    val (timeList, valueList) = DateManagerService.createWeekValue(*mWordManagerServiceV3.allLocalWords.toTypedArray())
                                                    withContext(Dispatchers.Main) { mCurveView.setData(timeList, valueList) }
                                                }
                                            }
                                            1 -> {
                                                mStatisticsActivity.launch(Dispatchers.Default) {
                                                    if (!::mWordManagerServiceV3.isInitialized)
                                                        mWordManagerServiceV3 = WordManagerServiceV3.instanceChannel.receive()
                                                    val (timeList, valueList) = DateManagerService.createMonthValue(*mWordManagerServiceV3.allLocalWords.toTypedArray())
                                                    withContext(Dispatchers.Main) { mCurveView.setData(timeList, valueList) }
                                                }
                                            }
                                        }
                                    }
                                }
                            }.lparams(wrapContent, wrapContent) {
                                topMargin = dip(16)
                            }

                            mCurveView = curveView().lparams(matchParent, dip(256))
                        }.lparams(matchParent, wrapContent) {
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

    fun initToolbar() {
        mStatisticsActivity.setSupportActionBar(mToolbar)
        mStatisticsActivity.supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

}