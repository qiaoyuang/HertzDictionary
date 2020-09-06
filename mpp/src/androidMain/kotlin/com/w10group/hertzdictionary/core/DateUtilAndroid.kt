package com.w10group.hertzdictionary.core

import java.text.SimpleDateFormat
import java.util.*

/**
 * 日期以及时间工具函数
 */

actual fun Long.fmtMonthDay(): String = SimpleDateFormat("MM-dd", Locale.getDefault()).format(Date(this))

actual fun Long.fmtDateNormal(): String = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date(this))

actual fun Long.fmtDateAll(): String = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date(this))

actual fun Long.fmtHourMinutes(): String = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(this))

// 获取当前时间戳
actual inline val currentTimestamp
    get() = System.currentTimeMillis()

// 获取今天 0 时的时间戳
actual inline val todayTimestamp
    get() = Calendar.getInstance().apply {
        timeInMillis = currentTimestamp
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }.timeInMillis