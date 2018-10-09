package com.w10group.hertzdictionary.biz.manager

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.widget.EditText
import com.w10group.hertzdictionary.biz.bean.InquireResult
import com.w10group.hertzdictionary.biz.bean.LocalWord
import com.w10group.hertzdictionary.biz.main.WordListAdapter
import com.w10group.hertzdictionary.core.NetworkUtil
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import org.jetbrains.anko.*
import org.jetbrains.anko.design.snackbar
import org.litepal.LitePal
import java.util.concurrent.CopyOnWriteArrayList

/**
 * Created by Administrator on 2018/7/16.
 * 单词查询存储计算等管理服务
 */

class WordManagerService(private val mView: WordDisplayView) {

    interface WordDisplayView {
        fun getEditText(): EditText
        fun getRecyclerView(): RecyclerView
        fun getContext(): Context
        fun displayInquireResult(inquireResult: InquireResult, word: String)
        fun displayOtherTranslation(words: String)
        fun displayRelatedWords(words: String)
    }

    private val mContext by lazy { mView.getContext() }
    private val mETInput by lazy { mView.getEditText() }
    private val mRecyclerView by lazy { mView.getRecyclerView() }

    private val mData by lazy { CopyOnWriteArrayList<LocalWord>() }
    private val mAdapter by lazy {
        WordListAdapter(mContext, mData,
                itemOnClickListener = {
                    mETInput.setText(it)
                    inquire(it)
                })
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

    fun getAllWord() {
        Observable.just(LitePal.order("count desc")
                .find(LocalWord::class.java))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeBy { list ->
                    list?.let { mData.addAll(it) }
                    mRecyclerView.adapter = mAdapter
                }
    }

    fun inquire(word: String) {
        if (!NetworkUtil.checkNetwork(mContext)) {
            mRecyclerView.snackbar("当前无网络连接")
            return
        }
        mProgressDialog.show()
        NetworkUtil.create<NetworkService>()
                .inquireWord(word)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext {
                    refreshRecyclerViewData(it)
                    mView.displayInquireResult(it, word)
                }
                .observeOn(Schedulers.computation())
                .map {
                    //拼接其它义项
                    val builder1 = StringBuilder()
                    it.alternativeTranslations?.let { list ->
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
                    it.relatedWords?.let { relatedWords ->
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
                    arrayOf(builder1.toString(), builder2.toString())
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeBy(
                        onNext = {
                            mView.displayOtherTranslation(it[0])
                            mView.displayRelatedWords(it[1])
                        },
                        onError = {
                            it.printStackTrace()
                            mProgressDialog.dismiss()
                            mRecyclerView.snackbar("网络出现问题，请稍后再试。")
                        },
                        onComplete = { mProgressDialog.dismiss() }
                )
    }

    //查询动作成功发生后调用此方法来进行数据库操作以及RecyclerView更新
    private fun refreshRecyclerViewData(inquireResult: InquireResult) {
        val disposable = Observable.just(inquireResult)
                .subscribeOn(Schedulers.computation())
                .observeOn(Schedulers.computation())
                .map {
                    val orig = it.word!![0]
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
                    word!!
                }
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext {
                    if (it.isSaved) {
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
                .observeOn(Schedulers.io())
                .subscribeBy(
                        onNext = { it.save() },
                        onError = { it.printStackTrace() }
                )
        disposable.dispose()
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