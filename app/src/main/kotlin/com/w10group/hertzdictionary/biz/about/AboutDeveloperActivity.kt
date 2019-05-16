package com.w10group.hertzdictionary.biz.about

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.support.design.widget.AppBarLayout.LayoutParams.SCROLL_FLAG_EXIT_UNTIL_COLLAPSED
import android.support.design.widget.AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL
import android.support.design.widget.AppBarLayout.ScrollingViewBehavior
import android.support.design.widget.CollapsingToolbarLayout.LayoutParams.COLLAPSE_MODE_PARALLAX
import android.support.design.widget.CollapsingToolbarLayout.LayoutParams.COLLAPSE_MODE_PIN
import android.support.v4.content.ContextCompat
import android.support.v7.widget.Toolbar
import android.view.Gravity
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.w10group.hertzdictionary.R
import com.w10group.hertzdictionary.biz.features.FeaturesActivity
import com.w10group.hertzdictionary.biz.manager.FileReadManagerService
import com.w10group.hertzdictionary.biz.manager.ImageManagerService
import com.w10group.hertzdictionary.core.*
import com.w10group.hertzdictionary.core.image.CompleteScaleImageView
import com.w10group.hertzdictionary.core.view.circleImageView
import com.w10group.hertzdictionary.core.view.createTouchFeedbackBorderless
import com.w10group.hertzdictionary.core.view.getActionBarSize
import kotlinx.coroutines.launch
import org.jetbrains.anko.*
import org.jetbrains.anko.appcompat.v7.toolbar
import org.jetbrains.anko.cardview.v7.cardView
import org.jetbrains.anko.design.appBarLayout
import org.jetbrains.anko.design.collapsingToolbarLayout
import org.jetbrains.anko.design.coordinatorLayout
import org.jetbrains.anko.design.snackbar
import org.jetbrains.anko.support.v4.nestedScrollView
import java.util.*

/**
 * Created by Administrator on 2018/6/25.
 * 关于开发者Activity
 */

class AboutDeveloperActivity : CoroutineScopeActivity() {

    private companion object {
        const val DEVELOPER_NAME = "Raidriar"
        const val ABOUT_ME_FILE_NAME = "about_me.txt"

        const val WECHAT_CODE_URL = "https://upload-images.jianshu.io/upload_images/12354730-20bdaff09e53dbdd.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240"
        const val ALIPAY_CODE_URL = "https://upload-images.jianshu.io/upload_images/12354730-c576dd762c8365e5.jpg?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240"

        const val BTC_ADDRESS = "3589aqdNmzCuQeQcYWjF6w9Euq8FsWoDfg"
        const val ETH_ADDRESS = "0x98725b434f875f91604362be9deac3ad38a365fc"
    }

    private val blue1 by lazy { ContextCompat.getColor(this, R.color.blue1) }
    private val blueGray by lazy { ContextCompat.getColor(this, R.color.blueGray) }
    private val coin by lazy { ContextCompat.getColor(this, R.color.coin) }

    private lateinit var mToolbar: Toolbar
    private lateinit var mIMBackground: ImageView
    private lateinit var mIMAvatar: ImageView
    private lateinit var mTVContent1: TextView
    private lateinit var mTVContent2: TextView
    private lateinit var mTVContent3: TextView
    private lateinit var mTVFeature: TextView

    private val mCompleteRequestCode = 1
    private val mCompleteScaleImageView by lazy { createCompleteScaleImageView(ImageManagerService.urlList, mCompleteRequestCode) }

    private val mReceiptCodeUrlList by lazy {
        val list = LinkedList<String>()
        list.add(WECHAT_CODE_URL)
        list.add(ALIPAY_CODE_URL)
        list
    }

    private val mReceiptRequestCode = 2
    private val mReceiptCompleteScaleImageView by lazy { createCompleteScaleImageView(mReceiptCodeUrlList, mReceiptRequestCode) }

