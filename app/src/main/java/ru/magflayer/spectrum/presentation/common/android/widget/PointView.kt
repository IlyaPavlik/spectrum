package ru.magflayer.spectrum.presentation.common.android.widget

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View

import ru.magflayer.spectrum.presentation.common.helper.AppHelper

class PointView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = -1) : View(context, attrs, defStyleAttr) {

    companion object {
        private const val RADIUS_DP = 4f
        private const val STROKE_WIDTH_DP = 2f
    }

    private val aimPaint = Paint()
    var currentX: Float = 0.toFloat()
        private set
    var currentY: Float = 0.toFloat()
        private set
    private var circleRadius: Float = 0.toFloat()

    private val leftTopArc = RectF()
    private val leftBottomArc = RectF()
    private val rightTopArc = RectF()
    private val rightBottomArc = RectF()

    private val radius = AppHelper.convertDpToPixel(RADIUS_DP, context).toInt()

    private var onPointChangeListener: OnPointChangeListener? = null
    private var moveEnabled: Boolean = false
    private var moveActive: Boolean = false

    interface OnPointChangeListener {
        fun onPointChanged(x: Float, y: Float, radius: Int)
    }

    init {
        val circleStrokeWidth = AppHelper.convertDpToPixel(STROKE_WIDTH_DP, context)

        aimPaint.isAntiAlias = true
        aimPaint.strokeWidth = circleStrokeWidth
        aimPaint.style = Paint.Style.STROKE
    }

    fun setAimColor(color: Int) {
        aimPaint.color = color
        invalidate()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val parentWidth = View.MeasureSpec.getSize(widthMeasureSpec)
        val parentHeight = View.MeasureSpec.getSize(heightMeasureSpec)

        currentX = (parentWidth / 2).toFloat()
        currentY = (parentHeight / 2).toFloat()

        leftTopArc.set(currentX - 50, currentY - 50, currentX, currentY)
        leftBottomArc.set(currentX - 50, currentY, currentX, currentY + 50)
        rightTopArc.set(currentX, currentY - 50, currentX + 50, currentY)
        rightBottomArc.set(currentX, currentY, currentX + 50, currentY + 50)
    }

    override fun onDraw(canvas: Canvas) {
        canvas.drawCircle(currentX, currentY, circleRadius, aimPaint)

        canvas.drawArc(leftTopArc, 180f, 90f, false, aimPaint)
        canvas.drawArc(leftBottomArc, 180f, -90f, false, aimPaint)
        canvas.drawArc(rightTopArc, 0f, -90f, false, aimPaint)
        canvas.drawArc(rightBottomArc, 0f, 90f, false, aimPaint)

        onPointChangeListener?.onPointChanged(currentX, currentY, radius)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        val x = event.x
        val y = event.y

        if (!moveActive) {
            return super.onTouchEvent(event)
        }

        return when (event.action) {
            MotionEvent.ACTION_DOWN -> touchDown(x, y)
            MotionEvent.ACTION_MOVE -> touchMove(x, y)
            MotionEvent.ACTION_UP -> touchUp()
            else -> false
        }
    }

    private fun touchDown(x: Float, y: Float): Boolean {
        if (x > currentX - circleRadius
                && x < currentX + circleRadius
                && y > currentY - circleRadius
                && y < currentY + circleRadius) {
            moveEnabled = true
            currentX = x
            currentY = y
            invalidate()
            return true
        }
        return false
    }

    private fun touchMove(x: Float, y: Float): Boolean {
        if (moveEnabled) {
            currentX = x
            currentY = y
            invalidate()
            return true
        }
        return false
    }

    private fun touchUp(): Boolean {
        moveEnabled = false
        return true
    }
}
