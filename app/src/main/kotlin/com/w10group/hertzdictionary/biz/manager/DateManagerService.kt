package com.w10group.hertzdictionary.biz.manager

import com.w10group.hertzdictionary.biz.bean.LocalWord
import java.util.*
import kotlin.collections.ArrayList

/**
 * 日期时间管理服务
 * @author Qiao
 */

typealias CurveValue = Pair<List<Long>, List<Int>>

object DateManagerService {

    private const val ONE_DAY = 86400

    // 生成某单词的周曲线数据
    fun createWeekValue(localWord: LocalWord): CurveValue = createXYValue(weekTimestampList, localWord)

    // 生成某单词的月曲线数据
    fun createMonthValue(localWord: LocalWord): CurveValue = createXYValue(monthTimestampList, localWord)

    private fun createXYValue(timeList: List<Long>, localWord: LocalWord): CurveValue {
        val valueList = IntArray(timeList.size) { 0 }
        localWord.timeList?.apply {
            forEach {
                timeList.forEachIndexed { index, _it ->
                    if (it - _it < ONE_DAY) {
                        valueList[index]++
                        return@forEachIndexed
                    }
                }
            }
        }
        return timeList to valueList.toList()
    }

    // 获取最近一周的时间戳
    private val weekTimestampList
        get() = getTimestampList(7)

    // 获取最近一个月的时间戳
    private val monthTimestampList
        get() = getTimestampList(30)

    // 获取当前时间戳
    private val currentTimestamp
        get() = System.currentTimeMillis()

    // 获取时间戳列表
    private fun getTimestampList(count: Int): List<Long> = ArrayList<Long>().apply {
        var currentTimestamp = Calendar.getInstance().apply {
            timeInMillis = currentTimestamp
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
        add(currentTimestamp)
        repeat(count - 1) {
            val frontTimestamp = currentTimestamp - ONE_DAY
            add(frontTimestamp)
            currentTimestamp = frontTimestamp
        }
    }.reversed()

}