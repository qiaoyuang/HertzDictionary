package com.w10group.hertzdictionary.business.features

import android.graphics.Color
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.AppBarLayout.ScrollingViewBehavior
import android.support.design.widget.AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL
import android.support.design.widget.AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS
import android.support.design.widget.AppBarLayout.LayoutParams.SCROLL_FLAG_SNAP
import android.support.v4.content.ContextCompat
import android.support.v7.widget.Toolbar
import android.view.Gravity
import android.view.MenuItem
import android.widget.TextView
import com.w10group.hertzdictionary.R
import com.w10group.hertzdictionary.business.manager.FileReadManagerService
import com.w10group.hertzdictionary.core.ActionBarSize
import com.w10group.hertzdictionary.core.createTouchFeedbackBorderless
import org.jetbrains.anko.*
import org.jetbrains.anko.appcompat.v7.toolbar
import org.jetbrains.anko.cardview.v7.cardView
import org.jetbrains.anko.design.appBarLayout
import org.jetbrains.anko.design.coordinatorLayout
import org.jetbrains.anko.support.v4.nestedScrollView

class FeaturesActivity : AppCompatActivity() {

    private companion object {
        const val FEATURE_FILE_NAME = "feature.txt"
        const val MY_EMAIL = "Email:qiaoyuang2012@gmail.com"
        const val GITHUB_ADDRESS = "https://github.com/qiaoyuang/HertzDictionary"
    }

    private val blue1 by lazy { ContextCompat.getColor(this, R.color.blue1) }

