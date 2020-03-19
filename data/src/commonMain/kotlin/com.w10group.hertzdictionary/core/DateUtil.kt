package com.w10group.hertzdictionary.core

/**
 * 日期以及时间工具函数
 */

expect fun Long.fmtMonthDay(): String

expect fun Long.fmtDateNormal(): String

expect fun Long.fmtDateAll(): String

expect fun Long.fmtHourMinutes(): String