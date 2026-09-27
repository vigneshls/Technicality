package com.littleengineer.game.ui

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.TypedValue
import android.view.Gravity
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.littleengineer.game.R
import com.littleengineer.game.util.GridFocusHelper

/**
 * Landing screen (the Leanback launcher entry point). Two big D-pad
 * navigable buttons: start a new build, or view completed houses.
 */
class MainMenuActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val density = resources.displayMetrics.density
        val root = FrameLayout(this).apply {
            setBackgroundColor(Color.parseColor("#7EC8E3"))
        }

        val column = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
        }

        val title = TextView(this).apply {
            text = getString(R.string.app_name)
            setTextColor(Color.WHITE)
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 48f)
            gravity = Gravity.CENTER
            setPadding(0, 0, 0, 64)
        }
        column.addView(title)

        val playButton = Button(this).apply {
            text = getString(R.string.menu_play)
            setBackgroundResource(R.drawable.bg_button_selector)
            setTextColor(Color.WHITE)
            textSize = 24f
            setPadding(64, 32, 64, 32)
            setOnClickListener {
                startActivity(Intent(this@MainMenuActivity, GameActivity::class.java))
            }
        }
        val townButton = Button(this).apply {
            text = getString(R.string.menu_town)
            setBackgroundResource(R.drawable.bg_button_selector)
            setTextColor(Color.WHITE)
            textSize = 24f
            setPadding(64, 32, 64, 32)
            setOnClickListener {
                startActivity(Intent(this@MainMenuActivity, MyTownActivity::class.java))
            }
        }

        val margin = (16 * density).toInt()
        column.addView(playButton, LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
        ).apply { setMargins(margin, margin, margin, margin) })
        column.addView(townButton, LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
        ).apply { setMargins(margin, margin, margin, margin) })

        root.addView(column, FrameLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT
        ).apply { gravity = Gravity.CENTER })

        setContentView(root)

        GridFocusHelper.wireVerticalChain(listOf(playButton, townButton))
        root.post { playButton.requestFocus() }
    }
}
