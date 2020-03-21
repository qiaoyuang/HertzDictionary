package com.w10group.hertzdictionary.core

/**
 * 日期以及时间工具函数（待实现）
 */

actual fun Long.fmtMonthDay(): String = ""

actual fun Long.fmtDateNormal(): String = ""

actual fun Long.fmtDateAll(): String = ""

actual fun Long.fmtHourMinutes(): String = ""

// 获取当前时间戳
actual inline val currentTimestamp
    get() = 0L

// 获取今天 0 时的时间戳
actual inline val todayTimestamp
    get() = 0L