package com.w10group.hertzdictionary.core.image

import android.Manifest
import android.animation.ArgbEvaluator
import android.animation.ObjectAnimator
import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.provider.MediaStore
import android.provider.Settings
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v4.view.ViewPager
import android.view.Gravity
import android.view.View
import android.view.animation.LinearInterpolator
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import com.davemorrissey.labs.subscaleview.ImageSource
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView
import com.w10group.hertzdictionary.R
import com.w10group.hertzdictionary.core.view.subsamplingImageView
import kotlinx.coroutines.*
import org.jetbrains.anko.*
import org.jetbrains.anko.design.longSnackbar
import org.jetbrains.anko.design.snackbar
import org.jetbrains.anko.support.v4.viewPager
import java.io.File
import java.util.*
import kotlin.coroutines.CoroutineContext

/**
 * Created by qiaoyuang on 2018/7/9.
 * 大图查看器封装（网络查看模式支持并发）
 */

class CompleteScaleImageView(private val mActivity: Activity,
                             private val mImageDownloader: ImageDownloader,
                             private val mRequestCode: Int) : CoroutineScope {

    private lateinit var mTotalJob: Job

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + mTotalJob

    private lateinit var mViewPager: ViewPager
    private val mAdapter by lazy { DialogPagerAdapter(mViews, mDialog) }

    private lateinit var mIVBack: ImageView//后退按钮
    private lateinit var mIVDownload: ImageView//下载按钮
    private lateinit var mIVDelete: ImageView//删除按钮
    private lateinit var mTVImageCount: TextView//显示当前为第几页的TextView

    private val mDialog = Dialog(mActivity, R.style.Dialog_Fullscreen).apply {
        setContentView(initView())
        setOnDismissListener { mTotalJob.cancel() }
    }

    private val mViews by lazy { LinkedList<View>() }
    private val mDownloadFiles by lazy { LinkedList<File>() }//下载图片列表，当使用网络查看模式时，该列表保存所有下载下来的完整尺寸图片

    private var mSelectedPosition = 0//当前选中的位置
    private val mAlbumName by lazy { mActivity.getString(R.string.app_name) }

    private companion object {
        const val URL = 0
        const val FILE = 1
        const val PROGRESS_BAR_ID = 10
        const val SUBSAMPLING_ID = 11

        const val VALUE_TEXT_COLOR = "textColor"
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

    private val mAnim = ObjectAnimator.ofArgb(mTVImageCount, VALUE_TEXT_COLOR , Color.WHITE, Color.TRANSPARENT).apply {
        duration = 1500
        interpolator = LinearInterpolator()
        setEvaluator(ArgbEvaluator())
    }

    private fun initView(): View =
        AnkoContext.create(mActivity).apply {
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
                        if (ContextCompat.checkSelfPermission(mActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                            restoreImage()
                        } else {
                            ActivityCompat.requestPermissions(mActivity, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), mRequestCode)
                        }
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

    // 保存图片
    fun restoreImage() {
        val file = mDownloadFiles[mSelectedPosition]
        MediaStore.Images.Media.insertImage(mActivity.contentResolver, file.absolutePath, file.name, mAlbumName)
        mViewPager.snackbar("图片保存成功")
    }

    // 动态申请权限被拒绝后的回调
    fun permissionsRejectSnack() {
        mViewPager.longSnackbar("您拒绝了存储权限申请，保存图片失败", "设置") {
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.data = Uri.fromParts("package", mActivity.packageName, null)
            mActivity.startActivity(intent)
        }.setActionTextColor(Color.YELLOW)
    }

    private fun createItemView(): View =
        AnkoContext.create(mActivity).apply {
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

    var isDownloaderEnable: Boolean = false
        set(value) {
            field = value
            mIVDownload.visibility = if (value) {
                View.VISIBLE
            } else View.INVISIBLE
        }

    // 启动并展示图片
    fun showByCoroutines(startPosition: Int = 0) {
        mTotalJob = Job()
        launch {
            if (mViews.isEmpty()) {
                if (mStatus == URL) {
                    mUrls?.let { urls ->
                        for (i in 0 until urls.size) {
                            mViews.add(createItemView())
                            val deferred = async(Dispatchers.IO) {
                                val downloadFile = mImageDownloader.download(urls[i], mActivity)
                                mDownloadFiles.add(downloadFile)
                                downloadFile
                            }
                            launch {
                                mViews[i].find<SubsamplingScaleImageView>(SUBSAMPLING_ID).setImage(ImageSource.uri(Uri.fromFile(deferred.await())))
                                mViews[i].find<ProgressBar>(PROGRESS_BAR_ID).visibility = View.INVISIBLE
                            }
                        }
                        initShow(startPosition)
                    }
                } else if (mStatus == FILE) {
                    mFiles?.let {
                        for (file in it) {
                            val view = createItemView()
                            val subsamplingScaleImageView = view.find<SubsamplingScaleImageView>(SUBSAMPLING_ID)
                            mViews.add(view)
                            subsamplingScaleImageView.setImage(ImageSource.uri(Uri.fromFile(file)))
                        }
                    }
                }
            } else showAgain(startPosition)
        }
    }

    private fun initShow(startPosition: Int) {
        mViewPager.adapter = mAdapter
        mSelectedPosition = startPosition
        val size = if (mStatus == URL) mUrls!!.size else mFiles!!.size
        val text = "${startPosition + 1}/$size"
        mTVImageCount.text = text
        showAgain(startPosition)
    }

    private fun showAgain(startPosition: Int) {
        mViewPager.currentItem = startPosition
        mDialog.show()
        mAnim.start()
    }

    // 回收资源
    fun recycler() {
        if (mViews.isNotEmpty()) {
            mViews.forEach {
                it.find<SubsamplingScaleImageView>(SUBSAMPLING_ID).recycle()
            }
        }
    }

}