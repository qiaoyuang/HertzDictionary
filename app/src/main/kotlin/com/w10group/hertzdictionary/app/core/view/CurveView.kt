package com.w10group.hertzdictionary.app.core.view

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.core.content.ContextCompat
import com.w10group.hertzdictionary.app.R
import com.w10group.hertzdictionary.core.fmtDateNormal
import com.w10group.hertzdictionary.core.fmtMonthDay
import org.jetbrains.anko.dip
import org.jetbrains.anko.sp
import java.text.NumberFormat
import kotlin.math.abs

/**
 * 查询频数曲线自定义 View
 * @author Qiao
 */

class CurveView : View {

    constructor(context: Context) : super(context)
    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet)
    constructor(context: Context, attributeSet: AttributeSet, defStyleAttr: Int) : super(context, attributeSet, defStyleAttr)

    /**
     * 颜色
     */
    private val darkBlue = ContextCompat.getColor(context, R.color.pool_curve_blue_dark)
    private val lightBlue = ContextCompat.getColor(context, R.color.pool_curve_blue_light)
    private val windowBackground = ContextCompat.getColor(context, R.color.pool_curve_window_background)
    private val white = Color.WHITE

    /**
     * dp 以及 sp 值
     */
    private val dp1 = 1.dp
    private val dp2 = 2.dp
    private val dp4 = 4.dp
    private val dp6 = 6.dp
    private val dp12 = 12.dp
    private val dp16 = 16.dp
    private val dp24 = 24.dp
    private val dp28 = 28.dp
    private val dp32 = 32.dp
    private val dp48 = 48.dp
    private val dp96 = 96.dp
    private val sp10 = 10.sp
    private val sp12 = 12.sp

    /**
     * 画笔
     */

    // 文字画笔
    private val mTextPaint = Paint().apply {
        isAntiAlias = true
        color = ContextCompat.getColor(context, R.color.text_disable_color)
        textSize = sp10
    }

    // 虚线画笔
    private val mDashLinePaint = Paint().apply {
        isAntiAlias = true
        color = ContextCompat.getColor(context, R.color.list_divider_color)
        pathEffect = DashPathEffect(floatArrayOf(dp6, dp4), 0f)
        strokeWidth = dp1
    }

    // 浅蓝色图形画笔
    private val mGraphicsPaint = Paint().apply {
        isAntiAlias = true
        color = lightBlue
        pathEffect = CornerPathEffect(dp4)
    }

    // 深蓝色曲线画笔
    private val mCurvePaint = Paint().apply {
        isAntiAlias = true
        color = darkBlue
        style = Paint.Style.STROKE
        pathEffect = CornerPathEffect(dp4)
        strokeWidth = dp2
    }

    // 查询频数单位画笔
    private val mUnitPaint = Paint().apply {
        isAntiAlias = true
        textSize = sp10
    }

    // 点击处竖线画笔
    private val mVerticalLinePaint = Paint().apply {
        isAntiAlias = true
        color = darkBlue
        strokeWidth = dp1
    }

    // 触摸点画笔
    private val mTouchPaint = Paint().apply {
        isAntiAlias = true
        textSize = sp12
    }

    /**
     * 路径
     */

    // 浅蓝色图形路径
    private val mGraphicsPath = Path()

    // 深蓝色曲线路径
    private val mCurvePath = Path()

    private var isPathEmpty = false
        set(value) {
            field = value
            if (value) {
                mGraphicsPath.reset()
                mCurvePath.reset()
            }
        }

    /**
     * 一些参数
     */

    // 图标的时间（X 轴参数）
    private val time = ArrayList<Long>()

    // 图表的查询频数值（Y 轴参数）
    private val value = ArrayList<Int>()

    // 触摸点的横纵坐标
    private var touchX = 0f
    private var touchY = 0f

    private val mFormat = NumberFormat.getInstance().apply {
        maximumFractionDigits = 1
    }

    private val mDefaultUnit = context.getString(R.string.default_unit)

    // 真实宽度
    private var realWidth = 0f

    // 设置数据
    fun setData(xList: List<Long>, yList: List<Int>) {
        time.clear()
        time.addAll(xList)
        value.clear()
        value.addAll(yList)
        require(time.size == value.size) { "xList and yList must be equal in size." }
        require(time.size >= 2) { "The size of xList and yList must be greater than 1." }
        touchX = 0f
        touchY = 0f
        isPathEmpty = true
        invalidate()
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        realWidth = width - dp4 * 1.5f
    }

    override fun onDraw(canvas: Canvas) = canvas whenTimeAndValueNotEmpty {
        drawXText()
        drawYText()
        drawUnit()
        drawDottedLine()
        drawCurve()
        drawWindow()
    }

    private inline infix fun Canvas.whenTimeAndValueNotEmpty(block: Canvas.() -> Unit) {
        if (time.isNotEmpty() && value.isNotEmpty())
            block()
    }

    // 第一步：绘制 X 轴坐标参数（时间）
    private fun Canvas.drawXText() {
        val time1 = time.first().fmtMonthDay()
        val time4 = time.last().fmtMonthDay()
        val position = time.size / 3
        val time2 = time[position].fmtMonthDay()
        val time3 = time[position shl 1].fmtMonthDay()
        val y = (height * 9 / 10).toFloat()
        val fWidth = realWidth
        val x1 = fWidth / 10
        val x2 = x1 * 4 - dp16
        val x3 = x1 * 7 - dp24
        val x4 = fWidth - dp32
        infix fun String.drawTextX(x: Float) = drawText(this, x, y, mTextPaint)
        time1 drawTextX x1
        time2 drawTextX x2
        time3 drawTextX x3
        time4 drawTextX x4
    }

    // 第二步：绘制 Y 轴坐标参数（查询频数）
    private fun Canvas.drawYText() {
        val maxValue = value.maxOrNull()!!
        val value1 = "0"
        val (value2, value3, value4) = if (maxValue == 0)
            Triple("1", "2", "3")
        else Triple(mFormat.format(maxValue.toFloat() / 3),
                mFormat.format(maxValue.toFloat() / 3 * 2),
                "$maxValue")
        val x = 0f
        val y1 = (height / 5).toFloat()
        val y2 = y1 * 2
        val y3 = y1 * 3
        val y4 = y1 * 4
        infix fun String.drawTextY(y: Float) = drawText(this, x, y, mTextPaint)
        value4 drawTextY y1
        value3 drawTextY y2
        value2 drawTextY y3
        value1 drawTextY y4
    }

    // 第三步：绘制右上角查询频数单位
    private fun Canvas.drawUnit() {
        val x = width.toFloat() - dp32
        val y = dp24
        mUnitPaint.color = darkBlue
        drawText(mDefaultUnit, x, y, mUnitPaint)
        mUnitPaint.color = lightBlue
        drawRoundRect(x - dp4, y - dp12, x + dp28, y + dp4, 10f, 10f, mUnitPaint)
    }

    // 第四步：绘制横向虚线
    private fun Canvas.drawDottedLine() {
        val stopX = realWidth
        val startX = realWidth / 10
        fun drawLine(y: Float) = drawLine(startX, y, stopX, y, mDashLinePaint)
        val y1 = (height / 5).toFloat()
        val y2 = y1 * 2
        val y3 = y1 * 3
        val y4 = y1 * 4
        drawLine(y1)
        drawLine(y2)
        drawLine(y3)
        drawLine(y4)
    }

    // 第五步：绘制查询频数曲线以及下方浅蓝色区域
    private fun Canvas.drawCurve() {
        if (!isPathEmpty) {
            if (value.any { it != 0 })
                drawPath(mGraphicsPath, mGraphicsPaint)
            drawPath(mCurvePath, mCurvePaint)
            return
        }
        val width = realWidth
        val x0 = width / 10
        val y0 = (height / 5 * 4).toFloat()
        if (value.all { it == 0 }) {
            mCurvePath.moveTo(x0, y0)
            mCurvePath.lineTo(width, y0)
            drawPath(mCurvePath, mCurvePaint)
            return
        }
        mGraphicsPath.moveTo(x0, y0)
        val (x1, y1) = valueToCoordinate(0)
        mGraphicsPath.lineTo(x1, y1)
        mCurvePath.moveTo(x1, y1)
        for (index in 1 until value.size) {
            val (x, y) = valueToCoordinate(index)
            mGraphicsPath.lineTo(x, y)
            mCurvePath.lineTo(x, y)
        }
        mGraphicsPath.lineTo(width, y0)
        mGraphicsPath.close()
        drawPath(mGraphicsPath, mGraphicsPaint)
        drawPath(mCurvePath, mCurvePaint)
    }

    // 第六步：绘制触摸点以及弹窗
    private fun Canvas.drawWindow() = if (touchX > 0 && touchY > 0) {
        // 获取与触摸点最近的有值的点在 time 和 value 中的 index
        val index = getTimeTemp()
        if (index >= 0) {
            // 获取要绘制竖线的 x 与 y 坐标
            val (x, y) = valueToCoordinate(index)
            val radius = dp4
            val startY = height / 5 - radius
            val endY = height.toFloat() / 5 * 4

            // 绘制弹窗
            val touchDiaPowerText = context.getString(R.string.curve_view_count, "${value[index]}$mDefaultUnit")
            val touchTimeText = time[index].fmtDateNormal()
            val windowWidth = dp96
            val offset = dp16
            val windowHeight = dp48
            val binaryOffset = offset / 2
            val windowX = if (x < realWidth / 2) x + binaryOffset else x - windowWidth - offset
            val windowY = if (y < height shr 1) y + binaryOffset else y - windowHeight - offset

            // 绘制竖线
            drawLine(x, startY, x, endY, mVerticalLinePaint)
            // 绘制白边蓝心圆
            mTouchPaint.color = white
            drawCircle(x, y, radius * 1.5f, mTouchPaint)
            mTouchPaint.color = darkBlue
            drawCircle(x, y, radius, mTouchPaint)

            // 绘制深色背景
            mTouchPaint.color = windowBackground
            drawRoundRect(windowX, windowY, windowX + windowWidth, windowY + windowHeight, dp4, dp4, mTouchPaint)
            // 绘制时间文字
            mTouchPaint.color = white
            val drawX = windowX + offset / 2
            val drawY = windowY + offset
            drawText(touchTimeText, drawX, drawY, mTouchPaint)
            // 绘制查询频数值文字
            drawText(touchDiaPowerText, drawX, drawY + windowHeight / 2, mTouchPaint)
        } else Unit
    } else Unit

    // 给出 index，返回该 index 对应数据在图表中的坐标
    private fun valueToCoordinate(index: Int): FloatArray {
        // 计算时间
        val diff = time[index] - time.first()
        val maxDiff = time.last() - time.first()
        val diffScale = diff.toFloat() / maxDiff.toFloat()
        val offset = realWidth / 10
        val maxWidth = offset * 9
        val x = diffScale * maxWidth + offset
        // 计算查询频数
        val max = value.maxOrNull()!!.toFloat()
        val baseHeight = height.toFloat() / 5
        val y = if (max == 0f) baseHeight * 4 else (max - value[index]) / max * (baseHeight * 3) + baseHeight
        return floatArrayOf(x, y)
    }

    // 给定横坐标 X，计算出对应的时间与值的 index
    private fun getTimeTemp(): Int {
        // 获取 realTime 的过程本质上就是 valueToCoordinate 函数计算 x 过程的逆运算
        val offset = realWidth / 10
        val maxWidth = offset * 9
        val diffScale = (touchX - offset) / maxWidth
        val maxDiff = time.last() - time.first()
        val diff = diffScale * maxDiff
        // 得到触摸点对应的大概的时间戳值
        val touchTime = diff + time.first()
        // 获得 time 中与 touchTime 差值的绝对值最小的值
        val realTime = time.minByOrNull { abs(it - touchTime) }
        return if (realTime != null) time.indexOf(realTime) else -1
    }

    private inline val Int.dp
        get() = dip(this).toFloat()

    private inline val Int.sp
        get() = sp(this).toFloat()

    @Suppress("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        touchX = event.x
        touchY = event.y
        isPathEmpty = false
        invalidate()
        return true
    }

}