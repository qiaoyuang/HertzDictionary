package com.w10group.hertzdictionary.business.about

import android.graphics.Color
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.AppBarLayout.LayoutParams.SCROLL_FLAG_EXIT_UNTIL_COLLAPSED
import android.support.design.widget.AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL
import android.support.design.widget.AppBarLayout.ScrollingViewBehavior
import android.support.design.widget.CollapsingToolbarLayout
import android.support.design.widget.CollapsingToolbarLayout.LayoutParams.COLLAPSE_MODE_PARALLAX
import android.support.design.widget.CollapsingToolbarLayout.LayoutParams.COLLAPSE_MODE_PIN
import android.support.v4.content.ContextCompat
import android.support.v7.widget.Toolbar
import android.view.Gravity
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import com.w10group.hertzdictionary.R
import com.w10group.hertzdictionary.business.features.FeaturesActivity
import com.w10group.hertzdictionary.business.manager.FileReadManagerService
import com.w10group.hertzdictionary.business.manager.ImageManagerService
import com.w10group.hertzdictionary.core.ActionBarSize
import com.w10group.hertzdictionary.core.circleImageView
import com.w10group.hertzdictionary.core.createTouchFeedbackBorderless
import org.jetbrains.anko.*
import org.jetbrains.anko.appcompat.v7.toolbar
import org.jetbrains.anko.cardview.v7.cardView
import org.jetbrains.anko.design.appBarLayout
import org.jetbrains.anko.design.collapsingToolbarLayout
import org.jetbrains.anko.design.coordinatorLayout
import org.jetbrains.anko.support.v4.nestedScrollView

class AboutDeveloperActivity : AppCompatActivity() {

    private companion object {
        const val DEVELOPER_NAME = "Raidriar"
        const val ABOUT_ME_FILE_NAME = "about_me.txt"
    }

    private val blue1 by lazy { ContextCompat.getColor(this, R.color.blue1) }

    private lateinit var mCollapsingToolbarLayout: CollapsingToolbarLayout
    private lateinit var mToolbar: Toolbar
    private lateinit var mIMBackground: ImageView
    private lateinit var mIMAvatar: ImageView
    private lateinit var mTVContent1: TextView
    private lateinit var mTVContent2: TextView
    private lateinit var mTVFeature: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        coordinatorLayout {
            fitsSystemWindows = true
            backgroundColorResource = R.color.deepWhite

            appBarLayout {
                fitsSystemWindows = true
                elevation = dip(4).toFloat()
                translationZ = dip(4).toFloat()

                mCollapsingToolbarLayout = collapsingToolbarLayout {
                    fitsSystemWindows = true
                    setContentScrimColor(blue1)

                    mIMBackground = imageView {
                        fitsSystemWindows = true
                        scaleType = ImageView.ScaleType.CENTER_CROP
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                            foreground = createTouchFeedbackBorderless(this@AboutDeveloperActivity)
                        setOnClickListener {

                        }
                    }.lparams(matchParent, matchParent) {
                        collapseMode = COLLAPSE_MODE_PARALLAX
                    }

                    mIMAvatar = circleImageView {
                        scaleType = ImageView.ScaleType.CENTER_CROP
                        borderWidth = dip(2)
                        borderColor = Color.WHITE
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                            foreground = createTouchFeedbackBorderless(this@AboutDeveloperActivity)
                        setOnClickListener {

                        }
                    }.lparams(dip(80), dip(80)) {
                        gravity = Gravity.CENTER_HORIZONTAL or Gravity.TOP
                        topMargin = dip(88)
                    }

                    textView {
                        textSize = 20f
                        textColor = Color.WHITE
                        text = DEVELOPER_NAME
                    }.lparams(wrapContent, wrapContent) {
                        gravity = Gravity.CENTER_HORIZONTAL or Gravity.BOTTOM
                        bottomMargin = dip(48)
                    }

                    mToolbar = toolbar {
                        title = "关于开发者"
                    }.lparams(matchParent, ActionBarSize.get(this@AboutDeveloperActivity)) {
                        collapseMode = COLLAPSE_MODE_PIN
                    }

                }.lparams(matchParent, matchParent) {
                    scrollFlags = SCROLL_FLAG_SCROLL or SCROLL_FLAG_EXIT_UNTIL_COLLAPSED
                }
            }.lparams(matchParent, dip(256))

            nestedScrollView {

                cardView {
                    elevation = dip(4).toFloat()
                    translationZ = dip(4).toFloat()
                    isClickable = true
                    foreground = createTouchFeedbackBorderless(this@AboutDeveloperActivity)

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

                        button {
                            text = "微信收款码"
                            textSize = 16f
                            backgroundColorResource = R.color.wechat
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                                foreground = createTouchFeedbackBorderless(this@AboutDeveloperActivity)
                        }.lparams(matchParent, wrapContent) {
                            topMargin = dip(24)
                        }

                        button {
                            text = "支付宝收款码"
                            textSize = 16f
                            backgroundColorResource = R.color.alipay
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                                foreground = createTouchFeedbackBorderless(this@AboutDeveloperActivity)
                        }.lparams(matchParent, wrapContent) {
                            topMargin = dip(8)
                        }

                    }.lparams(matchParent, wrapContent) {
                        topMargin = dip(16)
                        bottomMargin = dip(16)
                        marginStart = dip(8)
                        marginEnd = dip(8)
                    }

                }.lparams(matchParent, wrapContent) {
                    margin = dip(8)
                }

            }.lparams(matchParent, matchParent) {
                behavior = ScrollingViewBehavior()
            }

        }

        setSupportActionBar(mToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        val transparent = ContextCompat.getColor(this, android.R.color.transparent)
        mCollapsingToolbarLayout.setExpandedTitleColor(transparent)

        ImageManagerService.loadBackground(this, mIMBackground)
        ImageManagerService.loadAvatar(this, mIMAvatar)
        FileReadManagerService.process(ABOUT_ME_FILE_NAME, this, mTVContent1, mTVContent2)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> { onBackPressed() }
        }
        return super.onOptionsItemSelected(item)
    }

}