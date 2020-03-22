package com.w10group.hertzdictionary.app.biz.ui.statistics

/**
 * 统计页面的 UI 更新时需要的数据
 * @author Qiao
 */

data class UIUpdateData(val totalCountText: String,
                        val averageCountText: String,
                        val mostWordText: String,
                        val timeList: List<Long>,
                        val valueList: List<Int>)