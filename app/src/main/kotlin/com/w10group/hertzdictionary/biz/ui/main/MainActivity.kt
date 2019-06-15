package com.w10group.hertzdictionary.biz.ui.main

import android.content.Context
import android.os.Bundle
import android.view.*
import android.view.inputmethod.InputMethodManager
import com.w10group.hertzdictionary.R
import com.w10group.hertzdictionary.biz.manager.DateManagerService
import com.w10group.hertzdictionary.biz.manager.WordManagerServiceV3
import com.w10group.hertzdictionary.core.architecture.CoroutineScopeActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Created by Administrator on 2018/6/15.
 * 主界面 Activity
 */

class MainActivity : CoroutineScopeActivity<MainActivity>() {

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
            createCurveData()
        }

    var status = STATUS_INQUIRED_NOT

    private lateinit var mAdapter: WordListAdapter

    override val uiComponent = MainActivityUIComponent(this)
    override val implementer = this
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        launch(Dispatchers.IO) {
            mAdapter = WordListAdapter(implementer, WordManagerServiceV3.allLocalWords) {
                uiComponent.setWordText(it)
                inquire(it)
            }
            withContext(Dispatchers.Main) {
                uiComponent.loadBackgroundImageView()
                uiComponent.setAdapter(mAdapter)
            }
        }
    }

    override fun onStart() {
        super.onStart()
        // 获取查询结果
        launch {
            for (element in WordManagerServiceV3.inquireResultChannel) {
                val (inquireResult, word) = element
                uiComponent.displayInquireResult(inquireResult, word)
            }
        }
        // 获取其它义项以及相关词组
        launch {
            for (element in WordManagerServiceV3.OTRWChannel) {
                val (otherTranslation, relatedWords) = element
                uiComponent displayOtherTranslation otherTranslation
                uiComponent displayRelatedWords relatedWords
            }
        }
        // 获取更新曲线图的信号
        launch {
            for (element in WordManagerServiceV3.curveChannel) {
                mAdapter.sumCount++
                createCurveData()
            }
        }
    }

    override fun onStop() {
        super.onStop()
        WordManagerServiceV3.networkJob?.cancel()
    }

    fun inquire(word: String) = WordManagerServiceV3.inquire(word, uiComponent.snackBarView)

    fun refreshRecyclerView() = launch {
        val word = WordManagerServiceV3.listUpdateChannel.receive()
        val (front, next) = WordManagerServiceV3.coordinate
        if (word.isSaved) {
            if (front >= 0) {
                mAdapter.notifyItemRemoved(front)
                mAdapter.notifyItemInserted(next)
            }
            mAdapter.notifyItemRangeChanged(0, mAdapter.itemCount)
        } else {
            val index = mAdapter.itemCount - 1
            mAdapter.notifyItemRangeChanged(0, index)
            mAdapter.notifyItemInserted(index)
        }
    }

    private fun createCurveData() {
        when (curveStatus) {
            CURVE_STATUS_WEEK -> {
                WordManagerServiceV3.currentLocalWord?.let {
                    launch(Dispatchers.Default) {
                        val (timeList, valueList) = DateManagerService.createWeekValue(it)
                        withContext(Dispatchers.Main) { uiComponent.updateCurveView(timeList, valueList) }
                    }
                }
            }
            CURVE_STATUS_MONTH -> {
                WordManagerServiceV3.currentLocalWord?.let {
                    launch(Dispatchers.Default) {
                        val (timeList, valueList) = DateManagerService.createMonthValue(it)
                        withContext(Dispatchers.Main) { uiComponent.updateCurveView(timeList, valueList) }
                    }
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
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
            if (event.action == MotionEvent.ACTION_DOWN) {
                if (uiComponent.isShouldHideInput(currentFocus, event)) {
                    currentFocus?.windowToken?.let {
                        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                        imm.hideSoftInputFromWindow(it, 0)
                    }
                }
            }
        }
        return super.dispatchTouchEvent(ev)
    }

}