package com.w10group.hertzdictionary.core

import kotlinx.cinterop.memScoped
import platform.Foundation.*

/**
 * 日期以及时间工具函数（待实现）
 * @author qiaoyuang
 */

actual fun Long.fmtMonthDay(): String = ""

actual fun Long.fmtDateNormal(): String = ""

actual fun Long.fmtDateAll(): String = ""

actual fun Long.fmtHourMinutes(): String = ""

// 获取当前时间戳
actual inline val currentTimestamp
    get() = memScoped {
        NSDate.dateWithTimeIntervalSinceNow(0.0)
            .timeIntervalSince1970().toLong() * 1000
    }

// 获取今天 0 时的时间戳
@OptIn(ExperimentalUnsignedTypes::class)
actual inline val todayTimestamp
    get() = memScoped {
        val calendar = NSCalendar.currentCalendar
        val now = NSDate()
        val components = calendar.components(NSCalendarUnitYear or NSCalendarUnitMonth or NSCalendarUnitDay, fromDate = now)
        val startDate = calendar.dateFromComponents(components)
        0L
    }