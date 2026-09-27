package com.littleengineer.game.ui

import android.graphics.Color
import android.os.Bundle
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import com.littleengineer.game.R
import com.littleengineer.game.model.BuildingType
import com.littleengineer.game.model.GameProgress
import com.littleengineer.game.model.HouseConfig
import com.littleengineer.game.ui.stages.BricksStageView
import com.littleengineer.game.ui.stages.DecorateStageView
import com.littleengineer.game.ui.stages.DigStageView
import com.littleengineer.game.ui.stages.PourStageView
import com.littleengineer.game.ui.stages.RevealStageView
import com.littleengineer.game.ui.stages.RoofStageView
import com.littleengineer.game.ui.stages.SurveyStageView
import com.littleengineer.game.util.SoundManager

/**
 * Hosts the full 7-stage build loop as a simple linear state machine:
 * Survey -> Dig -> Pour -> Bricks -> Roof -> Decorate -> Reveal.
 *
 * Each stage is a self-contained view that calls its completion lambda when
 * the child has finished the stage's single decision loop; this activity's
 * only job is to swap the next stage view into [container] and accumulate
 * the player's choices into [houseConfig].
 *
 * NOTE: today this always builds a HOUSE (Step 8's second building type is
 * layered on top of the same 7-stage loop via [BuildingType], not yet wired
 * into stage-specific art -- see MyTownActivity / GameProgress).
 */
class GameActivity : AppCompatActivity() {

    private lateinit var container: FrameLayout
    private lateinit var soundManager: SoundManager
    private var houseConfig = HouseConfig()
    private var buildingType = BuildingType.HOUSE

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        soundManager = SoundManager(this)
        // No-op until a music_loop raw resource is added in Step 7 (final audio pass).
        soundManager.startBackgroundMusic()

        container = FrameLayout(this).apply {
            setBackgroundColor(Color.parseColor("#4FA8D8"))
        }
        setContentView(container)

        startNewBuild()
    }

    private fun startNewBuild() {
        houseConfig = HouseConfig()
        showSurvey()
    }

    private fun showSurvey() {
        val view = SurveyStageView(
            this, getString(R.string.stage_survey_title),
            onComplete = { showDig() },
            onAction = { soundManager.playClearObstacle() }
        )
        container.removeAllViews()
        container.addView(view)
    }

    private fun showDig() {
        val view = DigStageView(
            this, getString(R.string.stage_dig_title),
            onComplete = { showPour() },
            onAction = { soundManager.playDig() }
        )
        container.removeAllViews()
        container.addView(view)
    }

    private fun showPour() {
        val view = PourStageView(
            this, getString(R.string.stage_pour_title),
            onComplete = { showBricks() },
            onAction = { soundManager.playPour() }
        )
        container.removeAllViews()
        container.addView(view)
    }

    private fun showBricks() {
        val view = BricksStageView(
            this, getString(R.string.stage_bricks_title),
            onComplete = { wallRows ->
                houseConfig.wallRows = wallRows.toMutableList()
                showRoof()
            },
            onAction = { soundManager.playBrick() }
        )
        container.removeAllViews()
        container.addView(view)
    }

    private fun showRoof() {
        val view = RoofStageView(
            this, getString(R.string.stage_roof_title),
            onComplete = { roof ->
                houseConfig.roof = roof
                showDecorate()
            },
            onAction = { soundManager.playRoof() }
        )
        container.removeAllViews()
        container.addView(view)
    }

    private fun showDecorate() {
        val view = DecorateStageView(
            this,
            onComplete = { paintColor, tree, hasFence ->
                houseConfig.paintColor = paintColor
                houseConfig.tree = tree
                houseConfig.hasFence = hasFence
                showReveal()
            },
            onAction = { soundManager.playDecorate() }
        )
        container.removeAllViews()
        container.addView(view)
    }

    private var revealView: RevealStageView? = null

    private fun showReveal() {
        GameProgress.recordCompletedBuild(this, buildingType)

        val view = RevealStageView(
            this,
            houseConfig,
            onBuildAnother = { startNewBuild() },
            onMainMenu = {
                finish()
            }
        )
        revealView = view
        container.removeAllViews()
        container.addView(view)
        view.startCelebration { soundManager.playFanfare() }
    }

    override fun onDestroy() {
        revealView?.stop()
        soundManager.release()
        super.onDestroy()
    }
}
