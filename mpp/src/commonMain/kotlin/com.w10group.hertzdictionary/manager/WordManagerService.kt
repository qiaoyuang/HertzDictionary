package com.w10group.hertzdictionary.manager

import com.w10group.hertzdictionary.core.CLIENT
import com.w10group.hertzdictionary.core.IODispatcher
import com.w10group.hertzdictionary.core.SafeListConstructor
import com.w10group.hertzdictionary.core.currentTimestamp
import com.w10group.hertzdictionary.data.InquireResult
import com.w10group.hertzdictionary.database.LocalWord
import com.w10group.hertzdictionary.database.LocalWordDAO
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import kotlin.native.concurrent.ThreadLocal

/**
 * Created by Qiao Yuang on 2018/11/20.
 * 单词查询存储计算等管理服务，第四版
 */

@ThreadLocal
object WordManagerService {

    private const val BASE_URL = "http://translate.google.cn/translate_a/single"

    private lateinit var allLocalWords: MutableList<LocalWord>

    var currentLocalWord: LocalWord? = null
        private set

    var sumCount = 0

    suspend fun getAllLocalWord(): MutableList<LocalWord> =
        if (this::allLocalWords.isInitialized)
            allLocalWords
        else SafeListConstructor().also {
            withContext(IODispatcher) {
                it.addAll(LocalWordDAO.queryAll())
            }
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

    // 第一个数字为负 -1 的时候表示未移动过，-10 的时候表示是第一次查询，非负时表示移动前的位置，第二个数表示移动后的位置
    const val NO_MOVE = -1
    const val FIRST_INQUIRE = -10

    // 刷新 RecyclerView 的词序
    suspend fun updateRecyclerViewData(inquireResult: InquireResult): IntArray {
        val orig = inquireResult.word!!.first()
        var word: LocalWord? = null
        val coordinate = intArrayOf(NO_MOVE, NO_MOVE)
        // 在 mData 中查找 word 是否存在，如果存在则找到它并记录其 index
        allLocalWords.forEachIndexed { index, localWord ->
            if (localWord.en == orig.en) {
                word = localWord
                localWord.count++
                localWord.reSort(index, coordinate)
                return@forEachIndexed
            }
        }
        // 如果 word 没有初始化表示 word 不存在于 mData 中，所以创建新 word
        currentLocalWord = if (word == null) LocalWord(ch = orig.ch, en = orig.en).also {
            coordinate[0] = FIRST_INQUIRE
            it.timeList.add(currentTimestamp)
            allLocalWords.add(it)
            withContext(IODispatcher) { LocalWordDAO.insert(it) }
        } else word!!.also {
            it.timeList.add(currentTimestamp)
            withContext(IODispatcher) { LocalWordDAO.update(it) }
        }
        sumCount++
        return coordinate
    }

    // 调整 LocalWord 在 mData 中的位置，并返回链表是否被调整过
    private fun LocalWord.reSort(index: Int, coordinate: IntArray) = when {
        index == 0 -> Unit
        allLocalWords[index - 1].count >= count -> Unit
        else -> loop@ for (i in index - 1 downTo 0) {
            val word = allLocalWords[i]
            when {
                word.count >= count -> {
                    val newIndex = i + 1
                    allLocalWords.add(newIndex, allLocalWords.removeAt(index))
                    coordinate[0] = index
                    coordinate[1] = newIndex
                    break@loop
                }
                i == 0 -> {
                    allLocalWords.add(i, allLocalWords.removeAt(index))
                    coordinate[0] = index
                    coordinate[1] = i
                }
            }
        }
    }

}