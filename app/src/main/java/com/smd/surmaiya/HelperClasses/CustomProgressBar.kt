package com.smd.surmaiya.HelperClasses


import android.R
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import kotlin.math.min

class CustomProgressBar @JvmOverloads constructor(context: Context?, attrs: AttributeSet? = null) :
    View(context, attrs) {
    private var semiCirclePaint: Paint? = null
    private var progressPaint: Paint? = null
    private var semiCircleBounds: RectF? = null
    private var progress = 0f

    init {
        init()
    }

    private fun init() {
        semiCirclePaint = Paint()
        semiCirclePaint!!.isAntiAlias = true
        semiCirclePaint!!.style = Paint.Style.STROKE
        semiCirclePaint!!.strokeWidth = 10f // Adjust thickness as needed
        semiCirclePaint!!.setColor(resources.getColor(R.color.darker_gray))
        progressPaint = Paint()
        progressPaint!!.isAntiAlias = true
        progressPaint!!.style = Paint.Style.STROKE
        progressPaint!!.strokeWidth = 10f // Adjust thickness as needed
        progressPaint!!.setColor(resources.getColor(R.color.holo_red_light))
        semiCircleBounds = RectF()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val width = width
        val height = height
        val diameter = min(width.toDouble(), height.toDouble()).toInt()
        semiCircleBounds!![0f, 0f, diameter.toFloat()] = diameter.toFloat()
        canvas.drawArc(semiCircleBounds!!, 90f, 180f, false, semiCirclePaint!!)
        val sweepAngle = 180 * (progress / 100f)
        canvas.drawArc(semiCircleBounds!!, 180f, sweepAngle, false, progressPaint!!)
    }

    fun setProgress(progress: Float) {
        this.progress = progress
        invalidate()
    }
}
