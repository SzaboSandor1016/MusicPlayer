package com.example.core.ui

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RadialGradient
import android.graphics.RectF
import android.graphics.Shader
import android.util.AttributeSet
import android.view.View

class GradientBackgroundView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var radius: Float = 0f

    private var startColor: Int = resources.getColor(R.color.secondary, context.theme)
    private var centerColor: Int = resources.getColor(R.color.secondary, context.theme)
    private var endColor: Int = resources.getColor(R.color.primary, context.theme)

    init {
        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.GradientBackgroundView,
            0, 0
        ).apply {
            try {
                startColor = getColor(R.styleable.GradientBackgroundView_startColor, startColor)
                centerColor = getColor(R.styleable.GradientBackgroundView_centerColor, centerColor)
                endColor = getColor(R.styleable.GradientBackgroundView_endColor, endColor)
                radius = getDimension(R.styleable.GradientBackgroundView_cornerRadius, 0f)
            } finally {
                recycle()
            }
        }
    }

    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val shader = RadialGradient(
            width / 2f,
            height / 2f,
            width.coerceAtLeast(height) / 2f,
            intArrayOf(startColor, centerColor, endColor),
            null,
            Shader.TileMode.CLAMP
        )
        paint.shader = shader

        val rect = RectF(0f, 0f, width.toFloat(), height.toFloat())
        canvas.drawRoundRect(rect, radius, radius, paint)
    }
}