    private fun createCompleteScaleImageView(list: MutableList<String>, requestCode: Int): CompleteScaleImageView {
        val completeScaleImageView = CompleteScaleImageView(this, GlideDownloader, requestCode)
        completeScaleImageView.setDownloadEnable(true)
        completeScaleImageView.mUrls = list
        return completeScaleImageView
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        coordinatorLayout {
            fitsSystemWindows = true
            backgroundColorResource = R.color.deepWhite

            appBarLayout {
                fitsSystemWindows = true
                elevation = dip(4).toFloat()

                collapsingToolbarLayout {
                    fitsSystemWindows = true
                    title = "关于开发者"
                    setContentScrimColor(blue1)
                    setExpandedTitleColor(Color.TRANSPARENT)

                    mIMBackground = imageView {
                        fitsSystemWindows = true
                        scaleType = ImageView.ScaleType.CENTER_CROP
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                            foreground = createTouchFeedbackBorderless(context)
                        setOnClickListener {  mCompleteScaleImageView.showByCoroutines(this@AboutDeveloperActivity, 1) }
                    }.lparams(matchParent, matchParent) {
                        collapseMode = COLLAPSE_MODE_PARALLAX
                    }

                    mIMAvatar = circleImageView {
                        scaleType = ImageView.ScaleType.CENTER_CROP
                        borderWidth = dip(2)
                        borderColor = Color.WHITE
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                            foreground = createTouchFeedbackBorderless(context)
                        setOnClickListener { mCompleteScaleImageView.showByCoroutines(this@AboutDeveloperActivity) }
                    }.lparams(dip(80), dip(80)) {
                        gravity = Gravity.CENTER_HORIZONTAL or Gravity.TOP
                        topMargin = dip(88)
                        collapseMode = COLLAPSE_MODE_PARALLAX
                    }

                    textView {
                        textSize = 20f
                        textColor = Color.WHITE
                        text = DEVELOPER_NAME
                    }.lparams(wrapContent, wrapContent) {
                        gravity = Gravity.CENTER_HORIZONTAL or Gravity.BOTTOM
                        bottomMargin = dip(48)
                        collapseMode = COLLAPSE_MODE_PARALLAX
                    }

                    mToolbar = toolbar().lparams(matchParent, getActionBarSize(context)) {
                        collapseMode = COLLAPSE_MODE_PIN
                    }

                }.lparams(matchParent, matchParent) {
                    scrollFlags = SCROLL_FLAG_SCROLL or SCROLL_FLAG_EXIT_UNTIL_COLLAPSED
                }
            }.lparams(matchParent, dip(256))

            nestedScrollView {

                cardView {
                    elevation = dip(4).toFloat()
                    isClickable = true
                    foreground = createTouchFeedbackBorderless(context)

                    verticalLayout {

                        mTVContent1 = textView {
                            textSize = 16f
                        }.lparams(matchParent, wrapContent)

                        mTVFeature = textView {
                            text = "未来新功能"
                            textSize = 16f
                            textColor = blue1
                            setOnClickListener { startActivity<FeaturesActivity>() }
                        }.lparams(wrapContent, wrapContent) {
                            gravity = Gravity.CENTER_HORIZONTAL
                            topMargin = dip(8)
                        }

                        mTVContent2 = textView {
                            textSize = 16f
                        }.lparams(matchParent, wrapContent) {
                            topMargin = dip(8)
                        }



                        frameLayout {
                            backgroundColor = blueGray
                            foreground = createTouchFeedbackBorderless(context)
                            setOnClickListener { mReceiptCompleteScaleImageView.showByCoroutines(this@AboutDeveloperActivity) }
                            GlideApp.with(this@AboutDeveloperActivity).load(R.drawable.wechatpay).dontAnimate().into(
                                    imageView().lparams(dip(24), dip(24)) {
                                        gravity = Gravity.CENTER_VERTICAL
                                        marginStart = dip(32)
                                        topMargin = dip(16)
                                        bottomMargin = dip(16)
                            })
                            textView {
                                text = "微信收款码"
                                textSize = 16f
                                textColorResource = R.color.wechat
                            }.lparams(wrapContent, wrapContent) {
                                gravity = Gravity.CENTER
                            }
                        }.lparams(matchParent, wrapContent) {
                            topMargin = dip(24)
                        }



                        frameLayout {
                            backgroundColor = blueGray
                            foreground = createTouchFeedbackBorderless(context)
                            setOnClickListener { mReceiptCompleteScaleImageView.showByCoroutines(this@AboutDeveloperActivity,1) }
                            GlideApp.with(this@AboutDeveloperActivity).load(R.drawable.alipay).dontAnimate().into(
                                    imageView().lparams(dip(24), dip(24)) {
                                        gravity = Gravity.CENTER_VERTICAL
                                        marginStart = dip(32)
                                        topMargin = dip(16)
                                        bottomMargin = dip(16)
                                    })
                            textView {
                                text = "支付宝收款码"
                                textSize = 16f
                                textColorResource = R.color.alipay
                            }.lparams(wrapContent, wrapContent) {
                                gravity = Gravity.CENTER
                            }
                        }.lparams(matchParent, wrapContent) {
                            topMargin = dip(8)
                        }



                        mTVContent3 = textView {
                            textSize = 16f
                        }.lparams(matchParent, wrapContent) {
                            topMargin = dip(24)
                        }



                        frameLayout {
                            backgroundColor = blueGray
                            foreground = createTouchFeedbackBorderless(context)
                            val content = "BTC钱包地址"
                            setOnClickListener { copyToClipBoard(it, content, BTC_ADDRESS) }
                            GlideApp.with(this@AboutDeveloperActivity).load(R.drawable.btc).dontAnimate().into(
                                    imageView().lparams(dip(24), dip(24)) {
                                        gravity = Gravity.CENTER_VERTICAL
                                        marginStart = dip(32)
                                        topMargin = dip(16)
                                        bottomMargin = dip(16)
                                    })
                            textView {
                                text = content
                                textSize = 16f
                                textColor = coin
                            }.lparams(wrapContent, wrapContent) {
                                gravity = Gravity.CENTER
                            }
                        }.lparams(matchParent, wrapContent) {
                            topMargin = dip(24)
                        }



                        frameLayout {
                            backgroundColor = blueGray
                            foreground = createTouchFeedbackBorderless(context)
                            val content = "ETH钱包地址"
                            setOnClickListener { copyToClipBoard(it, content, ETH_ADDRESS) }
                            GlideApp.with(this@AboutDeveloperActivity).load(R.drawable.eth).dontAnimate().into(
                                    imageView().lparams(dip(24), dip(24)) {
                                        gravity = Gravity.CENTER_VERTICAL
                                        marginStart = dip(32)
                                        topMargin = dip(16)
                                        bottomMargin = dip(16)
                                    })
                            textView {
                                text = content
                                textSize = 16f
                                textColor = coin
                            }.lparams(wrapContent, wrapContent) {
                                gravity = Gravity.CENTER
                            }
                        }.lparams(matchParent, wrapContent) {
                            topMargin = dip(8)
                        }



                    }.lparams(matchParent, wrapContent) {
                        topMargin = dip(16)
                        bottomMargin = dip(24)
                        marginStart = dip(8)
                        marginEnd = dip(8)
                    }

                }.lparams(matchParent, wrapContent) {
                    margin = dip(4)
                }

            }.lparams(matchParent, matchParent) {
                behavior = ScrollingViewBehavior()
            }

        }

        setSupportActionBar(mToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        launch { ImageManagerService.loadBackground(this@AboutDeveloperActivity, mIMBackground) }
        ImageManagerService.loadAvatar(this, mIMAvatar)
        FileReadManagerService.processByCoroutines(this, this, ABOUT_ME_FILE_NAME, mTVContent1, mTVContent2, mTVContent3)
    }

    override fun onDestroy() {
        super.onDestroy()
        mCompleteScaleImageView.recycler()
        mReceiptCompleteScaleImageView.recycler()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            mCompleteRequestCode -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mCompleteScaleImageView.restoreImage()
                } else {
                    mCompleteScaleImageView.permissionsRejectSnack()
                }
            }
            mReceiptRequestCode -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mReceiptCompleteScaleImageView.restoreImage()
                } else {
                    mReceiptCompleteScaleImageView.permissionsRejectSnack()
                }
            }
        }
    }

    private fun copyToClipBoard(view: View, content: String, address: String) {
        val cmb = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        cmb.primaryClip = ClipData.newPlainText("label", address)
        view.snackbar("${content}已复制到剪贴板")
    }

}