    private lateinit var mTVCurrentContent: TextView
    private lateinit var mTVNextContent: TextView
    private lateinit var mTVBugFeedback1: TextView
    private lateinit var mTVBugFeedback2: TextView
    private lateinit var mTVAboutTech1: TextView
    private lateinit var mTVAboutTech2: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lateinit var toolbar: Toolbar
        coordinatorLayout {
            backgroundColorResource = R.color.deepWhite
            appBarLayout {
                elevation = dip(8).toFloat()
                translationZ = dip(8).toFloat()
                toolbar = toolbar {
                    title = "未来新功能"
                    backgroundColorResource = R.color.blue1
                }.lparams(matchParent, ActionBarSize.get(this@FeaturesActivity)) {
                    scrollFlags = SCROLL_FLAG_SCROLL or SCROLL_FLAG_ENTER_ALWAYS or SCROLL_FLAG_SNAP
                }
            }.lparams(matchParent, wrapContent)

            nestedScrollView {

                verticalLayout {
                    cardView {
                        elevation = dip(4).toFloat()
                        translationZ = dip(4).toFloat()
                        isClickable = true
                        foreground = createTouchFeedbackBorderless(this@FeaturesActivity)
                        verticalLayout {
                            textView {
                                val title = "当前版本：V${packageManager.getPackageInfo(packageName, 0).versionName}"
                                text = title
                                textColor = Color.BLACK
                                textSize = 22f
                            }.lparams(wrapContent, wrapContent)

                            mTVCurrentContent = textView {
                                textSize = 16f
                            }.lparams(matchParent, matchParent) {
                                topMargin = dip(8)
                            }

                        }.lparams(matchParent, wrapContent) {
                            marginStart = dip(8)
                            marginEnd = dip(8)
                            topMargin = dip(16)
                            bottomMargin = dip(16)
                        }
                    }.lparams(matchParent, wrapContent) {
                        marginStart = dip(8)
                        marginEnd = dip(8)
                        topMargin = dip(16)
                    }

                    cardView {
                        elevation = dip(4).toFloat()
                        translationZ = dip(4).toFloat()
                        isClickable = true
                        foreground = createTouchFeedbackBorderless(this@FeaturesActivity)

                        verticalLayout {
                            textView {
                                val title = "计划开发的新功能"
                                text = title
                                textColor = Color.BLACK
                                textSize = 22f
                            }.lparams(wrapContent, wrapContent)

                            mTVNextContent = textView {
                                textSize = 16f
                            }.lparams(matchParent, matchParent) {
                                topMargin = dip(8)
                            }

                        }.lparams(matchParent, wrapContent) {
                            marginStart = dip(8)
                            marginEnd = dip(8)
                            topMargin = dip(16)
                            bottomMargin = dip(16)
                        }

                    }.lparams(matchParent, wrapContent) {
                        marginStart = dip(8)
                        marginEnd = dip(8)
                        topMargin = dip(8)
                    }

                    cardView {
                        elevation = dip(4).toFloat()
                        translationZ = dip(4).toFloat()
                        isClickable = true
                        foreground = createTouchFeedbackBorderless(this@FeaturesActivity)

                        verticalLayout {
                            textView {
                                val title = "Bug反馈"
                                text = title
                                textColor = Color.BLACK
                                textSize = 22f
                            }.lparams(wrapContent, wrapContent)

                            mTVBugFeedback1 = textView {
                                textSize = 16f
                            }.lparams(matchParent, matchParent) {
                                topMargin = dip(8)
                            }

                            textView {
                                text = MY_EMAIL
                                textSize = 16f
                                textColor = blue1
                                setOnClickListener {

                                }
                            }.lparams(wrapContent, wrapContent) {
                                gravity = Gravity.CENTER_HORIZONTAL
                                topMargin = dip(8)
                            }

                            mTVBugFeedback2 = textView {
                                textSize = 16f
                            }.lparams(matchParent, matchParent) {
                                topMargin = dip(8)
                            }

                            button {
                                text = "我的微信"
                                textSize = 16f
                                backgroundColorResource = R.color.wechat
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                                    foreground = createTouchFeedbackBorderless(this@FeaturesActivity)
                            }.lparams(matchParent, wrapContent) {
                                topMargin = dip(24)
                            }

                        }.lparams(matchParent, wrapContent) {
                            marginStart = dip(8)
                            marginEnd = dip(8)
                            topMargin = dip(16)
                            bottomMargin = dip(16)
                        }

                    }.lparams(matchParent, wrapContent) {
                        marginStart = dip(8)
                        marginEnd = dip(8)
                        topMargin = dip(8)
                    }

                    cardView {
                        elevation = dip(4).toFloat()
                        translationZ = dip(4).toFloat()
                        isClickable = true
                        foreground = createTouchFeedbackBorderless(this@FeaturesActivity)

                        verticalLayout {
                            textView {
                                val title = "一点技术相关"
                                text = title
                                textColor = Color.BLACK
                                textSize = 22f
                            }.lparams(wrapContent, wrapContent)

                            mTVAboutTech1 = textView {
                                textSize = 16f
                            }.lparams(matchParent, matchParent) {
                                topMargin = dip(8)
                            }

                            textView {
                                text = GITHUB_ADDRESS
                                textSize = 16f
                                textColor = blue1
                                setOnClickListener {

                                }
                            }.lparams(wrapContent, wrapContent) {
                                gravity = Gravity.CENTER_HORIZONTAL
                                topMargin = dip(8)
                            }

                            mTVAboutTech2 = textView {
                                textSize = 16f
                            }.lparams(matchParent, matchParent) {
                                topMargin = dip(8)
                            }

                        }.lparams(matchParent, wrapContent) {
                            marginStart = dip(8)
                            marginEnd = dip(8)
                            topMargin = dip(16)
                            bottomMargin = dip(16)
                        }

                    }.lparams(matchParent, wrapContent) {
                        marginStart = dip(8)
                        marginEnd = dip(8)
                        topMargin = dip(8)
                        bottomMargin = dip(16)
                    }

                }.lparams(matchParent, matchParent)

            }.lparams(matchParent, matchParent) {
                behavior = ScrollingViewBehavior()
            }
        }

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        FileReadManagerService.process(FEATURE_FILE_NAME, this,
                mTVCurrentContent, mTVNextContent, mTVBugFeedback1,
                mTVBugFeedback2, mTVAboutTech1, mTVAboutTech2)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> { onBackPressed() }
        }
        return super.onOptionsItemSelected(item)
    }

}