package com.littleengineer.game.ui.stages

import android.content.Context
import android.graphics.Color
import android.util.TypedValue
import android.view.Gravity
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import com.littleengineer.game.R
import com.littleengineer.game.model.HouseConfig
import com.littleengineer.game.util.GridFocusHelper
import com.littleengineer.game.util.InputDebouncer

/** Stage 7: the finished house animates in, then offers Build Another / Main Menu. */
class RevealStageView(
    context: Context,
    houseConfig: HouseConfig,
    onBuildAnother: () -> Unit,
    onMainMenu: () -> Unit
) : FrameLayout(context) {

    private val houseView = HouseRevealView(context, houseConfig)

    init {
        val density = resources.displayMetrics.density
        val debouncer = InputDebouncer()

        val root = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
        }

        val title = TextView(context).apply {
            text = context.getString(R.string.great_job)
            setTextColor(Color.WHITE)
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 40f)
            gravity = Gravity.CENTER
            setPadding(0, 24, 0, 8)
        }
        root.addView(title)

        houseView.layoutParams = LinearLayout.LayoutParams(
            (600 * density).toInt(), (360 * density).toInt()
        )
        root.addView(houseView)

        val buttonRow = LinearLayout(context).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER
            setPadding(0, (24 * density).toInt(), 0, 0)
        }

        val buildAnotherBtn = Button(context).apply {
            text = context.getString(R.string.build_another)
            setBackgroundResource(R.drawable.bg_button_selector)
            setTextColor(Color.WHITE)
            setPadding(48, 24, 48, 24)
            setOnClickListener {
                if (debouncer.allow(1)) onBuildAnother()
            }
        }
        val mainMenuBtn = Button(context).apply {
            text = context.getString(R.string.main_menu)
            setBackgroundResource(R.drawable.bg_button_selector)
            setTextColor(Color.WHITE)
            setPadding(48, 24, 48, 24)
            setOnClickListener {
                if (debouncer.allow(2)) onMainMenu()
            }
        }

        val margin = (16 * density).toInt()
        buttonRow.addView(buildAnotherBtn, LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
        ).apply { setMargins(margin, margin, margin, margin) })
        buttonRow.addView(mainMenuBtn, LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
        ).apply { setMargins(margin, margin, margin, margin) })

        root.addView(buttonRow)
        addView(root, LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT))

        GridFocusHelper.wireHorizontalChain(listOf(buildAnotherBtn, mainMenuBtn))
        post { buildAnotherBtn.requestFocus() } // pre-focused per spec
    }

    fun startCelebration(onCelebrationSound: () -> Unit) {
        houseView.playRevealSequence(onCelebrationSound)
    }

    fun stop() {
        houseView.stopAnimations()
    }
}
