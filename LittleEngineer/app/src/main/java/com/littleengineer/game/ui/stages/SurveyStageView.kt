package com.littleengineer.game.ui.stages

import android.content.Context
import com.littleengineer.game.model.HouseLayout
import com.littleengineer.game.model.TileState

/** Stage 1: bulldoze the obstacle tiles off the plot. */
class SurveyStageView(
    context: Context,
    titleText: String,
    onComplete: () -> Unit,
    onAction: () -> Unit = {}
) : GridStageView(
    context,
    HouseLayout.GRID_ROWS,
    HouseLayout.GRID_COLS,
    cursorIcon = "🚧", // 🚧 placeholder bulldozer cursor (swap for real sprite in Step 7)
    titleText = titleText,
    onComplete = onComplete,
    onAction = onAction
) {
    override fun initialState(r: Int, c: Int): TileState =
        if (HouseLayout.OBSTACLES.contains(r to c)) TileState.OBSTACLE else TileState.CLEARED

    override fun targetState(r: Int, c: Int): TileState? =
        if (HouseLayout.OBSTACLES.contains(r to c)) TileState.CLEARED else null
}
