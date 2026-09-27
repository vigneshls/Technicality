package com.littleengineer.game.ui.stages

import android.content.Context
import com.littleengineer.game.model.HouseLayout
import com.littleengineer.game.model.TileState

/** Stage 2: dig out the marked house footprint. */
class DigStageView(
    context: Context,
    titleText: String,
    onComplete: () -> Unit,
    onAction: () -> Unit = {}
) : GridStageView(
    context,
    HouseLayout.GRID_ROWS,
    HouseLayout.GRID_COLS,
    cursorIcon = "🚧", // placeholder excavator cursor
    titleText = titleText,
    onComplete = onComplete,
    onAction = onAction
) {
    override fun initialState(r: Int, c: Int): TileState =
        if (HouseLayout.FOOTPRINT.contains(r to c)) TileState.MARKED else TileState.CLEARED

    override fun targetState(r: Int, c: Int): TileState? =
        if (HouseLayout.FOOTPRINT.contains(r to c)) TileState.DUG else null
}
