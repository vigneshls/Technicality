package com.littleengineer.game.ui.stages

import android.content.Context
import android.graphics.Color
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.GridLayout
import android.widget.LinearLayout
import android.widget.TextView
import com.littleengineer.game.model.TileState
import com.littleengineer.game.ui.TileCellView
import com.littleengineer.game.util.GridFocusHelper
import com.littleengineer.game.util.InputDebouncer

/**
 * Shared mechanics for the three grid-based stages: Survey (clear obstacles),
 * Dig (foundation) and Pour (concrete). Each is "move a cursor over a grid,
 * press Select to advance one tile's state, done when every target tile is
 * converted" -- only the initial/target states, colors and cursor icon differ.
 */
abstract class GridStageView(
    context: Context,
    private val rows: Int,
    private val cols: Int,
    private val cursorIcon: String,
    titleText: String,
    private val onComplete: () -> Unit,
    private val onAction: () -> Unit = {}
) : FrameLayout(context) {

    /** The tile's starting visual state. */
    protected abstract fun initialState(r: Int, c: Int): TileState

    /** The state this tile must reach for the stage to progress; null = not part of this stage. */
    protected abstract fun targetState(r: Int, c: Int): TileState?

    protected open fun colorFor(state: TileState): Int = when (state) {
        TileState.OBSTACLE -> Color.parseColor("#6B4423")
        TileState.CLEARED -> Color.parseColor("#8FD98F")
        TileState.MARKED -> Color.parseColor("#E0D6C3")
        TileState.DUG -> Color.parseColor("#8B5A2B")
        TileState.CONCRETE -> Color.parseColor("#9E9E9E")
    }

    private val debouncer = InputDebouncer()
    private var remaining = 0
    private val cells = Array(rows) { arrayOfNulls<TileCellView>(cols) }
    private val states = Array(rows) { r -> Array(cols) { c -> initialState(r, c) } }

    init {
        val root = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
        }

        val title = TextView(context).apply {
            text = titleText
            setTextColor(Color.WHITE)
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 32f)
            gravity = Gravity.CENTER
            setPadding(0, 24, 0, 24)
        }
        root.addView(title)

        val grid = GridLayout(context).apply {
            rowCount = rows
            columnCount = cols
        }

        val cellRefs = mutableListOf<MutableList<View>>()
        val cellSizePx = (140 * resources.displayMetrics.density).toInt()
        val marginPx = (8 * resources.displayMetrics.density).toInt()

        for (r in 0 until rows) {
            val rowViews = mutableListOf<View>()
            for (c in 0 until cols) {
                val cell = TileCellView(context)
                cell.setFillColor(colorFor(states[r][c]))
                val target = targetState(r, c)
                cell.isClickable = target != null

                val lp = GridLayout.LayoutParams(
                    GridLayout.spec(r), GridLayout.spec(c)
                ).apply {
                    width = cellSizePx
                    height = cellSizePx
                    setMargins(marginPx, marginPx, marginPx, marginPx)
                }
                grid.addView(cell, lp)

                if (target != null) {
                    remaining++
                    cell.setOnClickListener {
                        if (!debouncer.allow(SELECT_KEY)) return@setOnClickListener
                        onTileSelected(r, c, cell, target)
                    }
                }
                cells[r][c] = cell
                rowViews.add(cell)
            }
            cellRefs.add(rowViews)
        }

        root.addView(grid, LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        ).apply { gravity = Gravity.CENTER })

        addView(root, LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        ))

        GridFocusHelper.wireGridFocus(cellRefs)

        // Focus the first interactive tile so the player can start moving immediately.
        findFirstTarget()?.let { (r, c) -> cells[r][c]?.focusIcon = cursorIcon }
        post { findFirstTarget()?.let { (r, c) -> cells[r][c]?.requestFocus() } }

        // Give every cell the cursor icon definition; TileCellView only draws it while focused.
        for (r in 0 until rows) for (c in 0 until cols) {
            cells[r][c]?.setOnFocusChangeListener { v, hasFocus ->
                (v as TileCellView).focusIcon = if (hasFocus) cursorIcon else null
            }
        }

        if (remaining == 0) {
            // Defensive: a stage with no target tiles completes immediately.
            post { onComplete() }
        }
    }

    private fun findFirstTarget(): Pair<Int, Int>? {
        for (r in 0 until rows) for (c in 0 until cols) {
            if (targetState(r, c) != null && states[r][c] != targetState(r, c)) return r to c
        }
        return null
    }

    private fun onTileSelected(r: Int, c: Int, cell: TileCellView, target: TileState) {
        if (states[r][c] == target) return // already done, ignore extra presses
        states[r][c] = target
        cell.animateToColor(colorFor(target))
        cell.isClickable = false
        onAction()
        remaining--
        if (remaining <= 0) {
            postDelayed({ onComplete() }, 350)
        }
    }

    companion object {
        private const val SELECT_KEY = 1
    }
}
