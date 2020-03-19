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