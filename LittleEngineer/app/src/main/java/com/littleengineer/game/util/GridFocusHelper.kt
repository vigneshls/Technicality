package com.littleengineer.game.util

import android.view.View

/**
 * Wires deterministic 4-directional D-pad focus navigation across a rectangular
 * grid of views, independent of Android's default (sometimes flaky) 2D focus
 * search heuristics.
 *
 * At the edges of the grid, a direction is wired back to the cell itself so
 * that pressing D-pad against a boundary simply holds focus in place instead
 * of jumping to an unrelated view elsewhere on screen -- important for a
 * 5-6 year old mashing the remote.
 */
object GridFocusHelper {

    fun wireGridFocus(grid: List<List<View>>) {
        val rows = grid.size
        for (r in 0 until rows) {
            for (c in grid[r].indices) {
                val cell = grid[r][c]
                if (cell.id == View.NO_ID) {
                    cell.id = View.generateViewId()
                }
            }
        }
        for (r in 0 until rows) {
            val cols = grid[r].size
            for (c in 0 until cols) {
                val cell = grid[r][c]
                val up = if (r > 0) grid[r - 1].getOrNull(c) else null
                val down = if (r < rows - 1) grid[r + 1].getOrNull(c) else null
                val left = if (c > 0) grid[r][c - 1] else null
                val right = if (c < cols - 1) grid[r][c + 1] else null

                cell.nextFocusUpId = (up ?: cell).id
                cell.nextFocusDownId = (down ?: cell).id
                cell.nextFocusLeftId = (left ?: cell).id
                cell.nextFocusRightId = (right ?: cell).id

                cell.isFocusable = true
                cell.isFocusableInTouchMode = false
            }
        }
    }

    /** Wires a simple vertical chain (e.g. a stack of menu buttons). */
    fun wireVerticalChain(views: List<View>) {
        for (v in views) if (v.id == View.NO_ID) v.id = View.generateViewId()
        for (i in views.indices) {
            val v = views[i]
            val up = views.getOrNull(i - 1) ?: v
            val down = views.getOrNull(i + 1) ?: v
            v.nextFocusUpId = up.id
            v.nextFocusDownId = down.id
            v.isFocusable = true
            v.isFocusableInTouchMode = false
        }
    }

    /** Wires a simple horizontal chain (e.g. left/right pickers). */
    fun wireHorizontalChain(views: List<View>) {
        for (v in views) if (v.id == View.NO_ID) v.id = View.generateViewId()
        for (i in views.indices) {
            val v = views[i]
            val left = views.getOrNull(i - 1) ?: v
            val right = views.getOrNull(i + 1) ?: v
            v.nextFocusLeftId = left.id
            v.nextFocusRightId = right.id
            v.isFocusable = true
            v.isFocusableInTouchMode = false
        }
    }
}
