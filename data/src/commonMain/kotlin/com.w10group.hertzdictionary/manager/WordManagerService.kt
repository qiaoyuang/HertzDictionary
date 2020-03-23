package com.w10group.hertzdictionary.manager

import com.w10group.hertzdictionary.core.CLIENT
import com.w10group.hertzdictionary.core.currentTimestamp
import com.w10group.hertzdictionary.data.InquireResult
import com.w10group.hertzdictionary.database.LocalWord
import com.w10group.hertzdictionary.database.LocalWordDAO
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext

/**
 * Created by Qiao Yuang on 2018/11/20.
 * 单词查询存储计算等管理服务，第四版
 */

object WordManagerService {

    private const val BASE_URL = "http://translate.google.cn/translate_a/single"

    private lateinit var allLocalWords: ArrayList<LocalWord>

    var currentLocalWord: LocalWord? = null
        private set

    var sumCount = 0

    suspend fun getAllLocalWord(): MutableList<LocalWord> =
        if (this::allLocalWords.isInitialized)
            allLocalWords
        else ArrayList<LocalWord>().also {
            it.addAll(LocalWordDAO.queryAll())
            allLocalWords = it
            sumCount = it.sumBy { word -> word.count }
        }

    // 单词查询
    suspend fun inquire(word: String): InquireResult = CLIENT.get(BASE_URL) {
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

    // 拼接其它义项以及相关词组并返回
    suspend fun getOtherTranslationAndRelateWords(
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
    suspend fun updateRecyclerViewData(inquireResult: InquireResult): IntArray {
        val orig = inquireResult.word!![0]
        var word: LocalWord? = null
        val coordinate = intArrayOf(-1, -1)
        // 在 mData 中查找 word 是否存在，如果存在则找到它并记录其 index
        allLocalWords.forEachIndexed { index, localWord ->
            if (localWord.en == orig.en) {
                word = localWord
                localWord.count++
                localWord.reSort(index, coordinate)
                return@forEachIndexed
            }
        }
        fun sendWord(localWord: LocalWord) {
            currentLocalWord = localWord
            sumCount++
        }
        // 如果 word 没有初始化表示 word 不存在于 mData 中，所以创建新 word
        if (word == null) LocalWord().let {
            it.ch = orig.ch
            it.en = orig.en
            coordinate[0] = -10
            it.timeList.add(currentTimestamp)
            allLocalWords.add(it)
            sendWord(it)
            withContext(Dispatchers.IO) { LocalWordDAO.insert(it) }
        } else word!!.let {
            it.timeList.add(currentTimestamp)
            sendWord(it)
            withContext(Dispatchers.IO) { LocalWordDAO.update(it) }
        }
        return coordinate
    }

    // 调整 LocalWord 在 mData 中的位置，并返回链表是否被调整过
    private fun LocalWord.reSort(index: Int, coordinate: IntArray) {
        // 第一个数字为负 -1 的时候表示未移动过，-10 的时候表示是第一次查询，非负时表示移动前的位置，第二个数表示移动后的位置
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