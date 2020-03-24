package com.w10group.hertzdictionary.app.biz.ui.main

import android.app.ProgressDialog
import android.content.Context
import android.os.Bundle
import android.view.*
import android.view.inputmethod.InputMethodManager
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.w10group.hertzdictionary.app.R
import com.w10group.hertzdictionary.core.DataModule
import com.w10group.hertzdictionary.app.core.architecture.BaseActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.jetbrains.anko.design.snackbar
import org.jetbrains.anko.progressDialog

/**
 * Created by Administrator on 2018/6/15.
 * 主界面 Activity
 */

@Suppress("DEPRECATION")
class MainActivity : BaseActivity<MainActivity>() {

    companion object {
        // 标记当前词典的状态是否是查询状态
        const val STATUS_INQUIRED_NOT = 0
        const val STATUS_INQUIRED = 1

        // 标记曲线横轴显示时间的状态
        const val CURVE_STATUS_WEEK = 0
        const val CURVE_STATUS_MONTH = 1
    }

    var curveStatus = CURVE_STATUS_WEEK
        set(value) {
            field = value
            viewModel.updateCurveData(value)
        }

    var status = STATUS_INQUIRED_NOT

    private lateinit var mAdapter: WordListAdapter

    override val uiComponent = MainActivityUIComponent(this)
    override val implementer = this

    private lateinit var viewModel: MainViewModel

    private lateinit var progressDialog: ProgressDialog

    private val coordinate = intArrayOf(-1, -1)

    private var networkJob: Job? = null

    init { lifecycle.addObserver(uiComponent) }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DataModule.init(application)
        viewModel = getViewModel {
            allWordList.observe(implementer, Observer {
                with(uiComponent) {
                    lifecycleScope.launch { loadBackgroundImageView() }
                    mAdapter = WordListAdapter(implementer, it) { word ->
                        uiComponent.setWordText(word)
                        inquire(word)
                    }
                    setAdapter(mAdapter)
                }
            })

            inquireResult.observe(implementer, Observer {
                when (it) {
                    is InquireResponseSuccess -> uiComponent.displayInquireResult(it.inquireResult, it.word)
                    is InquireResponseError -> {
                        progressDialog.dismiss()
                        uiComponent.snackBarView.snackbar(R.string.network_error)
                        it.exception.printStackTrace()
                    }
                }

            })

            otherTranslationAndRelateWords.observe(implementer, Observer {
                it?.let {
                    val (otherTranslation, relatedWords) = it
                    uiComponent displayOtherTranslation otherTranslation
                    uiComponent displayRelatedWords relatedWords
                    progressDialog.dismiss()
                }
            })

            recyclerViewAdjustmentCoordinate.observe(implementer, Observer {
                it?.let {
                    coordinate[0] = it[0]
                    coordinate[1] = it[1]
                    viewModel.updateCurveData(curveStatus)
                }
            })

            curveData.observe(implementer, Observer {
                val (timeList, valueList) = it
                uiComponent.updateCurveView(timeList, valueList)
            })

            updateAllWordList()
        }
    }

    fun inquire(word: String) {
        if (!::progressDialog.isInitialized)
            progressDialog = progressDialog(title = R.string.wait, message = R.string.getting_word) {
                setProgressStyle(0)
                setOnDismissListener { networkJob?.cancel() }
            }
        progressDialog.show()
        networkJob = viewModel.inquire(word)
    }

    fun refreshRecyclerView() = lifecycleScope.launch {
        val (front, next) = coordinate
        if (front != -10) {
            if (front > 0) {
                mAdapter.notifyItemRemoved(front)
                mAdapter.notifyItemInserted(next)
            }
            mAdapter.notifyItemRangeChanged(0, mAdapter.itemCount)
        } else {
            val index = mAdapter.itemCount
            mAdapter.notifyItemRangeChanged(0, index)
            mAdapter.notifyItemInserted(index)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.move_to_Bottom -> uiComponent.scrollToBottom()
            R.id.move_to_top -> uiComponent.scrollToTop()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (status == STATUS_INQUIRED) {
                uiComponent.restore()
                return false
            }
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        ev?.let { event ->
            if (event.action == MotionEvent.ACTION_DOWN)
                if (uiComponent.isShouldHideInput(currentFocus, event))
                    currentFocus?.windowToken?.let {
                        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                        imm.hideSoftInputFromWindow(it, 0)
                    }
        }
        return super.dispatchTouchEvent(ev)
    }

}