package com.example.beziertest

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import kotlin.math.pow
import kotlin.math.sqrt

class MetaBallView : View {

    private val mPaint = Paint()
    private val mPath = Path()
    private var k1: Float = 0F
    private var k2: Float = 0F
    private var radius = 50F
    private var bezierX: Float = 300F
    private var bezierY: Float = 500F
    private var lastTime: Long = 0
    private var isUp = false

    private val slop = ViewConfiguration.get(context).scaledTouchSlop

    init {
        mPaint.isAntiAlias = true
        mPaint.color = Color.RED
        mPaint.style = Paint.Style.FILL
    }

    companion object {
        const val ORIGIN_X: Float = 300F
        const val ORIGIN_Y: Float = 500F
    }

    constructor(context: Context) : super(context)

    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet)

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        lastTime = System.currentTimeMillis()
        drawFirstCircle(canvas)
        drawBezierLine(canvas)
        drawSecondCircle(canvas)
        moveCircle()
    }

    private fun drawFirstCircle(canvas: Canvas?) {
        radius = (50F + (20F - 50F) * (getDistance() / 1000F)).toFloat()
        if (radius < 20F) {
            radius = 20F
        }
        canvas?.drawCircle(ORIGIN_X, ORIGIN_Y, radius, mPaint)
    }

    private fun drawBezierLine(canvas: Canvas?) {
        if (canDrawBezierLine()) {
            val list = getBezierPointList()
            mPath.reset()
            mPath.moveTo(list[0].x, list[0].y)
            mPath.quadTo(list[4].x, list[4].y, list[3].x, list[3].y)
            mPath.lineTo(list[2].x, list[2].y)
            mPath.quadTo(list[4].x, list[4].y, list[1].x, list[1].y)
            mPath.close()
            canvas?.drawPath(mPath, mPaint)
        }
    }

    private fun canDrawBezierLine(): Boolean {
        val distance = getDistance()

        return distance > 2 * radius && distance < 7 * radius
    }

    private fun getDistance(): Double {
        val distance = (bezierX - ORIGIN_X).toDouble().pow(2) +
                (bezierY - ORIGIN_Y).toDouble().pow(2)

        return sqrt(distance)
    }

    private fun getBezierPointList(): List<PointF> {
        val middlePoint = PointF()
        middlePoint.x = (ORIGIN_X + bezierX) / 2
        middlePoint.y = (ORIGIN_Y + bezierY) / 2
        val a = middlePoint.x
        val b = middlePoint.y
        val c = ORIGIN_X
        val d = ORIGIN_Y
        val r = radius

        val aParam: Float =
            8 * a * c - 4 * a * a - 4 * c * c + 4 * r * r

        val bParam: Float =
            -8 * a * b + 8 * b * c + 8 * a * d - 8 * c * d

        val cParam: Float =
            -4 * b * b + 8 * b * d - 4 * d * d + 4 * r * r

        val tmp: Float = sqrt(bParam * bParam - 4 * aParam * cParam)
        k1 = (-bParam + tmp) / (2 * aParam)
        k2 = (-bParam - tmp) / (2 * aParam)

        val list = ArrayList<PointF>()
        list.add(getBezierPoint(ORIGIN_X, ORIGIN_Y, middlePoint.x, middlePoint.y, -k1))
        list.add(getBezierPoint(ORIGIN_X, ORIGIN_Y, middlePoint.x, middlePoint.y, -k2))
        list.add(getBezierPoint(bezierX, bezierY, middlePoint.x, middlePoint.y, -k1))
        list.add(getBezierPoint(bezierX, bezierY, middlePoint.x, middlePoint.y, -k2))
        list.add(middlePoint)

        return list
    }

    private fun getBezierPoint(
        c: Float,
        d: Float,
        a: Float,
        b: Float,
        k: Float
    ): PointF {
        val pointF = PointF()
        pointF.x = (a * k * k - b * k + d * k + c) / (k * k + 1)
        pointF.y = k * pointF.x - a * k + b
        return pointF
    }

    private fun drawSecondCircle(canvas: Canvas?) {
        canvas?.drawCircle(bezierX, bezierY, radius, mPaint)
    }

    private fun moveCircle() {
        if (isUp) {
            move2Origin()
        }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        event?.let {
            Log.e("test", "action type: ${event.action}")
            when (event.action) {

                MotionEvent.ACTION_MOVE -> {
                    isUp = false
                    bezierX = event.x
                    bezierY = event.y
                    invalidate()
                }
                MotionEvent.ACTION_UP -> {
                    isUp = true
                    move2Origin()
                }
                else -> {
                }
            }
        }
        return true
    }

    private fun move2Origin() {
        if (getDistance() > 3 * radius) {

            when {
                bezierX > ORIGIN_X -> {
                    val k = (bezierY - ORIGIN_Y) / (bezierX - ORIGIN_X)
                    val deltaX = sqrt(slop * slop / (k * k + 1))
                    bezierX -= deltaX
                    bezierY -= k * deltaX
                }
                bezierX < ORIGIN_X -> {
                    val k = (bezierY - ORIGIN_Y) / (bezierX - ORIGIN_X)
                    val deltaX = sqrt(slop * slop / (k * k + 1))
                    bezierX += deltaX
                    bezierY += k * deltaX
                }
                else -> {
                    bezierY = if (ORIGIN_Y < bezierY) bezierY - slop else bezierY + slop
                }
            }
            invalidate()
        }
    }
}