package com.w10group.hertzdictionary.core.image

import android.animation.ArgbEvaluator
import android.animation.ObjectAnimator
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.net.Uri
import android.provider.MediaStore
import android.support.v4.view.ViewPager
import android.view.Gravity
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import com.davemorrissey.labs.subscaleview.ImageSource
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView
import com.w10group.hertzdictionary.R
import com.w10group.hertzdictionary.core.subsamplingImageView
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import org.jetbrains.anko.*
import org.jetbrains.anko.design.snackbar
import org.jetbrains.anko.support.v4.viewPager
import java.io.File
import java.util.*

/**
 * Created by qiaoyuang on 2018/7/9.
 * 大图查看器封装，图片下载的时候还可以多利用线程池的并发进行重构
 */

class CompleteScaleImageView(private val mContext: Context,
                             private val mImageDownloader: ImageDownloader) {

    private lateinit var mViewPager: ViewPager
    private val mAdapter by lazy { DialogPagerAdapter(mViews, mDialog) }

    private lateinit var mIVBack: ImageView//后退按钮
    private lateinit var mIVDownload: ImageView//下载按钮
    private lateinit var mIVDelete: ImageView//删除按钮
    private lateinit var mTVImageCount: TextView//显示当前为第几页的TextView

    private val mDialog = Dialog(mContext, R.style.Dialog_Fullscreen)

    private val mViews by lazy { LinkedList<View>() }
    private val mDownloadFiles by lazy { LinkedList<File>() }//下载图片列表，当使用网络查看模式时，该列表保存所有下载下来的完整尺寸图片

    private var mSelectedPosition = 0//当前选中的位置

    companion object {
        const val URL = 0
        const val FILE = 1
        const val PROGRESS_BAR_ID = 10
        const val SUBSAMPLING_ID = 11
    }

    //标示当前状态是网络图片显示模式还是本地图片显示模式
    private var mStatus = URL

    var mUrls: MutableList<String>? = null
        set(value) {
            field = value
            mStatus = URL
            if (mViews.isNotEmpty())
                mViews.clear()
            if (mDownloadFiles.isNotEmpty())
                mDownloadFiles.clear()
        }

    var mFiles: MutableList<File>? = null
        set(value) {
            field = value
            mStatus = FILE
            if (mViews.isNotEmpty())
                mViews.clear()
        }

    private val mAnim by lazy {
        val colorAnim = ObjectAnimator.ofInt(mTVImageCount, "textColor", Color.WHITE, Color.TRANSPARENT)
        colorAnim.duration = 1500
        colorAnim.setEvaluator(ArgbEvaluator())
        colorAnim
    }

    init { mDialog.setContentView(initView()) }

    private fun initView(): View =
        AnkoContext.create(mContext).apply {
            frameLayout {
                backgroundColor = Color.BLACK
                mViewPager = viewPager {
                    addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
                        override fun onPageScrollStateChanged(state: Int) {}
                        override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}
                        override fun onPageSelected(position: Int) {
                            mSelectedPosition = position
                            val text = "${position + 1}/${mViews.size}"
                            mTVImageCount.text = text
                            mAnim.start()
                        }
                    })
                }.lparams(matchParent, matchParent)

                mIVBack = imageView {
                    imageResource = R.drawable.ic_arrow_back_white_24dp
                    setPadding(dip(24), dip(24), 0, 0)
                    setOnClickListener { mDialog.dismiss() }
                }.lparams(wrapContent, wrapContent) {
                    gravity = Gravity.START or Gravity.TOP
                }

                mIVDelete = imageView {
                    imageResource = R.drawable.ic_delete_white_24dp
                    visibility = View.INVISIBLE
                    setPadding(0, dip(24), dip(24), 0)
                    setOnClickListener {
                        val size = mViews.size
                        if (mStatus == URL) {
                            mUrls?.removeAt(mSelectedPosition)
                        } else if (mStatus == FILE) {
                            mFiles?.removeAt(mSelectedPosition)
                        }
                        onDelete(mSelectedPosition)
                        mViewPager.removeViewAt(mSelectedPosition)
                        if (mSelectedPosition != size) {
                            val text = "${mSelectedPosition + 1}/${mViews.size}"
                            mTVImageCount.text = text
                        }
                        mAdapter.notifyDataSetChanged()
                    }
                }.lparams(wrapContent, wrapContent) {
                    gravity = Gravity.END or Gravity.TOP
                }

                mIVDownload = imageView {
                    imageResource = R.drawable.ic_file_download_white_24dp
                    visibility = View.INVISIBLE
                    setPadding(0, 0, dip(24), dip(24))
                    setOnClickListener {
                        val file = mDownloadFiles[mSelectedPosition]
                        MediaStore.Images.Media.insertImage(mContext.contentResolver,
                                file.absolutePath, file.name, null)
                        snackbar(it, "图片保存成功")
                    }
                }.lparams(wrapContent, wrapContent) {
                    gravity = Gravity.END or Gravity.BOTTOM
                }

                mTVImageCount = textView {
                    textSize = 18f
                    textColor = Color.WHITE
                }.lparams(wrapContent, wrapContent) {
                    gravity = Gravity.CENTER_HORIZONTAL or Gravity.BOTTOM
                    bottomMargin = dip(48)
                }

            }
        }.view

    private fun createItemView(): View =
        AnkoContext.create(mContext).apply {
            frameLayout {
                progressBar {
                    id = PROGRESS_BAR_ID
                    visibility = View.VISIBLE
                }.lparams(wrapContent, wrapContent) {
                    gravity = Gravity.CENTER
                }
                subsamplingImageView {
                    id = SUBSAMPLING_ID
                    setOnClickListener { mAnim.start() }
                }.lparams(matchParent, matchParent)
            }
        }.view

    fun setDeleteUnable() { mIVDelete.visibility = View.INVISIBLE }

    private var onDelete = { _: Int -> Unit }
    fun setDeleteEnable(onDelete: (Int) -> Unit) {
        mIVDelete.visibility = View.VISIBLE
        this.onDelete = onDelete
    }

    //设置下载按钮是否启用
    fun setDownloadEnable(isEnable: Boolean) {
        mIVDownload.visibility = if (isEnable) {
            View.VISIBLE
        } else View.INVISIBLE
    }

    fun show(startPosition: Int = 0) {
        if (mViews.isEmpty()) {
            if (mStatus == URL) {
                mUrls?.let { urls: MutableList<String> ->
                    for (i in 0 until urls.size) {
                        mViews.add(createItemView())
                    }
                    initShow(startPosition)
                    var index = 0
                    Observable.create<File> {
                        for (url in urls) {
                            val downLoadFile = mImageDownloader.download(url, mContext)
                            mDownloadFiles.add(downLoadFile)
                            it.onNext(downLoadFile)
                        }
                        it.onComplete()
                    }
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribeBy {
                                mViews[index].find<SubsamplingScaleImageView>(SUBSAMPLING_ID).setImage(ImageSource.uri(Uri.fromFile(it)))
                                mViews[index].find<ProgressBar>(PROGRESS_BAR_ID).visibility = View.INVISIBLE
                                index++
                            }
                }
            } else if (mStatus == FILE) {
                mFiles?.let {
                    for (file in it) {
                        val view = createItemView()
                        val subsamplingScaleImageView = view.find<SubsamplingScaleImageView>(SUBSAMPLING_ID)
                        mViews.add(subsamplingScaleImageView)
                        subsamplingScaleImageView.setImage(ImageSource.uri(Uri.fromFile(file)))
                    }
                }
                initShow(startPosition)
            }
        } else showAgain(startPosition)
    }

    private fun initShow(startPosition: Int) {
        mViewPager.adapter = mAdapter
        mSelectedPosition = startPosition
        val size = if (mStatus == URL) {
            mUrls!!.size
        } else mFiles!!.size
        val text = "${startPosition + 1}/$size"
        mTVImageCount.text = text
        showAgain(startPosition)
    }

    private fun showAgain(startPosition: Int) {
        mViewPager.currentItem = startPosition
        mDialog.show()
        mAnim.start()
    }

    fun recycler() {
        if (mViews.isNotEmpty()) {
            mViews.forEach {
                it.find<SubsamplingScaleImageView>(SUBSAMPLING_ID).recycle()
            }
        }
    }

}