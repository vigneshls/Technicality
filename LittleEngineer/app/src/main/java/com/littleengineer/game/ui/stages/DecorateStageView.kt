package com.littleengineer.game.ui.stages

import android.content.Context
import android.graphics.Color
import android.view.ViewGroup
import android.widget.FrameLayout
import com.littleengineer.game.model.TreeChoice

/**
 * Stage 6: three quick single-choice picks in sequence -- paint color, tree,
 * fence -- each just a [PickerStageView] under the hood.
 */
class DecorateStageView(
    private val context: Context,
    private val onComplete: (paintColor: Int, tree: TreeChoice, hasFence: Boolean) -> Unit,
    private val onAction: () -> Unit = {}
) : FrameLayout(context) {

    private var paintColor: Int = Color.parseColor("#FDD835")
    private var tree: TreeChoice = TreeChoice.NONE

    init {
        showPaintPicker()
    }

    private fun swapTo(view: FrameLayout) {
        removeAllViews()
        addView(view, LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT))
    }

    private fun showPaintPicker() {
        val options = listOf(
            PickerOption(Color.parseColor("#E53935"), "Red", Color.parseColor("#E53935")),
            PickerOption(Color.parseColor("#FDD835"), "Yellow", Color.parseColor("#FDD835")),
            PickerOption(Color.parseColor("#1E88E5"), "Blue", Color.parseColor("#1E88E5")),
            PickerOption(Color.parseColor("#8E24AA"), "Purple", Color.parseColor("#8E24AA"))
        )
        swapTo(PickerStageView(context, "PICK A PAINT COLOR!", options) { picked ->
            onAction()
            paintColor = picked
            showTreePicker()
        })
    }

    private fun showTreePicker() {
        val options = listOf(
            PickerOption(TreeChoice.NONE, "No Tree", Color.parseColor("#9E9E9E"), "✖"),
            PickerOption(TreeChoice.ROUND, "Round Tree", Color.parseColor("#43A047"), "🌳"),
            PickerOption(TreeChoice.TALL, "Tall Tree", Color.parseColor("#2E7D32"), "🌲")
        )
        swapTo(PickerStageView(context, "ADD A TREE?", options) { picked ->
            onAction()
            tree = picked
            showFencePicker()
        })
    }

    private fun showFencePicker() {
        val options = listOf(
            PickerOption(false, "No Fence", Color.parseColor("#9E9E9E"), "✖"),
            PickerOption(true, "Add Fence", Color.parseColor("#D7CCC8"), "🏡")
        )
        swapTo(PickerStageView(context, "ADD A FENCE?", options) { picked ->
            onAction()
            onComplete(paintColor, tree, picked)
        })
    }
}
