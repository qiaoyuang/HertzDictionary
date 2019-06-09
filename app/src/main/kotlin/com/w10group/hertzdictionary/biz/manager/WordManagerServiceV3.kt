package com.w10group.hertzdictionary.biz.manager

import android.view.View
import com.w10group.hertzdictionary.biz.bean.InquireResult
import com.w10group.hertzdictionary.biz.bean.LocalWord
import com.w10group.hertzdictionary.core.NetworkUtil
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import org.jetbrains.anko.design.snackbar
import org.jetbrains.anko.progressDialog
import org.litepal.LitePal
import org.litepal.extension.find
import java.util.concurrent.CopyOnWriteArrayList

/**
 * Created by Qiao Yuang on 2018/11/20.
 * 单词查询存储计算等管理服务，协程重构第二版
 */

object WordManagerServiceV3 {

    val listUpdateChannel = Channel<LocalWord>(1)

    val inquireResultChannel = Channel<Pair<InquireResult, String>>(1)

    val OTRWChannel = Channel<Pair<String, String>>(1)

    val curveChannel = Channel<Boolean>(1)

    var currentLocalWord: LocalWord? = null
        private set

    val allLocalWords = CopyOnWriteArrayList<LocalWord>().apply {
        val list = LitePal.order("count desc").find<LocalWord>()
        addAll(list)
    }

    var networkJob: Job? = null
        private set

    // 查询单词
    @Suppress("DEPRECATION")
    fun inquire(word: String,
                view: View) {
        networkJob = GlobalScope.launch(Dispatchers.Main) {
            if (!NetworkUtil.checkNetwork(view.context)) {
                view.snackbar("当前无网络连接")
                return@launch
            }
            val progressDialog = view.context.progressDialog(title = "请稍候......", message = "正在获取单词数据......") {
                setProgressStyle(0)
                setOnDismissListener { networkJob?.cancel() }
            }
            progressDialog.show()
            val inquireResult = try {
                NetworkUtil.instance.inquireWordByCoroutinesAsync(word).await()
            } catch (e: Exception) {
                e.printStackTrace()
                progressDialog.dismiss()
                view.snackbar("网络出现问题，请稍后再试。")
                return@launch
            }
            val pairDeferred = async(Dispatchers.Default) { getOtherTranslationAndRelateWords(inquireResult) }
            inquireResultChannel.send(inquireResult to word)
            OTRWChannel.send(pairDeferred.await())
            progressDialog.dismiss()
            launch(Dispatchers.Default) { updateRecyclerViewData(inquireResult) }
        }
    }

    // 拼接其它义项以及相关词组并返回
    private suspend fun getOtherTranslationAndRelateWords(inquireResult: InquireResult): Pair<String, String> {
        // 拼接其它义项
        val otherTranslationDeferred = inquireResult.alternativeTranslations?.get(0)?.words?.let {
            GlobalScope.async(Dispatchers.Default) {
                StringBuilder().apply {
                    val last = it.size - 1
                    it.forEachIndexed { index, alternative ->
                        if (index != 0)
                            append(if (index == last) alternative.word else "${alternative.word}，")
                    }
                }.toString()
            }
        }
        // 拼接相关词组
        val relatedWordsDeferred = inquireResult.relatedWords?.words?.let {
            GlobalScope.async(Dispatchers.Default) {
                StringBuilder().apply {
                    val last = it.size - 1
                    it.forEachIndexed { index, word -> append(if (index == last) word else "$word, ") }
                }.toString()
            }
        }
        val otherTranslation = otherTranslationDeferred?.await() ?: ""
        val relatedWords = relatedWordsDeferred?.await() ?: ""
        return otherTranslation to relatedWords
    }

    // 刷新RecyclerView的词序
    private suspend fun updateRecyclerViewData(inquireResult: InquireResult) {
        val orig = inquireResult.word!![0]
        var word: LocalWord? = null
        // 在mData中查找word是否存在，如果存在则找到它并记录其index
        allLocalWords.forEachIndexed { index, localWord ->
            if (localWord.en == orig.en) {
                word = localWord
                localWord.count++
                localWord reSort index
            }
        }
        // 如果word没有初始化表示word不存在于mData中，所以创建新word
        if (word == null) {
            word = LocalWord(ch = orig.ch, en = orig.en)
            allLocalWords.add(word!!)
        }
        if (word!!.timeList == null) {
            word!!.timeList = ArrayList()
        }
        word!!.timeList!!.add(DateManagerService.currentTimestamp)
        currentLocalWord = word
        curveChannel.send(true)
        listUpdateChannel.send(word!!)
        withContext(Dispatchers.IO) { word!!.save() }
    }

    // 第一个数字为负的时候表示未移动过，非负时表示移动前的位置，第二个数表示移动后的位置
    val isMoved = intArrayOf(-1, -1)

    // 调整LocalWord在mData中的位置，并返回链表是否被调整过
    private infix fun LocalWord.reSort(index: Int) {
        isMoved[0] = -1
        when {
            index == 0 -> return
            allLocalWords[index - 1].count >= count -> return
            else -> {
                val start = index - 1
                for (i in start downTo 0) {
                    val word = allLocalWords[i]
                    if (word.count >= count) {
                        allLocalWords.removeAt(index)
                        val newIndex = i + 1
                        allLocalWords.add(newIndex, this)
                        isMoved[0] = index
                        isMoved[1] = newIndex
                        return
                    } else if (i == 0 && word.count < count) {
                        allLocalWords.removeAt(index)
                        allLocalWords.add(i, this)
                        isMoved[0] = index
                        isMoved[1] = i
                    }
                }
            }
        }
    }

}