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
import com.littleengineer.game.ui.TileCellView
import com.littleengineer.game.util.GridFocusHelper
import com.littleengineer.game.util.InputDebouncer

/** One selectable choice in a [PickerStageView]: e.g. a roof shape, a paint color, yes/no. */
data class PickerOption<T>(val value: T, val label: String, val color: Int, val icon: String? = null)

/**
 * Generic "browse with left/right, confirm with Select" screen. Reused for
 * Stage 5 (roof shape) and every quick pick in Stage 6 (paint, tree, fence).
 */
class PickerStageView<T>(
    context: Context,
    titleText: String,
    options: List<PickerOption<T>>,
    private val onPicked: (T) -> Unit
) : FrameLayout(context) {

    private val debouncer = InputDebouncer()

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
            setPadding(0, 24, 0, 32)
        }
        root.addView(title)

        val row = LinearLayout(context).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER
        }

        val choiceViews = mutableListOf<View>()
        val cellSize = (150 * density).toInt()
        val margin = (16 * density).toInt()

        for (option in options) {
            val column = LinearLayout(context).apply {
                orientation = LinearLayout.VERTICAL
                gravity = Gravity.CENTER
            }
            val box = TileCellView(context)
            box.setFillColor(option.color)
            box.isClickable = true
            box.layoutParams = LinearLayout.LayoutParams(cellSize, cellSize).apply {
                setMargins(margin, margin, margin, margin)
            }
            box.setOnFocusChangeListener { v, hasFocus ->
                (v as TileCellView).focusIcon = if (hasFocus) (option.icon ?: "⭐") else null
                v.scaleX = if (hasFocus) 1.12f else 1f
                v.scaleY = if (hasFocus) 1.12f else 1f
            }
            box.setOnClickListener {
                if (!debouncer.allow(0)) return@setOnClickListener
                onPicked(option.value)
            }
            val label = TextView(context).apply {
                text = option.label
                setTextColor(Color.WHITE)
                setTextSize(TypedValue.COMPLEX_UNIT_SP, 18f)
                gravity = Gravity.CENTER
                setPadding(0, 8, 0, 0)
            }
            column.addView(box)
            column.addView(label)
            row.addView(column)
            choiceViews.add(box)
        }

        root.addView(row)
        addView(root, LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT))

        GridFocusHelper.wireHorizontalChain(choiceViews)
        post { choiceViews.first().requestFocus() }
    }
}
