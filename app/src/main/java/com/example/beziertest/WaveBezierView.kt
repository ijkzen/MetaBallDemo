package com.example.beziertest

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.View
import android.view.animation.LinearInterpolator


class WaveBezierView : View, View.OnClickListener {
    private var mPaint: Paint? = null
    private var mPath: Path? = null
    private val mWaveLength = 1000
    private var mOffset = 0
    private var mScreenHeight = 0
    private var mScreenWidth = 0
    private var mWaveCount = 0
    private var mCenterY = 0

    constructor(context: Context?) : super(context) {}
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        mPath = Path()
        mPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mPaint?.setColor(Color.LTGRAY)
        mPaint?.setStyle(Paint.Style.FILL_AND_STROKE)
        setOnClickListener(this)
    }

    protected override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mScreenHeight = h
        mScreenWidth = w
        mWaveCount = Math.round(mScreenWidth / mWaveLength + 1.5).toInt()
        mCenterY = mScreenHeight / 2
    }

    protected override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        mPath?.reset()
        mPath?.moveTo(-mWaveLength + mOffset.toFloat(), mCenterY.toFloat())
        for (i in 0 until mWaveCount) {
            // + (i * mWaveLength)
            // + mOffset
            mPath?.quadTo(
                -mWaveLength * 3 / 4 + i * mWaveLength + mOffset.toFloat(),
                mCenterY + 60F,
                -mWaveLength / 2 + i * mWaveLength + mOffset.toFloat(),
                mCenterY.toFloat()
            )
            mPath?.quadTo(
                -mWaveLength / 4 + i * mWaveLength + mOffset.toFloat(),
                mCenterY - 60F,
                i * mWaveLength + mOffset.toFloat(),
                mCenterY.toFloat()
            )
        }
        mPath?.lineTo(mScreenWidth.toFloat(), mScreenHeight.toFloat())
        mPath?.lineTo(0F, mScreenHeight.toFloat())
        mPath?.close()
        canvas.drawPath(mPath!!, mPaint!!)
    }

    override fun onClick(view: View?) {
        val animator = ValueAnimator.ofInt(0, mWaveLength)
        animator.duration = 1000
        animator.repeatCount = ValueAnimator.INFINITE
        animator.interpolator = LinearInterpolator()
        animator.addUpdateListener { animation ->
            mOffset = animation.animatedValue as Int
            postInvalidate()
        }
        animator.start()
    }
}
