package com.w10group.hertzdictionary.biz.manager

import com.w10group.hertzdictionary.biz.bean.InquireResult
import com.w10group.hertzdictionary.biz.bean.LocalWord
import com.w10group.hertzdictionary.biz.main.WordListAdapter
import com.w10group.hertzdictionary.core.NetworkUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jetbrains.anko.design.snackbar
import org.jetbrains.anko.progressDialog
import org.litepal.LitePal
import org.litepal.extension.find
import java.io.IOException
import java.util.concurrent.CopyOnWriteArrayList

/**
 * Created by Qiao Yuang on 2018/11/20.
 * 单词查询存储计算等管理服务，协程重构第二版
 */

class WordManagerServiceV3(private val mView: WordDisplayView) {

    private val mContext = mView.getContext()
    private val mETInput = mView.getEditText()
    private val mRecyclerView = mView.getRecyclerView()
    private val mCoroutineScope = mView.getCoroutineScope()

    private val mData by lazy { CopyOnWriteArrayList<LocalWord>() }
    private val mAdapter: WordListAdapter by lazy {
        WordListAdapter(mContext, mData, mCoroutineScope) {
            mETInput.setText(it)
            inquire(it)
        }
    }

    @Suppress("DEPRECATION")
    private val mProgressDialog by lazy {
        mContext.progressDialog(title = "请稍候......", message = "正在获取单词数据......") {
            setCancelable(false)
            setProgressStyle(0)
        }
    }

    //滑动到RecyclerView顶部
    fun scrollToTop() {
        mRecyclerView.smoothScrollToPosition(0)
    }

    //滑动到RecyclerView底部
    fun scrollToBottom() {
        mRecyclerView.smoothScrollToPosition(mData.size - 1)
    }

    //获取所有单词
    suspend fun getAllWord() {
        val list = LitePal.order("count desc").find<LocalWord>()
        mData.addAll(list)
        withContext(Dispatchers.Main) { mRecyclerView.adapter = mAdapter }
    }

    //查询单词
    fun inquire(word: String): Job = mCoroutineScope.launch {
        if (!NetworkUtil.checkNetwork(mContext)) {
            mRecyclerView.snackbar("当前无网络连接")
            return@launch
        }
        mProgressDialog.show()
        val inquireResult = try {
            NetworkUtil.create<NetworkService>().inquireWordByCoroutines(word).await()
        } catch (e: IOException) {
            e.printStackTrace()
            mProgressDialog.dismiss()
            mRecyclerView.snackbar("网络出现问题，请稍后再试。")
            return@launch
        }
        mView.displayInquireResult(inquireResult, word)
        launch(Dispatchers.Default) { refreshRecyclerViewData(inquireResult) }
        this@WordManagerServiceV3 showOtherTranslationAndRelateWords withContext(Dispatchers.Default) {
            getOtherTranslationAndRelateWords(inquireResult)
        }
        mProgressDialog.dismiss()
    }

    //拼接其它义项以及相关词组并返回
    private fun getOtherTranslationAndRelateWords(inquireResult: InquireResult): Pair<String, String> {
        //拼接其它义项
        val otherTranslation = inquireResult.alternativeTranslations?.get(0)?.words?.let {
            StringBuilder().apply {
                val last = it.size - 1
                it.forEachIndexed { index, alternative ->
                    if (index != 0)
                        append(if (index == last) alternative.word else "${alternative.word}，")
                }
            }.toString()
        } ?: ""
        //拼接相关词组
        val relatedWords = inquireResult.relatedWords?.words?.let {
            StringBuilder().apply {
                val last = it.size - 1
                it.forEachIndexed { index, word -> append(if (index == last) word else "$word, ") }
            }.toString()
        } ?: ""
        return Pair(otherTranslation, relatedWords)
    }

    //展示其它义项以及相关词组
    private suspend infix fun showOtherTranslationAndRelateWords(pair: Pair<String, String>) {
        val (otherTranslation, relatedWords) = pair
        mView displayOtherTranslation otherTranslation
        mView displayRelatedWords relatedWords
    }

    //刷新RecyclerView的词序
    private suspend fun refreshRecyclerViewData(inquireResult: InquireResult) {
        val orig = inquireResult.word!![0]
        var word: LocalWord? = null
        //在mData中查找word是否存在，如果存在则找到它并记录其index
        mData.forEachIndexed { index, localWord ->
            if (localWord.en == orig.en) {
                word = localWord
                localWord.count++
                localWord reSort index
            }
        }
        //如果word没有初始化表示word不存在与mData中，所以创建新word
        if (word == null) {
            word = LocalWord(ch = orig.ch, en = orig.en)
            mData.add(word!!)
        }
        mAdapter.sumCount++
        withContext(Dispatchers.Main) {
            if (word!!.isSaved) {
                if (mIsMoved[0] >= 0) {
                    mAdapter.notifyItemRemoved(mIsMoved[0])
                    mAdapter.notifyItemInserted(mIsMoved[1])
                }
                mAdapter.notifyItemRangeChanged(0, mData.size)
            } else {
                val index = mData.size - 1
                mAdapter.notifyItemRangeChanged(0, index)
                mAdapter.notifyItemInserted(index)
            }
        }
        withContext(Dispatchers.IO) { word!!.save() }
    }

    //第一个数字为负的时候表示未移动过，非负时表示移动前的位置，第二个数表示移动后的位置
    private val mIsMoved by lazy { intArrayOf(-1, -1) }

    //调整LocalWord在mData中的位置，并返回链表是否被调整过
    private infix fun LocalWord.reSort(index: Int) {
        mIsMoved[0] = -1
        when {
            index == 0 -> return
            mData[index - 1].count >= count -> return
            else -> {
                val start = index - 1
                for (i in start downTo 0) {
                    val word = mData[i]
                    if (word.count >= count) {
                        mData.removeAt(index)
                        val newIndex = i + 1
                        mData.add(newIndex, this)
                        mIsMoved[0] = index
                        mIsMoved[1] = newIndex
                        return
                    } else if (i == 0 && word.count < count) {
                        mData.removeAt(index)
                        mData.add(i, this)
                        mIsMoved[0] = index
                        mIsMoved[1] = i
                    }
                }
            }
        }
    }

}