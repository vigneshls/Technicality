package com.littleengineer.game.ui.stages

import android.content.Context
import android.graphics.Color
import android.view.ViewGroup
import android.widget.FrameLayout
import com.littleengineer.game.model.RoofType

/** Stage 5: browse 3 roof shapes left/right, Select to place one. */
class RoofStageView(
    context: Context,
    titleText: String,
    onComplete: (RoofType) -> Unit,
    onAction: () -> Unit = {}
) : FrameLayout(context) {
    init {
        val options = listOf(
            PickerOption(RoofType.POINTED, "Pointed", Color.parseColor("#8E44AD"), "▲"),
            PickerOption(RoofType.DOME, "Dome", Color.parseColor("#7B1FA2"), "●"),
            PickerOption(RoofType.FLAT_CHIMNEY, "Flat + Chimney", Color.parseColor("#6A1B9A"), "▂")
        )
        val picker = PickerStageView(context, titleText, options) { picked ->
            onAction()
            onComplete(picked)
        }
        addView(picker, LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT))
    }
}
