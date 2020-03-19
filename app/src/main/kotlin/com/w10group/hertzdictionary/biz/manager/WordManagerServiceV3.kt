package com.w10group.hertzdictionary.biz.manager

import android.content.Context
import android.view.View
import com.w10group.hertzdictionary.R

import com.w10group.hertzdictionary.biz.data.database.LocalWord
import com.w10group.hertzdictionary.biz.data.database.LocalWordDatabase
import com.w10group.hertzdictionary.core.CLIENT
import com.w10group.hertzdictionary.data.InquireResult
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import org.jetbrains.anko.design.snackbar
import org.jetbrains.anko.progressDialog
import java.util.concurrent.CopyOnWriteArrayList

/**
 * Created by Qiao Yuang on 2018/11/20.
 * 单词查询存储计算等管理服务，协程重构第二版
 */

object WordManagerServiceV3 {

    private const val BASE_URL = "http://translate.google.cn/translate_a/single"

    val inquireResultChannel = Channel<Pair<InquireResult, String>>(1)

    val OTRWChannel = Channel<Pair<String, String>>(1)

    val curveChannel = Channel<Boolean>(1)

    var currentLocalWord: LocalWord? = null
        private set

    private lateinit var allLocalWords: CopyOnWriteArrayList<LocalWord>

    suspend fun getAllLocalWord(context: Context): MutableList<LocalWord> =
        if (this::allLocalWords.isInitialized)
            allLocalWords
        else CopyOnWriteArrayList<LocalWord>().also {
            it.addAll(LocalWordDatabase.getDAO(context).queryAll())
            allLocalWords = it
        }

    var networkJob: Job? = null
        private set

    // 查询单词
    @Suppress("DEPRECATION")
    fun inquire(word: String, view: View) {
        networkJob = GlobalScope.launch(Dispatchers.Main) {
            val progressDialog = view.context.progressDialog(title = R.string.wait, message = R.string.getting_word) {
                setProgressStyle(0)
                setOnDismissListener { networkJob?.cancel() }
            }
            progressDialog.show()
            val inquireResult = try {
                CLIENT.get<InquireResult>(BASE_URL) {
                    parameter("q", word)
                    parameter("dj", 1)
                    parameter("client", "gtx")
                    parameter("sl", "en")
                    parameter("tl", "zh-CN")
                    parameter("ie", "UTF-8")
                    parameter("dt", "t")
                    parameter("dt", "at")
                    parameter("dt", "rw")
                    parameter("dt", "bd")
                    parameter("dt", "rm")
                }
            } catch (e: Exception) {
                e.printStackTrace()
                progressDialog.dismiss()
                view.snackbar(R.string.network_error)
                return@launch
            }
            val pairDeferred = async(Dispatchers.Default) { getOtherTranslationAndRelateWords(inquireResult) }
            inquireResultChannel.send(inquireResult to word)
            OTRWChannel.send(pairDeferred.await())
            progressDialog.dismiss()
            launch(Dispatchers.Default) { updateRecyclerViewData(inquireResult, view.context) }
        }
    }

    // 拼接其它义项以及相关词组并返回
    private suspend fun getOtherTranslationAndRelateWords(
            inquireResult: InquireResult): Pair<String, String> = coroutineScope {
            // 拼接其它义项
            val otherTranslationDeferred = inquireResult.alternativeTranslations?.first()?.words?.let {
                async {
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
                async {
                    StringBuilder().apply {
                        val last = it.size - 1
                        it.forEachIndexed { index, word -> append(if (index == last) word else "$word, ") }
                    }.toString()
                }
            }
            val otherTranslation = otherTranslationDeferred?.await() ?: ""
            val relatedWords = relatedWordsDeferred?.await() ?: ""
            otherTranslation to relatedWords
        }

    // 刷新 RecyclerView 的词序
    private suspend fun updateRecyclerViewData(inquireResult: InquireResult, context: Context) {
        val orig = inquireResult.word!![0]
        var word: LocalWord? = null
        // 在 mData 中查找 word 是否存在，如果存在则找到它并记录其 index
        allLocalWords.forEachIndexed { index, localWord ->
            if (localWord.en == orig.en) {
                word = localWord
                localWord.count++
                localWord reSort index
                return@forEachIndexed
            }
        }
        suspend fun sendWord(localWord: LocalWord) {
            currentLocalWord = localWord
            curveChannel.send(true)
        }
        // 如果 word 没有初始化表示 word 不存在于 mData 中，所以创建新 word
        if (word == null) LocalWord(ch = orig.ch, en = orig.en).let {
            coordinate[0] = -10
            it.timeList.add(DateManagerService.currentTimestamp)
            allLocalWords.add(it)
            sendWord(it)
            withContext(Dispatchers.IO) { LocalWordDatabase.getDAO(context).insert(it) }
        } else word!!.let {
            it.timeList.add(DateManagerService.currentTimestamp)
            sendWord(it)
            withContext(Dispatchers.IO) { LocalWordDatabase.getDAO(context).update(it) }
        }
    }

    // 第一个数字为负 -1 的时候表示未移动过，-10 的时候表示是第一次查询，非负时表示移动前的位置，第二个数表示移动后的位置
    val coordinate = intArrayOf(-1, -1)

    // 调整 LocalWord 在 mData 中的位置，并返回链表是否被调整过
    private infix fun LocalWord.reSort(index: Int) {
        coordinate[0] = -1
        when {
            index == 0 -> return
            allLocalWords[index - 1].count >= count -> return
            else -> {
                val start = index - 1
                for (i in start downTo 0) {
                    val word = allLocalWords[i]
                    when {
                        word.count >= count -> {
                            allLocalWords.removeAt(index)
                            val newIndex = i + 1
                            allLocalWords.add(newIndex, this)
                            coordinate[0] = index
                            coordinate[1] = newIndex
                        }
                        i == 0 -> {
                            allLocalWords.removeAt(index)
                            allLocalWords.add(i, this)
                            coordinate[0] = index
                            coordinate[1] = i
                        }
                    }
                }
            }
        }
    }

}