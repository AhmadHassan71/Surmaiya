package com.smd.surmaiya.HelperClasses


import android.R
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import kotlin.math.min


class CustomProgressBar @JvmOverloads constructor(context: Context?, attrs: AttributeSet? = null) :
    View(context, attrs) {
    private var semiCirclePaint: Paint? = null
    private var progressPaint: Paint? = null
    private var outerSemiCircleBounds: RectF? = null
    private var innerSemiCircleBounds: RectF? = null
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
        progressPaint!!.setColor(resources.getColor(R.color.holo_blue_light))
        outerSemiCircleBounds = RectF()
        innerSemiCircleBounds = RectF()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val width = width
        val height = height
        val diameter = min(width.toDouble(), height.toDouble()).toInt()

        // Define the bounds for the outer semi-circle (full circle)
        outerSemiCircleBounds!![0f, 0f, diameter.toFloat()] = diameter.toFloat()

        // Define the bounds for the inner semi-circle (half circle)
        val innerDiameter = diameter * 0.8f // Adjust the inner circle diameter as needed
        val offset = (diameter - innerDiameter) / 2
        innerSemiCircleBounds!![offset, offset, diameter - offset] = diameter - offset

        // Draw the outer semi-circle (full circle)
        canvas.drawArc(outerSemiCircleBounds!!, 90f, 180f, false, semiCirclePaint!!)

        // Draw the inner semi-circle (half circle)
        canvas.drawArc(innerSemiCircleBounds!!, 90f, 180f, false, semiCirclePaint!!)

        // Calculate progress bar path
        val progressAngle = 180 * (progress / 100f)
        val startX = offset + innerDiameter / 2
        val startY = offset + innerDiameter
        val endX = diameter - offset - innerDiameter / 2
        val controlX = (width / 2).toFloat()
        val controlY = height - offset

        // Draw the progress bar
        val path = Path()
        path.moveTo(startX, startY)
        path.quadTo(controlX, controlY, endX, startY)
        canvas.drawPath(path, progressPaint!!)

        // Draw a red line for the progress bar
        val redPaint = Paint()
        redPaint.setColor(Color.RED)
        redPaint.strokeWidth = 10f // Adjust thickness as needed
        canvas.drawLine(startX, startY, endX, startY, redPaint)
    }


    fun setProgress(progress: Float) {
        this.progress = progress
        invalidate()
    }
}
