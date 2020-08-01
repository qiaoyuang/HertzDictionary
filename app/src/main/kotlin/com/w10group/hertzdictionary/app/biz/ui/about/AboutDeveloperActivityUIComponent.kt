package com.w10group.hertzdictionary.app.biz.ui.about

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
import android.view.Gravity
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.AppBarLayout.LayoutParams.SCROLL_FLAG_EXIT_UNTIL_COLLAPSED
import com.google.android.material.appbar.AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL
import com.google.android.material.appbar.CollapsingToolbarLayout.LayoutParams.COLLAPSE_MODE_PARALLAX
import com.google.android.material.appbar.CollapsingToolbarLayout.LayoutParams.COLLAPSE_MODE_PIN
import com.w10group.hertzdictionary.app.R
import com.w10group.hertzdictionary.app.biz.manager.ImageManagerService
import com.w10group.hertzdictionary.app.biz.ui.features.FeaturesActivity
import com.w10group.hertzdictionary.app.core.CoilDownloader
import com.w10group.hertzdictionary.app.core.image.CompleteScaleImageView
import com.w10group.hertzdictionary.app.core.view.*
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
 * AboutDeveloperActivity 的 UI
 * @author Qiao
 */

class AboutDeveloperActivityUIComponent(private val mAboutDeveloperActivity: AboutDeveloperActivity)
    : AnkoComponent<AboutDeveloperActivity>, LifecycleObserver {

    private companion object {
        const val DEVELOPER_NAME = "Raidriar"

        const val WECHAT_CODE_URL = "https://upload-images.jianshu.io/upload_images/12354730-20bdaff09e53dbdd.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240"
        const val ALIPAY_CODE_URL = "https://upload-images.jianshu.io/upload_images/12354730-c576dd762c8365e5.jpg?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240"

        const val BTC_ADDRESS = "3589aqdNmzCuQeQcYWjF6w9Euq8FsWoDfg"
        const val ETH_ADDRESS = "0x98725b434f875f91604362be9deac3ad38a365fc"
    }

    private val blue1 by lazy { ContextCompat.getColor(mAboutDeveloperActivity, R.color.blue1) }
    private val blueGray by lazy { ContextCompat.getColor(mAboutDeveloperActivity, R.color.blueGray) }
    private val coin by lazy { ContextCompat.getColor(mAboutDeveloperActivity, R.color.coin) }

    private lateinit var mToolbar: Toolbar

    lateinit var mIMBackground: ImageView
        private set

    lateinit var mIMAvatar: ImageView
        private set

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

    override fun createView(ui: AnkoContext<AboutDeveloperActivity>): View = ui.apply {
        coordinatorLayout {
            fitsSystemWindows = true
            backgroundColorResource = R.color.deepWhite

            appBarLayout {
                fitsSystemWindows = true
                elevation = dip(4).toFloat()

                collapsingToolbarLayout {
                    fitsSystemWindows = true
                    title = context.getString(R.string.mine)
                    setContentScrimColor(blue1)
                    setExpandedTitleColor(Color.TRANSPARENT)

                    mIMBackground = imageView {
                        fitsSystemWindows = true
                        scaleType = ImageView.ScaleType.CENTER_CROP
                        foreground = createTouchFeedbackBorderless(context)
                        setOnClickListener {  mCompleteScaleImageView.show(1) }
                    }.lparams(matchParent, matchParent) {
                        collapseMode = COLLAPSE_MODE_PARALLAX
                    }

                    mIMAvatar = circleImageView {
                        scaleType = ImageView.ScaleType.CENTER_CROP
                        borderWidth = dip(2)
                        borderColor = Color.WHITE
                        foreground = createTouchFeedbackBorderless(context)
                        setOnClickListener { mCompleteScaleImageView.show() }
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
                            setText(R.string.new_feature)
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
                            setOnClickListener { mReceiptCompleteScaleImageView.show() }
                            imageView().lparams(dip(24), dip(24)) {x
                                gravity = Gravity.CENTER_VERTICAL
                                marginStart = dip(32)
                                topMargin = dip(16)
                                bottomMargin = dip(16)
                            }.loadResId(R.drawable.wechatpay, mAboutDeveloperActivity.lifecycle)
                            textView {
                                setText(R.string.wechat_receive_code)
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
                            setOnClickListener { mReceiptCompleteScaleImageView.show(1) }
                            imageView().lparams(dip(24), dip(24)) {
                                gravity = Gravity.CENTER_VERTICAL
                                marginStart = dip(32)
                                topMargin = dip(16)
                                bottomMargin = dip(16)
                            }.loadResId(R.drawable.alipay, mAboutDeveloperActivity.lifecycle)
                            textView {
                                setText(R.string.alipay_receive_code)
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
                            val content = context.getString(R.string.btc_address)
                            setOnClickListener { copyToClipBoard(it, content, BTC_ADDRESS) }
                            imageView().lparams(dip(24), dip(24)) {
                                gravity = Gravity.CENTER_VERTICAL
                                marginStart = dip(32)
                                topMargin = dip(16)
                                bottomMargin = dip(16)
                            }.loadResId(R.drawable.btc, mAboutDeveloperActivity.lifecycle)
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
                            val content = context.getString(R.string.eth_address)
                            setOnClickListener { copyToClipBoard(it, content, ETH_ADDRESS) }
                            imageView().lparams(dip(24), dip(24)) {
                                gravity = Gravity.CENTER_VERTICAL
                                marginStart = dip(32)
                                topMargin = dip(16)
                                bottomMargin = dip(16)
                            }.loadResId(R.drawable.eth, mAboutDeveloperActivity.lifecycle)
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
                behavior = AppBarLayout.ScrollingViewBehavior()
            }

        }
    }.view

    @Suppress("unused")
    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    private fun init() {
        mAboutDeveloperActivity.setSupportActionBar(mToolbar)
        mAboutDeveloperActivity.supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    @Suppress("unused")
    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    private fun recycler() {
        mCompleteScaleImageView.recycler()
        mReceiptCompleteScaleImageView.recycler()
    }

    private fun createCompleteScaleImageView(list: MutableList<String>, requestCode: Int): CompleteScaleImageView =
            CompleteScaleImageView(mAboutDeveloperActivity, CoilDownloader, requestCode).apply {
                isDownloaderEnable = true
                mUrls = list
            }

    private fun copyToClipBoard(view: View, content: String, address: String) {
        val cmb = mAboutDeveloperActivity.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        cmb.setPrimaryClip(ClipData.newPlainText("label", address))
        view.snackbar(mAboutDeveloperActivity.getString(R.string.copy_down, content))
    }

    fun updateTextView(result: List<String>) {
        mTVContent1.text = result[0]
        mTVContent2.text = result[1]
        mTVContent3.text = result[2]
    }

    fun requestPermissionsResult(requestCode: Int, grantResults: IntArray) = with(when (requestCode) {
        mCompleteRequestCode -> mCompleteScaleImageView
        mReceiptRequestCode -> mReceiptCompleteScaleImageView
        else -> throw IllegalStateException("requestCode 不正确")
    }) {
        if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            restoreImage()
        else
            permissionsRejectSnack()
    }

}