package com.littleengineer.game.ui.stages

import android.content.Context
import android.graphics.Color
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import com.littleengineer.game.model.BrickColor
import com.littleengineer.game.model.HouseLayout
import com.littleengineer.game.ui.TileCellView
import com.littleengineer.game.util.GridFocusHelper
import com.littleengineer.game.util.InputDebouncer

/**
 * Stage 4: pick a brick color with D-pad left/right, press Select to stack one
 * more row onto the rising wall. Repeats until [HouseLayout.WALL_ROW_COUNT]
 * rows are built.
 */
class BricksStageView(
    context: Context,
    titleText: String,
    private val onComplete: (List<BrickColor>) -> Unit,
    private val onAction: () -> Unit = {}
) : FrameLayout(context) {

    private val debouncer = InputDebouncer()
    private val wallRows = mutableListOf<BrickColor>()
    private val wallStack: LinearLayout

    init {
        val density = resources.displayMetrics.density
        val root = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
        }

        val title = TextView(context).apply {
            text = titleText
            setTextColor(Color.WHITE)
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 32f)
            gravity = Gravity.CENTER
            setPadding(0, 24, 0, 16)
        }
        root.addView(title)

        // Rising wall: rows stack bottom-to-top, so we insert each new row at index 0.
        wallStack = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
        }
        val wallContainer = FrameLayout(context).apply {
            layoutParams = LinearLayout.LayoutParams(
                (420 * density).toInt(), (240 * density).toInt()
            )
        }
        wallContainer.addView(wallStack, FrameLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT
        ).apply { gravity = Gravity.BOTTOM })
        root.addView(wallContainer)

        // Palette: D-pad left/right cycles the highlighted brick color.
        val palette = LinearLayout(context).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER
            setPadding(0, (24 * density).toInt(), 0, 0)
        }

        val swatches = mutableListOf<View>()
        val cellSize = (120 * density).toInt()
        val margin = (12 * density).toInt()

        for (brick in BrickColor.entries) {
            val swatch = TileCellView(context)
            swatch.setFillColor(brick.colorInt)
            swatch.isClickable = true
            val lp = LinearLayout.LayoutParams(cellSize, cellSize).apply {
                setMargins(margin, margin, margin, margin)
            }
            swatch.layoutParams = lp
            swatch.setOnFocusChangeListener { v, hasFocus ->
                (v as TileCellView).focusIcon = if (hasFocus) "🧱" else null // 🧱
            }
            swatch.setOnClickListener {
                if (!debouncer.allow(0)) return@setOnClickListener
                placeRow(brick)
            }
            palette.addView(swatch)
            swatches.add(swatch)
        }
        root.addView(palette)

        addView(root, LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT))

        GridFocusHelper.wireHorizontalChain(swatches)
        post { swatches.first().requestFocus() }
    }

    private fun placeRow(color: BrickColor) {
        if (wallRows.size >= HouseLayout.WALL_ROW_COUNT) return
        wallRows.add(color)
        onAction()

        val density = resources.displayMetrics.density
        val row = View(context).apply {
            setBackgroundColor(color.colorInt)
            alpha = 0f
            translationY = 40f
        }
        val rowHeightPx = (60 * density).toInt()
        wallStack.addView(
            row, 0,
            LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, rowHeightPx).apply {
                setMargins(0, 4, 0, 4)
            }
        )
        row.animate().alpha(1f).translationY(0f).setDuration(220).start()

        if (wallRows.size >= HouseLayout.WALL_ROW_COUNT) {
            postDelayed({ onComplete(wallRows.toList()) }, 400)
        }
    }
}
