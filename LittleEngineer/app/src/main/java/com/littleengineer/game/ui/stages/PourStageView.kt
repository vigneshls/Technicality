package com.littleengineer.game.ui.stages

import android.content.Context
import com.littleengineer.game.model.HouseLayout
import com.littleengineer.game.model.TileState

/** Stage 3: pour concrete over the dug foundation, row by row (D-pad movement is naturally row-by-row). */
class PourStageView(
    context: Context,
    titleText: String,
    onComplete: () -> Unit,
    onAction: () -> Unit = {}
) : GridStageView(
    context,
    HouseLayout.GRID_ROWS,
    HouseLayout.GRID_COLS,
    cursorIcon = "🚛", // 🚛 placeholder cement mixer truck cursor
    titleText = titleText,
    onComplete = onComplete,
    onAction = onAction
) {
    override fun initialState(r: Int, c: Int): TileState =
        if (HouseLayout.FOOTPRINT.contains(r to c)) TileState.DUG else TileState.CLEARED

    override fun targetState(r: Int, c: Int): TileState? =
        if (HouseLayout.FOOTPRINT.contains(r to c)) TileState.CONCRETE else null
}
