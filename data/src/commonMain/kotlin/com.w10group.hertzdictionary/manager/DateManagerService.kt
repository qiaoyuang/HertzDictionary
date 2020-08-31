package com.w10group.hertzdictionary.manager

import com.w10group.hertzdictionary.core.todayTimestamp
import com.w10group.hertzdictionary.database.LocalWord
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

/**
 * 日期时间管理服务
 * @author Qiao
 */

typealias MostValue = Pair<List<LocalWord>, Int>
typealias CurveValue = Triple<List<Long>, List<Int>, MostValue>

object DateManagerService {

    const val ONE_DAY = 86_400_000

    // 生成单词的周曲线数据
    fun createWeekValue(vararg localWords: LocalWord): CurveValue = createXYValue(weekTimestampList, *localWords)

    // 生成单词的月曲线数据
    fun createMonthValue(vararg localWords: LocalWord): CurveValue = createXYValue(monthTimestampList, *localWords)

    private fun createXYValue(timeList: List<Long>, vararg localWords: LocalWord): CurveValue {
        val valueList = IntArray(timeList.size) { 0 }
        val wordAndCountMap = HashMap<LocalWord, Int>()
        localWords.forEach { localWord ->
            wordAndCountMap[localWord] = 0
            localWord.timeList.forEach {
                timeList.forEachIndexed { index, time ->
                    if (it - time in 0 until ONE_DAY) {
                        wordAndCountMap[localWord] = wordAndCountMap[localWord]!! + 1
                        valueList[index]++
                        return@forEachIndexed
                    }
                }
            }
        }
        val biggestValue = wordAndCountMap.entries.maxByOrNull { it.value }?.value ?: 0
        return Triple(timeList, valueList.toList(), wordAndCountMap.entries
                .asSequence()
                .filter { it.value == biggestValue }
                .map { it.key }
                .toList() to biggestValue)
    }

    // 获取最近一周的时间戳
    private inline val weekTimestampList
        get() = getTimestampList(7)

    // 获取最近一个月的时间戳
    private inline val monthTimestampList
        get() = getTimestampList(30)

    // 获取时间戳列表
    private fun getTimestampList(count: Int): List<Long> = ArrayList<Long>().apply {
        var timestamp = todayTimestamp
        add(timestamp)
        repeat(count - 1) {
            val frontTimestamp = timestamp - ONE_DAY
            add(frontTimestamp)
            timestamp = frontTimestamp
        }
    }.reversed()

}