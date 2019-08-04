package com.w10group.hertzdictionary.core.view

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.core.content.ContextCompat
import com.w10group.hertzdictionary.R
import com.w10group.hertzdictionary.core.fmtDateNormal
import com.w10group.hertzdictionary.core.fmtMonthDay
import kotlin.math.abs
import org.jetbrains.anko.dip
import org.jetbrains.anko.sp
import java.text.NumberFormat

/**
 * 算力曲线自定义 View
 * @author Qiao
 */
class CurveView : View {

    companion object {
        private const val DEFAULT_UNIT = "次/天"
    }

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
    private val dp1 = 1.initDPValue()
    private val dp2 = 2.initDPValue()
    private val dp4 = 4.initDPValue()
    private val dp6 = 6.initDPValue()
    private val dp12 = 12.initDPValue()
    private val dp16 = 16.initDPValue()
    private val dp24 = 24.initDPValue()
    private val dp28 = 28.initDPValue()
    private val dp32 = 32.initDPValue()
    private val dp48 = 48.initDPValue()
    private val dp96 = 96.initDPValue()
    private val sp10 = 10.initSPValue()
    private val sp12 = 12.initSPValue()

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

    // 算力单位画笔
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

    // 图表的算力值（Y 轴参数）
    private val value = ArrayList<Int>()

    // 触摸点的横纵坐标
    private var touchX = 0f
    private var touchY = 0f

    private val mFormat = NumberFormat.getInstance().apply {
        maximumFractionDigits = 1
    }

    // 设置数据
    fun setData(xList: List<Long>, yList: List<Int>) {
        time.clear()
        time.addAll(xList)
        value.clear()
        value.addAll(yList)
        if (time.size != value.size)
            throw IllegalArgumentException("xList and yList must be equal in size.")
        if (time.size < 2)
            throw IllegalArgumentException("The size of xList and yList must be greater than 1.")
        touchX = 0f
        touchY = 0f
        isPathEmpty = true
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        if (time.isNotEmpty() && value.isNotEmpty()) with(canvas) {
            drawXText()
            drawYText()
            drawUnit()
            drawDottedLine()
            drawCurve()
            drawWindow()
        }
    }

    // 第一步：绘制 X 轴坐标参数（时间）
    private fun Canvas.drawXText() {
        val time1 = time[0].fmtMonthDay()
        val time4 = time.last().fmtMonthDay()
        val position = time.size / 3
        val time2 = time[position].fmtMonthDay()
        val time3 = time[position * 2].fmtMonthDay()
        val y = (height * 9 / 10).toFloat()
        val fWidth = width.toFloat()
        val x1 = fWidth / 10
        val x2 = x1 * 4 - dp16
        val x3 = x1 * 7 - dp24
        val x4 = fWidth - dp32
        drawText(time1, x1, y, mTextPaint)
        drawText(time2, x2, y, mTextPaint)
        drawText(time3, x3, y, mTextPaint)
        drawText(time4, x4, y, mTextPaint)
    }

    // 第二步：绘制 Y 轴坐标参数（算力）
    private fun Canvas.drawYText() {
        val maxValue = value.max()!!
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
        drawText(value4, x, y1, mTextPaint)
        drawText(value3, x, y2, mTextPaint)
        drawText(value2, x, y3, mTextPaint)
        drawText(value1, x, y4, mTextPaint)
    }

    // 第三步：绘制右上角算力单位
    private fun Canvas.drawUnit() {
        val x = width.toFloat() - dp32
        val y = dp24
        mUnitPaint.color = darkBlue
        drawText(DEFAULT_UNIT, x, y, mUnitPaint)
        mUnitPaint.color = lightBlue
        drawRoundRect(x - dp4, y - dp12, x + dp28, y + dp4, 10f, 10f, mUnitPaint)
    }

    // 第四步：绘制横向虚线
    private fun Canvas.drawDottedLine() {
        val startX = (width / 10).toFloat()
        val stopX = width.toFloat()
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

    // 第五步：绘制算力曲线以及下方浅蓝色区域
    private fun Canvas.drawCurve() {
        if (!isPathEmpty) {
            if (value.any { it != 0 })
                drawPath(mGraphicsPath, mGraphicsPaint)
            drawPath(mCurvePath, mCurvePaint)
            return
        }
        val x0 = (width / 10).toFloat()
        val y0 = (height / 5 * 4).toFloat()
        if (value.all { it == 0 }) {
            mCurvePath.moveTo(x0, y0)
            mCurvePath.lineTo(width.toFloat(), y0)
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
        mGraphicsPath.lineTo(width.toFloat(), y0)
        mGraphicsPath.close()
        drawPath(mGraphicsPath, mGraphicsPaint)
        drawPath(mCurvePath, mCurvePaint)
    }

    // 第六步：绘制触摸点以及弹窗
    private fun Canvas.drawWindow() {
        if (touchX > 0 && touchY > 0) {
            // 获取与触摸点最近的有值的点在 time 和 value 中的 index
            val index = getTimeTemp()
            if (index >= 0) {
                // 获取要绘制竖线的 x 与 y 坐标
                val (x, y) = valueToCoordinate(index)
                val radius = dp4
                val startY = height / 5 - radius
                val endY = height.toFloat() / 5 * 4

                // 绘制弹窗
                val touchDiaPowerText = context.getString(R.string.curve_view_count, "${value[index]}$DEFAULT_UNIT")
                val touchTimeText = time[index].fmtDateNormal()
                val windowWidth = dp96
                val offset = dp16
                val windowHeight = dp48
                val binaryOffset = offset / 2
                val windowX = if (x < width / 2) x + binaryOffset else x - windowWidth - offset
                val windowY = if (y < height / 2) y + binaryOffset else y - windowHeight - offset

                // 画竖线
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
                // 绘制算力值文字
                drawText(touchDiaPowerText, drawX, drawY + windowHeight / 2, mTouchPaint)
            }
        }
    }

    // 给出 index，返回该 index 对应数据在图表中的坐标
    private fun valueToCoordinate(index: Int): Pair<Float, Float> {
        // 计算时间
        val diff = time[index] - time.first()
        val maxDiff = time.last() - time.first()
        val diffScale = diff.toFloat() / maxDiff.toFloat()
        val offset = width.toFloat() / 10
        val maxWidth = offset * 9
        val x = diffScale * maxWidth + offset
        // 计算算力
        val max = value.max()!!.toFloat()
        val baseHeight = height.toFloat() / 5
        val y = if (max == 0f) baseHeight * 4 else (max - value[index]) / max * (baseHeight * 3) + baseHeight
        return x to y
    }

    // 给定横坐标 X，计算出对应的时间与值的 index
    private fun getTimeTemp(): Int {
        // 获取 realTime 的过程本质上就是 valueToCoordinate 函数计算 x 过程的逆运算
        val offset = width.toFloat() / 10
        val maxWidth = offset * 9
        val diffScale = (touchX - offset) / maxWidth
        val maxDiff = time.last() - time.first()
        val diff = diffScale * maxDiff
        // 得到触摸点对应的大概的时间戳值
        val touchTime = diff + time.first()
        // 获得 time 中与 touchTime 差值的绝对值最小的值
        val realTime = time.minBy { abs(it - touchTime) }
        return if (realTime != null) time.indexOf(realTime) else -1
    }

    private fun Int.initDPValue(): Float = context.dip(this).toFloat()
    private fun Int.initSPValue(): Float = context.sp(this).toFloat()

    @Suppress("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        touchX = event.x
        touchY = event.y
        isPathEmpty = false
        invalidate()
        return true
    }

}