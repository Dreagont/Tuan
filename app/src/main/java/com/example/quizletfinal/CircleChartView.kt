package com.example.quizletfinal// CircleChartView.kt
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View

class CircleChartView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val rightColor = Color.GREEN
    private val wrongColor = Color.RED

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val rectF = RectF()

    var rightPercentage: Float = 0f
    var wrongPercentage: Float = 0f

    init {
        // Default constructor
    }

    constructor(
        context: Context,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        rightPercentage: Float,
        wrongPercentage: Float
    ) : this(context, attrs, defStyleAttr) {
        this.rightPercentage = rightPercentage
        this.wrongPercentage = wrongPercentage
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val width = getWidth().toFloat()
        val height = getHeight().toFloat()
        val radius = Math.min(width, height) / 2

        rectF.set(0f, 0f, width, height)

        // Draw "wrong" segment
        paint.color = wrongColor
        val wrongSweepAngle = 360 * (wrongPercentage / 100f)
        canvas.drawArc(rectF, -90f, wrongSweepAngle, true, paint)

        // Draw "right" segment
        paint.color = rightColor
        val rightSweepAngle = 360 * (rightPercentage / 100f)
        canvas.drawArc(rectF, -90f + wrongSweepAngle, rightSweepAngle, true, paint)
    }
}
