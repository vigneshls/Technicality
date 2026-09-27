package com.littleengineer.game.ui.stages

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.view.View
import android.view.animation.OvershootInterpolator
import com.littleengineer.game.model.HouseConfig
import com.littleengineer.game.model.RoofType
import com.littleengineer.game.model.TreeChoice

/**
 * Canvas-drawn "basic house outline" assembled from placeholder shapes.
 * Deliberately a little wonky/asymmetric per the visual style brief, rather
 * than a photoreal render. Final art (Step 7 of the build order) replaces
 * this with real sprites; the animation sequencing here stays the same.
 */
class HouseRevealView(context: Context, private val config: HouseConfig) : View(context) {

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var doorOpenFraction = 0f // 0 = closed, 1 = fully open
    private var personVisible = false
    private var armSwing = 0f
    private var smokePhase = 0f
    private val smokeRunner = object : Runnable {
        override fun run() {
            smokePhase += 0.05f
            if (smokePhase > 1f) smokePhase = 0f
            invalidate()
            postDelayed(this, 40)
        }
    }

    fun playRevealSequence(onCelebration: () -> Unit) {
        postDelayed({
            ValueAnimator.ofFloat(0f, 1f).apply {
                duration = 500
                addUpdateListener {
                    doorOpenFraction = it.animatedValue as Float
                    invalidate()
                }
            }.start()
        }, 300)

        postDelayed({
            personVisible = true
            invalidate()
            val wave = ValueAnimator.ofFloat(-25f, 25f).apply {
                duration = 260
                repeatCount = 5
                repeatMode = ValueAnimator.REVERSE
                interpolator = OvershootInterpolator()
                addUpdateListener {
                    armSwing = it.animatedValue as Float
                    invalidate()
                }
            }
            wave.start()
            onCelebration()
        }, 900)

        if (config.roof == RoofType.FLAT_CHIMNEY) {
            postDelayed({ post(smokeRunner) }, 700)
        }
    }

    fun stopAnimations() {
        removeCallbacks(smokeRunner)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val w = width.toFloat()
        val h = height.toFloat()

        // Ground
        paint.color = Color.parseColor("#6FCB6F")
        canvas.drawRect(0f, h * 0.82f, w, h, paint)

        val wallLeft = w * 0.28f
        val wallRight = w * 0.72f
        val wallBottom = h * 0.82f
        val rowCount = config.wallRows.size.coerceAtLeast(1)
        val rowHeight = (h * 0.34f) / rowCount
        val wallTop = wallBottom - h * 0.34f

        // Walls, bottom row first
        for ((i, brick) in config.wallRows.withIndex()) {
            val top = wallBottom - (i + 1) * rowHeight
            val bottom = wallBottom - i * rowHeight
            paint.color = brick.colorInt
            canvas.drawRect(wallLeft, top, wallRight, bottom, paint)
        }
        if (config.wallRows.isEmpty()) {
            paint.color = Color.LTGRAY
            canvas.drawRect(wallLeft, wallTop, wallRight, wallBottom, paint)
        }

        // Roof (intentionally a bit oversized/wonky)
        paint.color = Color.parseColor("#8E44AD")
        when (config.roof) {
            RoofType.POINTED -> {
                val path = Path().apply {
                    moveTo(wallLeft - 30f, wallTop)
                    lineTo((wallLeft + wallRight) / 2, wallTop - h * 0.22f)
                    lineTo(wallRight + 30f, wallTop)
                    close()
                }
                canvas.drawPath(path, paint)
            }
            RoofType.DOME -> {
                val rect = RectF(wallLeft - 10f, wallTop - h * 0.22f, wallRight + 10f, wallTop + h * 0.05f)
                canvas.drawArc(rect, 180f, 180f, true, paint)
            }
            RoofType.FLAT_CHIMNEY -> {
                canvas.drawRect(wallLeft - 20f, wallTop - h * 0.06f, wallRight + 20f, wallTop, paint)
                // Chimney
                paint.color = Color.parseColor("#5D4037")
                val chimneyLeft = wallRight - 60f
                val chimneyTop = wallTop - h * 0.18f
                canvas.drawRect(chimneyLeft, chimneyTop, chimneyLeft + 30f, wallTop - h * 0.06f, paint)

                if (smokePhase > 0f) {
                    paint.color = Color.argb((120 * (1 - smokePhase)).toInt(), 220, 220, 220)
                    val puffY = chimneyTop - smokePhase * 80f
                    canvas.drawCircle(chimneyLeft + 15f, puffY, 14f + smokePhase * 10f, paint)
                }
            }
        }

        // Door (paint-colored), swings open around its left hinge
        val doorWidth = (wallRight - wallLeft) * 0.22f
        val doorHeight = (wallBottom - wallTop) * 0.6f
        val doorLeft = (wallLeft + wallRight) / 2 - doorWidth / 2
        val doorTop = wallBottom - doorHeight
        canvas.save()
        canvas.translate(doorLeft, doorTop)
        canvas.rotate(-doorOpenFraction * 60f, 0f, doorHeight)
        paint.color = config.paintColor
        canvas.drawRect(0f, 0f, doorWidth, doorHeight, paint)
        canvas.restore()

        // Doorway shadow (visible once the door swings away)
        paint.color = Color.parseColor("#33000000")
        canvas.drawRect(doorLeft, doorTop, doorLeft + doorWidth, wallBottom, paint)

        // Cartoon person waving in the doorway
        if (personVisible) {
            val cx = doorLeft + doorWidth / 2
            val cy = wallBottom - doorHeight * 0.4f
            paint.color = Color.parseColor("#FFCC80")
            canvas.drawCircle(cx, cy - 40f, 22f, paint) // head
            paint.color = Color.parseColor("#42A5F5")
            canvas.drawRect(cx - 18f, cy - 20f, cx + 18f, cy + 40f, paint) // body
            // Waving arm
            canvas.save()
            canvas.rotate(armSwing, cx + 18f, cy - 15f)
            paint.color = Color.parseColor("#FFCC80")
            canvas.drawRect(cx + 18f, cy - 15f, cx + 50f, cy - 5f, paint)
            canvas.restore()
        }

        // Optional tree
        if (config.tree != TreeChoice.NONE) {
            val treeX = wallLeft - 90f
            paint.color = Color.parseColor("#6D4C41")
            canvas.drawRect(treeX - 8f, wallBottom - 60f, treeX + 8f, wallBottom, paint)
            paint.color = if (config.tree == TreeChoice.ROUND) Color.parseColor("#43A047") else Color.parseColor("#2E7D32")
            if (config.tree == TreeChoice.ROUND) {
                canvas.drawCircle(treeX, wallBottom - 90f, 45f, paint)
            } else {
                val path = Path().apply {
                    moveTo(treeX - 40f, wallBottom - 55f)
                    lineTo(treeX, wallBottom - 150f)
                    lineTo(treeX + 40f, wallBottom - 55f)
                    close()
                }
                canvas.drawPath(path, paint)
            }
        }

        // Optional fence
        if (config.hasFence) {
            paint.color = Color.parseColor("#D7CCC8")
            var x = wallLeft - 130f
            while (x < wallRight + 130f) {
                canvas.drawRect(x, wallBottom - 40f, x + 10f, wallBottom, paint)
                x += 30f
            }
        }
    }
}
