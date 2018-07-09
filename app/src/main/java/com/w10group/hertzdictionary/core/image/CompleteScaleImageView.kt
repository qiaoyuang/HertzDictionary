package com.w10group.hertzdictionary.core.image

import android.app.Dialog
import android.content.Context
import android.view.Gravity
import android.view.View
import com.w10group.hertzdictionary.R
import org.jetbrains.anko.*
import org.jetbrains.anko.support.v4.viewPager

class CompleteScaleImageView(private val mContext: Context,
                             private val mImageDownloader: ImageDownloader) {

    private val mDialog = Dialog(mContext, R.style.Dialog_Fullscreen)

    init {
        mDialog.setContentView(initView())

    }

    private fun initView(): View =
        AnkoContext.create(mContext).apply {
            frameLayout {
                viewPager().lparams(matchParent, matchParent)

                imageView {
                    imageResource = R.drawable.ic_close_white_18dp
                }.lparams(wrapContent, wrapContent) {
                    gravity = Gravity.START or Gravity.TOP
                    marginEnd = dip(24)
                    topMargin = dip(24)
                }

                imageView {
                    imageResource = R.drawable.ic_close_white_18dp
                }.lparams(wrapContent, wrapContent) {
                    gravity = Gravity.END or Gravity.TOP
                    marginEnd= dip(24)
                    topMargin = dip(24)
                }

                imageView {
                    imageResource = R.drawable.ic_close_white_18dp
                }.lparams(wrapContent, wrapContent) {
                    gravity = Gravity.END or Gravity.BOTTOM
                    marginEnd= dip(24)
                    bottomMargin = dip(24)
                }

                textView {
                    textSize = 16f
                }.lparams(wrapContent, wrapContent) {
                    gravity = Gravity.CENTER_HORIZONTAL or Gravity.BOTTOM
                    bottomMargin = dip(32)
                }

            }
        }.view

}