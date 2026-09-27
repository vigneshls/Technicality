package com.littleengineer.game.ui

import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.view.View
import android.view.animation.OvershootInterpolator

/**
 * A single, large, chunky grid tile. Draws itself directly on a Canvas so the
 * same class can represent an obstacle, cleared ground, a marked footprint, a
 * dug square, or poured concrete -- just by animating its fill color.
 *
 * Every focused tile gets a thick, high-contrast glowing border since this
 * platform (Android TV / D-pad) has no touch feedback at all.
 */
class TileCellView(context: Context) : View(context) {

    var fillColor: Int = Color.WHITE
        private set

    private val fillPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { style = Paint.Style.FILL }
    private val borderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        color = Color.parseColor("#FFEB3B")
        strokeWidth = 14f
    }
    private val quietBorderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        color = Color.argb(60, 0, 0, 0)
        strokeWidth = 4f
    }

    private var scale = 1f
    private val rect = RectF()
    private val cornerRadius = 28f

    /** Emoji/glyph shown centered on the tile while it holds focus -- the "vehicle cursor". */
    var focusIcon: String? = null
        set(value) {
            field = value
            invalidate()
        }
    private val iconPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        textAlign = Paint.Align.CENTER
        textSize = 72f
    }

    init {
        isFocusable = true
        isFocusableInTouchMode = false
        setFillColor(Color.WHITE)
    }

    fun setFillColor(color: Int) {
        fillColor = color
        fillPaint.color = color
        invalidate()
    }

    /** Animates a color change with a satisfying "pop" -- the main feedback for every stage action. */
    fun animateToColor(newColor: Int, onMidpoint: (() -> Unit)? = null) {
        val startColor = fillColor
        val scaleUp = ValueAnimator.ofFloat(1f, 1.25f, 1f).apply {
            duration = 260
            interpolator = OvershootInterpolator()
            addUpdateListener {
                scale = it.animatedValue as Float
                invalidate()
            }
        }
        val colorAnim = ValueAnimator.ofObject(ArgbEvaluator(), startColor, newColor).apply {
            duration = 180
            addUpdateListener {
                fillColor = it.animatedValue as Int
                fillPaint.color = fillColor
                invalidate()
            }
        }
        onMidpoint?.invoke()
        scaleUp.start()
        colorAnim.start()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val cx = width / 2f
        val cy = height / 2f
        canvas.save()
        canvas.scale(scale, scale, cx, cy)

        val pad = 10f
        rect.set(pad, pad, width - pad, height - pad)
        canvas.drawRoundRect(rect, cornerRadius, cornerRadius, fillPaint)
        canvas.drawRoundRect(rect, cornerRadius, cornerRadius, quietBorderPaint)

        if (isFocused) {
            val focusRect = RectF(pad / 2, pad / 2, width - pad / 2, height - pad / 2)
            canvas.drawRoundRect(focusRect, cornerRadius, cornerRadius, borderPaint)
            focusIcon?.let { icon ->
                val textY = cy - (iconPaint.descent() + iconPaint.ascent()) / 2f
                canvas.drawText(icon, cx, textY, iconPaint)
            }
        }
        canvas.restore()
    }

    override fun onFocusChanged(gainFocus: Boolean, direction: Int, previouslyFocusedRect: android.graphics.Rect?) {
        super.onFocusChanged(gainFocus, direction, previouslyFocusedRect)
        invalidate()
    }
}
