package com.w10group.hertzdictionary.core

/**
 * 日期以及时间工具函数
 */

expect fun Long.fmtMonthDay(): String

expect fun Long.fmtDateNormal(): String

expect fun Long.fmtDateAll(): String

expect fun Long.fmtHourMinutes(): String

// 获取当前时间戳
expect val currentTimestamp: Long

// 获取今天 0 时的时间戳
expect val todayTimestamp: Long