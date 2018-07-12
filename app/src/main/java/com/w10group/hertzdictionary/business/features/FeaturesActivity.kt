package com.w10group.hertzdictionary.business.features

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.Paint
import android.net.Uri
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
import com.w10group.hertzdictionary.core.GlideApp
import com.w10group.hertzdictionary.core.GlideDownloader
import com.w10group.hertzdictionary.core.createTouchFeedbackBorderless
import com.w10group.hertzdictionary.core.image.CompleteScaleImageView
import org.jetbrains.anko.*
import org.jetbrains.anko.appcompat.v7.toolbar
import org.jetbrains.anko.cardview.v7.cardView
import org.jetbrains.anko.design.appBarLayout
import org.jetbrains.anko.design.coordinatorLayout
import org.jetbrains.anko.support.v4.nestedScrollView
import java.util.*

class FeaturesActivity : AppCompatActivity() {

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
                                setOnClickListener { sendEmail() }
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
                                setOnClickListener { mCompleteScaleImageView.show() }
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
                        topMargin = dip(8)
                        marginStart = dip(8)
                        marginEnd = dip(8)
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
                                setOnClickListener { openBrowser() }
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

    private fun openBrowser() {
        val intent = Intent()
        intent.action = "android.intent.action.VIEW"
        val uri = Uri.parse(GITHUB_ADDRESS)
        intent.data = uri
        startActivity(intent)
    }

    private fun sendEmail() {
        val subject = "赫兹词典bug反馈"
        val content = "mailto:$MY_EMAIL?subject=$subject"
        val intent = Intent(Intent.ACTION_SENDTO)
        intent.data = Uri.parse(content)
        startActivity(intent)
    }

}