package com.w10group.hertzdictionary.biz.features

import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.Paint
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
import com.w10group.hertzdictionary.biz.manager.FileReadManagerService
import com.w10group.hertzdictionary.core.*
import com.w10group.hertzdictionary.core.image.CompleteScaleImageView
import org.jetbrains.anko.*
import org.jetbrains.anko.appcompat.v7.toolbar
import org.jetbrains.anko.cardview.v7.cardView
import org.jetbrains.anko.design.appBarLayout
import org.jetbrains.anko.design.coordinatorLayout
import org.jetbrains.anko.support.v4.nestedScrollView
import java.util.*

/**
 * Created by Administrator on 2018/6/25.
 * 未来新功能Activity
 */

class FeaturesActivity : CoroutineScopeActivity() {

    private companion object {
        const val FEATURE_FILE_NAME = "feature.txt"
        const val MY_EMAIL = "qiaoyuang2012@gmail.com"
        const val GITHUB_ADDRESS = "https://github.com/qiaoyuang/HertzDictionary"
        const val WECHAT_CODE = "https://upload-images.jianshu.io/upload_images/12354730-f08c7c37c316d1d8.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240"
    }

    private val blue1 by lazy { ContextCompat.getColor(this, R.color.blue1) }

    private lateinit var mTVCurrentContent: TextView
    private lateinit var mTVNextContent: TextView
    private lateinit var mTVBugFeedback1: TextView
    private lateinit var mTVBugFeedback2: TextView
    private lateinit var mTVAboutTech1: TextView
    private lateinit var mTVAboutTech2: TextView

    private val mWechatCodeList by lazy {
        val list = LinkedList<String>()
        list.add(WECHAT_CODE)
        list
    }

