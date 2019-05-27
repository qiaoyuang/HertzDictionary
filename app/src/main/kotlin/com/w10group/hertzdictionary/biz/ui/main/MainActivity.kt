package com.w10group.hertzdictionary.biz.ui.main

import android.content.Context
import android.os.Bundle
import android.view.*
import android.view.inputmethod.InputMethodManager
import com.w10group.hertzdictionary.R
import com.w10group.hertzdictionary.biz.manager.WordManagerServiceV3
import com.w10group.hertzdictionary.biz.ui.statistics.StatisticsActivity
import com.w10group.hertzdictionary.core.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.jetbrains.anko.*

/**
 * Created by Administrator on 2018/6/15.
 * 主界面 Activity
 */

class MainActivity : CoroutineScopeActivity() {

    internal companion object {
        // 标记当前词典的状态是否是查询状态
        const val STATUS_INQUIRED_NOT = 0
        const val STATUS_INQUIRED = 1
    }

    internal var status = STATUS_INQUIRED_NOT
    private val mWordManagerService by lazy { WordManagerServiceV3(mMainActivityUI) }

    private val mMainActivityUI = MainActivityUI(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mMainActivityUI.setContentView(this)
        mMainActivityUI.initToolBar()
        launch { mMainActivityUI.loadBackgroundImageView() }
        launch(Dispatchers.IO) { mWordManagerService.getAllWord() }
    }

    fun inquire(word: String) = mWordManagerService.inquire(word)

    fun refreshRecyclerView() = launch {
        mWordManagerService.refreshRecyclerView()
    }

    fun startStatisticsActivity() = launch {
        WordManagerServiceV3.instanceChannel.send(mWordManagerService)
        startActivity<StatisticsActivity>()
    }

    val currentLocalWord
        get() = mWordManagerService.currentLocalWord

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.move_to_Bottom -> mMainActivityUI.scrollToBottom()
            R.id.move_to_top -> mMainActivityUI.scrollToTop()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (status == STATUS_INQUIRED) {
                mMainActivityUI.restore()
                return false
            }
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        ev?.let { event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                if (mMainActivityUI.isShouldHideInput(currentFocus, event)) {
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