package com.w10group.hertzdictionary.biz.ui.features

import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.Paint
import android.view.Gravity
import android.view.View
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.AppBarLayout.LayoutParams.*
import com.w10group.hertzdictionary.R
import com.w10group.hertzdictionary.core.GlideApp
import com.w10group.hertzdictionary.core.GlideDownloader
import com.w10group.hertzdictionary.core.architecture.UIComponent
import com.w10group.hertzdictionary.core.image.CompleteScaleImageView
import com.w10group.hertzdictionary.core.view.createTouchFeedbackBorderless
import com.w10group.hertzdictionary.core.view.getActionBarSize
import org.jetbrains.anko.*
import org.jetbrains.anko.appcompat.v7.toolbar
import org.jetbrains.anko.cardview.v7.cardView
import org.jetbrains.anko.design.appBarLayout
import org.jetbrains.anko.design.coordinatorLayout
import org.jetbrains.anko.design.snackbar
import org.jetbrains.anko.support.v4.nestedScrollView

/**
 * FeaturesActivity 的 UI
 * @author Qiao
 */

class FeaturesActivityUIComponent(private val mFeatureActivity: FeaturesActivity) : UIComponent<FeaturesActivity>() {

    private companion object {
        const val MY_EMAIL = "qiaoyuang2012@gmail.com"
        const val GITHUB_ADDRESS = "https://github.com/qiaoyuang/HertzDictionary"
        const val WECHAT_CODE = "https://upload-images.jianshu.io/upload_images/12354730-f08c7c37c316d1d8.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240"
    }

    private val mRequestCode = 1
    private val mCompleteScaleImageView by lazy {
        CompleteScaleImageView(mFeatureActivity, GlideDownloader, mRequestCode).apply {
            isDownloaderEnable = true
            mUrls = arrayListOf(WECHAT_CODE)
        }
    }

    private lateinit var mToolbar: Toolbar

    private lateinit var mTVCurrentContent: TextView
    private lateinit var mTVNextContent: TextView
    private lateinit var mTVBugFeedback1: TextView
    private lateinit var mTVBugFeedback2: TextView
    private lateinit var mTVAboutTech1: TextView

    private val blue1 by lazy { ContextCompat.getColor(mFeatureActivity, R.color.blue1) }

    override fun createView(ui: AnkoContext<FeaturesActivity>): View = ui.apply {
        coordinatorLayout {
            backgroundColorResource = R.color.deepWhite
            appBarLayout {
                translationZ = dip(8).toFloat()
                mToolbar = toolbar {
                    title = context.getString(R.string.more_features)
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
                        foreground = createTouchFeedbackBorderless(context)
                        verticalLayout {
                            textView {
                                val title = context.getString(R.string.current_version,
                                        context.packageManager.getPackageInfo(context.packageName, 0).versionName)
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
                        foreground = createTouchFeedbackBorderless(context)

                        verticalLayout {
                            textView {
                                setText(R.string.plan_feature)
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
                        foreground = createTouchFeedbackBorderless(context)

                        verticalLayout {
                            textView {
                                setText(R.string.bug_feedback)
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
                                setOnClickListener { email(MY_EMAIL, context.getString(R.string.email_subject)) }
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
                                foreground = createTouchFeedbackBorderless(context)
                                setOnClickListener { mCompleteScaleImageView.showByCoroutines() }
                                GlideApp.with(mFeatureActivity).load(R.drawable.wechat).dontAnimate().into(
                                        imageView().lparams(dip(24), dip(24)) {
                                            gravity = Gravity.CENTER_VERTICAL
                                            marginStart = dip(64)
                                            topMargin = dip(16)
                                            bottomMargin = dip(16)
                                        })
                                textView {
                                    setText(R.string.my_wechat)
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
                        foreground = createTouchFeedbackBorderless(context)

                        verticalLayout {
                            textView {
                                setText(R.string.about_tech)
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
                                bottomMargin = dip(8)
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

                }.lparams(matchParent, wrapContent)

            }.lparams(matchParent, matchParent) {
                behavior = AppBarLayout.ScrollingViewBehavior()
            }
        }
    }.view

    override fun init() {
        mFeatureActivity.setSupportActionBar(mToolbar)
        mFeatureActivity.supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun recycler() = mCompleteScaleImageView.recycler()

    fun updateTextView(result: List<String>) {
        if (result.size > 4) {
            mTVCurrentContent.text = result[0]
            mTVNextContent.text = result[1]
            mTVBugFeedback1.text = result[2]
            mTVBugFeedback2.text = result[3]
            mTVAboutTech1.text = result[4]
        } else mToolbar.snackbar(R.string.file_load_error)
    }

    fun requestPermissionsResult(requestCode: Int, grantResults: IntArray) {
        if (requestCode == mRequestCode) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                mCompleteScaleImageView.restoreImage()
            } else {
                mCompleteScaleImageView.permissionsRejectSnack()
            }
        }
    }

}