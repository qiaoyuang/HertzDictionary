package com.w10group.hertzdictionary.biz.manager

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.widget.EditText
import com.w10group.hertzdictionary.biz.bean.InquireResult
import com.w10group.hertzdictionary.biz.bean.LocalWord
import com.w10group.hertzdictionary.biz.main.WordListAdapter
import com.w10group.hertzdictionary.core.NetworkUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jetbrains.anko.design.snackbar
import org.jetbrains.anko.progressDialog
import org.litepal.LitePal
import java.util.concurrent.CopyOnWriteArrayList

class WordManagerServiceV2(private val mView: WordDisplayView) {

    interface WordDisplayView {
        fun getEditText(): EditText
        fun getRecyclerView(): RecyclerView
        fun getContext(): Context
        fun displayInquireResult(inquireResult: InquireResult, word: String)
        fun displayOtherTranslation(words: String)
        fun displayRelatedWords(words: String)
        fun getCoroutineScope(): CoroutineScope
    }

    private val mContext = mView.getContext()
    private val mETInput = mView.getEditText()
    private val mRecyclerView = mView.getRecyclerView()
    private val mCoroutineScope = mView.getCoroutineScope()

    private val mData = CopyOnWriteArrayList<LocalWord>()
    private val mAdapter: WordListAdapter = WordListAdapter(mContext, mData) {
        mETInput.setText(it)
        inquire(it)
    }

    @Suppress("DEPRECATION")
    private val mProgressDialog by lazy {
        mContext.progressDialog(title = "请稍候......", message = "正在获取单词数据......") {
            setCancelable(false)
            setProgressStyle(0)
        }
    }

    fun scrollToTop() {
        mRecyclerView.smoothScrollToPosition(0)
    }

    fun scrollToBottom() {
        mRecyclerView.smoothScrollToPosition(mData.size - 1)
    }

    fun getAllWord() = mCoroutineScope.launch(Dispatchers.IO) {
        val list = LitePal.order("count desc").find(LocalWord::class.java)
        list?.let { mData.addAll(it) }
        withContext(Dispatchers.Main) {
            mRecyclerView.adapter = mAdapter
        }
    }

    fun inquire(word: String) = mCoroutineScope.launch {
        if (!NetworkUtil.checkNetwork(mContext)) {
            mRecyclerView.snackbar("当前无网络连接")
            return@launch
        }
        mProgressDialog.show()
        val deferred = NetworkUtil.create<NetworkService>().inquireWordByCoroutines(word)
        val inquireResult = deferred.await()
        refreshRecyclerViewData(inquireResult)
        mView.displayInquireResult(inquireResult, word)
        val (otherTranslation, relatedWords) = withContext(Dispatchers.Default) {
            //拼接其它义项
            val builder1 = StringBuilder()
            inquireResult.alternativeTranslations?.let { list ->
                list[0].words?.let { _list ->
                    val last = _list.size - 1
                    _list.forEachIndexed { index, alternative ->
                        if (index != 0) {
                            if (index == last) {
                                builder1.append(alternative.word)
                            } else {
                                builder1.append("${alternative.word}，")
                            }
                        }
                    }
                }
            }
            //拼接相关词组
            val builder2 = StringBuilder()
            inquireResult.relatedWords?.let { relatedWords ->
                relatedWords.words?.let { list ->
                    val last = list.size - 1
                    list.forEachIndexed { index, word ->
                        if (index == last) {
                            builder2.append(word)
                        } else {
                            builder2.append("$word, ")
                        }
                    }
                }
            }
            Pair(builder1.toString(), builder2.toString())
        }
        mView.displayOtherTranslation(otherTranslation)
        mView.displayRelatedWords(relatedWords)
        mProgressDialog.dismiss()
    }

    private fun refreshRecyclerViewData(inquireResult: InquireResult) = mCoroutineScope.launch(Dispatchers.Default) {
        val orig = inquireResult.word!![0]
        var word: LocalWord? = null
        //在mData中查找word是否存在，如果存在则找到它并记录其index
        mData.forEachIndexed { index, localWord ->
            if (localWord.en == orig.en) {
                word = localWord
                localWord.count++
                localWord.reSort(index)
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
                    mAdapter.notifyItemRangeChanged(0, mData.size)
                } else {
                    mAdapter.notifyItemRangeChanged(0, mData.size)
                }
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
    private fun LocalWord.reSort(index: Int) {
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