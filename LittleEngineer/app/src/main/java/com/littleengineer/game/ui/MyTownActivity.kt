package com.littleengineer.game.ui

import android.graphics.Color
import android.os.Bundle
import android.util.TypedValue
import android.view.Gravity
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import android.widget.GridLayout
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.littleengineer.game.R
import com.littleengineer.game.model.BuildingType
import com.littleengineer.game.model.GameProgress
import com.littleengineer.game.util.SoundManager

/**
 * Static "My Town" screen (Step 8): every completed build gets a slot on a
 * shared background. Every 5 completions unlocks a new building type that
 * reuses the same 7-stage loop with different stage art (asset swap only --
 * see GameActivity.buildingType).
 */
class MyTownActivity : AppCompatActivity() {

    private lateinit var soundManager: SoundManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        soundManager = SoundManager(this)
        val density = resources.displayMetrics.density

        val root = FrameLayout(this).apply {
            setBackgroundColor(Color.parseColor("#6FCB6F")) // static "town" backdrop
        }

        val column = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
        }

        val title = TextView(this).apply {
            text = "MY TOWN"
            setTextColor(Color.WHITE)
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 40f)
            gravity = Gravity.CENTER
            setPadding(0, 32, 0, 16)
        }
        column.addView(title)

        val total = GameProgress.totalCompleted(this)
        val unlocked = GameProgress.unlockedBuildingTypes(this)
        val untilNext = GameProgress.buildsUntilNextUnlock(this)

        val subtitle = TextView(this).apply {
            text = if (unlocked.size < BuildingType.entries.size) {
                "$total built · $untilNext more to unlock a new building!"
            } else {
                "$total built · everything unlocked!"
            }
            setTextColor(Color.WHITE)
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 18f)
            gravity = Gravity.CENTER
            setPadding(0, 0, 0, 24)
        }
        column.addView(subtitle)

        val grid = GridLayout(this).apply {
            columnCount = 5
        }
        val slotSize = (100 * density).toInt()
        val slotMargin = (8 * density).toInt()
        val slotCount = maxOf(total, 5).coerceAtMost(20)

        for (i in 0 until slotCount) {
            val slot = TextView(this).apply {
                gravity = Gravity.CENTER
                textSize = 36f
                text = if (i < total) "🏠" else "" // 🏠 filled, blank = empty slot
                setBackgroundColor(if (i < total) Color.parseColor("#FDD835") else Color.parseColor("#33FFFFFF"))
            }
            val lp = GridLayout.LayoutParams(GridLayout.spec(i / 5), GridLayout.spec(i % 5)).apply {
                width = slotSize
                height = slotSize
                setMargins(slotMargin, slotMargin, slotMargin, slotMargin)
            }
            grid.addView(slot, lp)
        }
        column.addView(grid)

        val backButton = Button(this).apply {
            text = getString(R.string.main_menu)
            setBackgroundResource(R.drawable.bg_button_selector)
            setTextColor(Color.WHITE)
            setPadding(48, 24, 48, 24)
            setOnClickListener { finish() }
        }
        column.addView(backButton, LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
        ).apply { topMargin = (24 * density).toInt() })

        root.addView(column, FrameLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT
        ).apply { gravity = Gravity.CENTER })

        setContentView(root)
        root.post { backButton.requestFocus() }
    }

    override fun onResume() {
        super.onResume()
        soundManager.startBackgroundMusic()
    }

    override fun onPause() {
        soundManager.pauseBackgroundMusic()
        super.onPause()
    }

    override fun onDestroy() {
        soundManager.release()
        super.onDestroy()
    }
}