    private val mRequestCode = 1
    private val mCompleteScaleImageView by lazy {
        val completeScaleImageView = CompleteScaleImageView(this, GlideDownloader, mRequestCode)
        completeScaleImageView.setDownloadEnable(true)
        completeScaleImageView.mUrls = mWechatCodeList
        completeScaleImageView
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lateinit var toolbar: Toolbar
        coordinatorLayout {
            backgroundColorResource = R.color.deepWhite
            appBarLayout {
                translationZ = dip(8).toFloat()
                toolbar = toolbar {
                    title = "未来新功能"
                    backgroundColorResource = R.color.blue1
                }.lparams(matchParent, getActionBarSize(this@FeaturesActivity)) {
                    scrollFlags = SCROLL_FLAG_SCROLL or SCROLL_FLAG_ENTER_ALWAYS or SCROLL_FLAG_SNAP
                }
            }.lparams(matchParent, wrapContent)

            nestedScrollView {
                verticalLayout {
                    cardView {
                        elevation = dip(4).toFloat()
                        isClickable = true
                        foreground = createTouchFeedbackBorderless(this@FeaturesActivity)
                        verticalLayout {
                            textView {
                                val title = "当前版本：V${packageManager.getPackageInfo(packageName, 0).versionName}"
                                text = title
                                textColor = Color.BLACK
                                textSize = 22f
                            }.lparams(wrapContent, wrapContent) {
                                bottomMargin = dip(8)
                            }

                            mTVCurrentContent = textView {
                                textSize = 16f
                            }.lparams(matchParent, wrapContent) {
                                margin = dip(8)
                            }

                        }.lparams(matchParent, wrapContent) {
                            margin = dip(16)
                        }

                    }.lparams(matchParent, wrapContent) {
                        marginStart = dip(4)
                        marginEnd = dip(4)
                        topMargin = dip(8)
                    }

                    cardView {
                        elevation = dip(4).toFloat()
                        isClickable = true
                        foreground = createTouchFeedbackBorderless(this@FeaturesActivity)

                        verticalLayout {
                            textView {
                                val title = "计划开发的新功能"
                                text = title
                                textColor = Color.BLACK
                                textSize = 22f
                            }.lparams(wrapContent, wrapContent) {
                                bottomMargin = dip(8)
                            }

                            mTVNextContent = textView {
                                textSize = 16f
                            }.lparams(matchParent, wrapContent) {
                                margin = dip(8)
                            }

                        }.lparams(matchParent, wrapContent) {
                            margin = dip(16)
                        }

                    }.lparams(matchParent, wrapContent) {
                        marginStart = dip(4)
                        marginEnd = dip(4)
                        topMargin = dip(4)
                    }

                    cardView {
                        elevation = dip(4).toFloat()
                        isClickable = true
                        foreground = createTouchFeedbackBorderless(this@FeaturesActivity)

                        verticalLayout {
                            textView {
                                val title = "Bug反馈"
                                text = title
                                textColor = Color.BLACK
                                textSize = 22f
                            }.lparams(wrapContent, wrapContent) {
                                bottomMargin = dip(8)
                            }

                            mTVBugFeedback1 = textView {
                                textSize = 16f
                            }.lparams(matchParent, matchParent) {
                                topMargin = dip(8)
                            }

                            textView {
                                text = MY_EMAIL
                                textSize = 16f
                                textColor = blue1
                                paint.flags = Paint.UNDERLINE_TEXT_FLAG
                                paint.isAntiAlias = true
                                gravity = Gravity.CENTER_HORIZONTAL
                                setOnClickListener { email(MY_EMAIL, "赫兹词典bug反馈") }
                            }.lparams(wrapContent, wrapContent) {
                                gravity = Gravity.CENTER_HORIZONTAL
                                topMargin = dip(8)
                            }

                            mTVBugFeedback2 = textView {
                                textSize = 16f
                            }.lparams(matchParent, wrapContent) {
                                topMargin = dip(8)
                                marginStart = dip(8)
                                marginEnd = dip(8)
                            }

                            frameLayout {
                                backgroundColorResource = R.color.blueGray
                                foreground = createTouchFeedbackBorderless(this@FeaturesActivity)
                                setOnClickListener { mCompleteScaleImageView.showByCoroutines(this@FeaturesActivity) }
                                GlideApp.with(this@FeaturesActivity).load(R.drawable.wechat).dontAnimate().into(
                                        imageView().lparams(dip(24), dip(24)) {
                                            gravity = Gravity.CENTER_VERTICAL
                                            marginStart = dip(64)
                                            topMargin = dip(16)
                                            bottomMargin = dip(16)
                                        })
                                textView {
                                    text = "我的微信"
                                    textSize = 16f
                                    textColorResource = R.color.wechat
                                }.lparams(wrapContent, wrapContent) {
                                    gravity = Gravity.CENTER
                                }
                            }.lparams(matchParent, wrapContent) {
                                topMargin = dip(24)
                                bottomMargin = dip(8)
                            }

                        }.lparams(matchParent, wrapContent) {
                            margin = dip(16)
                        }

                    }.lparams(matchParent, wrapContent) {
                        topMargin = dip(4)
                        marginStart = dip(4)
                        marginEnd = dip(4)
                    }

                    cardView {
                        elevation = dip(4).toFloat()
                        isClickable = true
                        foreground = createTouchFeedbackBorderless(this@FeaturesActivity)

                        verticalLayout {
                            textView {
                                val title = "一点技术相关"
                                text = title
                                textColor = Color.BLACK
                                textSize = 22f
                            }.lparams(wrapContent, wrapContent) {
                                bottomMargin = dip(8)
                            }

                            mTVAboutTech1 = textView {
                                textSize = 16f
                            }.lparams(matchParent, matchParent) {
                                topMargin = dip(8)
                                marginStart = dip(8)
                                marginEnd = dip(8)
                            }

                            textView {
                                text = GITHUB_ADDRESS
                                textSize = 16f
                                textColor = blue1
                                paint.flags = Paint.UNDERLINE_TEXT_FLAG
                                paint.isAntiAlias = true
                                gravity = Gravity.CENTER_HORIZONTAL
                                setOnClickListener { browse(GITHUB_ADDRESS, true) }
                            }.lparams(wrapContent, wrapContent) {
                                gravity = Gravity.CENTER_HORIZONTAL
                                topMargin = dip(8)
                            }

                            mTVAboutTech2 = textView {
                                textSize = 16f
                            }.lparams(matchParent, wrapContent) {
                                margin = dip(8)
                            }

                        }.lparams(matchParent, wrapContent) {
                            margin = dip(16)
                        }

                    }.lparams(matchParent, wrapContent) {
                        marginStart = dip(4)
                        marginEnd = dip(4)
                        topMargin = dip(4)
                        bottomMargin = dip(8)
                    }

                }.lparams(matchParent, matchParent)

            }.lparams(matchParent, matchParent) {
                behavior = ScrollingViewBehavior()
            }
        }

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        FileReadManagerService.processByCoroutines(this, this, FEATURE_FILE_NAME,
                mTVCurrentContent, mTVNextContent, mTVBugFeedback1,
                mTVBugFeedback2, mTVAboutTech1, mTVAboutTech2)
    }

    override fun onDestroy() {
        super.onDestroy()
        mCompleteScaleImageView.recycler()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> { onBackPressed() }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == mRequestCode) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                mCompleteScaleImageView.restoreImage()
            } else {
                mCompleteScaleImageView.permissionsRejectSnack()
            }
        }
    }

}