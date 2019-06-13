package com.w10group.hertzdictionary.biz.manager

import com.w10group.hertzdictionary.biz.bean.LocalWord
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

/**
 * 日期时间管理服务
 * @author Qiao
 */

typealias MostValue = Pair<List<LocalWord>, Int>
typealias CurveValue = Triple<List<Long>, List<Int>, MostValue>

object DateManagerService {

    private const val ONE_DAY = 86400000

    // 生成某单词的周曲线数据
    fun createWeekValue(vararg localWords: LocalWord): CurveValue = createXYValue(weekTimestampList, *localWords)

    // 生成某单词的月曲线数据
    fun createMonthValue(vararg localWords: LocalWord): CurveValue = createXYValue(monthTimestampList, *localWords)

    private fun createXYValue(timeList: List<Long>, vararg localWords: LocalWord): CurveValue {
        val valueList = IntArray(timeList.size) { 0 }
        val wordAndCountMap = HashMap<LocalWord, Int>()
        localWords.forEach { localWord ->
            wordAndCountMap[localWord] = 0
            localWord.timeList?.forEach {
                timeList.forEachIndexed { index, time ->
                    if (it - time in 0 until ONE_DAY) {
                        wordAndCountMap[localWord] = wordAndCountMap[localWord]!! + 1
                        valueList[index]++
                        return@forEachIndexed
                    }
                }
            }
        }
        val biggestValue = wordAndCountMap.entries.maxBy { it.value }?.value ?: 0
        return Triple(timeList, valueList.toList(), wordAndCountMap.entries
                .asSequence()
                .filter { it.value == biggestValue }
                .map { it.key }
                .toList() to biggestValue)
    }

    // 获取最近一周的时间戳
    private val weekTimestampList
        get() = getTimestampList(7)

    // 获取最近一个月的时间戳
    private val monthTimestampList
        get() = getTimestampList(30)

    // 获取当前时间戳
    val currentTimestamp
        get() = System.currentTimeMillis()

    // 获取时间戳列表
    private fun getTimestampList(count: Int): List<Long> = ArrayList<Long>().apply {
        var timestamp = Calendar.getInstance().apply {
            timeInMillis = currentTimestamp
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
        add(timestamp)
        repeat(count - 1) {
            val frontTimestamp = timestamp - ONE_DAY
            add(frontTimestamp)
            timestamp = frontTimestamp
        }
    }.reversed()

}