package com.littleengineer.game.util

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.SoundPool
import com.littleengineer.game.R

/**
 * Thin wrapper around SoundPool for short SFX and a MediaPlayer for looping
 * background music. All lookups are resource-name based and fail silently
 * (no crash, no exception) when a sound file hasn't been added yet, since
 * final audio assets are swapped in during Step 7 of the build order and the
 * game must remain fully understandable with sound off regardless.
 */
class SoundManager(private val context: Context) {

    private val soundPool: SoundPool = SoundPool.Builder()
        .setMaxStreams(4)
        .setAudioAttributes(
            AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build()
        )
        .build()

    private val loadedSounds = HashMap<String, Int>()
    private var musicPlayer: MediaPlayer? = null

    private fun rawResId(name: String): Int =
        context.resources.getIdentifier(name, "raw", context.packageName)

    private fun soundIdFor(name: String): Int? {
        loadedSounds[name]?.let { return it }
        val resId = rawResId(name)
        if (resId == 0) return null
        return try {
            val id = soundPool.load(context, resId, 1)
            loadedSounds[name] = id
            id
        } catch (t: Throwable) {
            null
        }
    }

    private fun play(name: String) {
        val id = soundIdFor(name) ?: return
        try {
            soundPool.play(id, 1f, 1f, 1, 0, 1f)
        } catch (t: Throwable) {
            // Never let audio failures interrupt gameplay.
        }
    }

    fun playClearObstacle() = play("sfx_clear")
    fun playDig() = play("sfx_dig")
    fun playPour() = play("sfx_pour")
    fun playBrick() = play("sfx_brick")
    fun playRoof() = play("sfx_roof")
    fun playDecorate() = play("sfx_decorate")
    fun playFanfare() = play("sfx_fanfare")

    fun startBackgroundMusic() {
        if (musicPlayer != null) return
        val resId = rawResId("music_loop")
        if (resId == 0) return
        try {
            musicPlayer = MediaPlayer.create(context, resId)?.apply {
                isLooping = true
                setVolume(0.5f, 0.5f)
                start()
            }
        } catch (t: Throwable) {
            musicPlayer = null
        }
    }

    fun stopBackgroundMusic() {
        musicPlayer?.let {
            try { it.stop(); it.release() } catch (t: Throwable) { /* ignore */ }
        }
        musicPlayer = null
    }

    fun release() {
        stopBackgroundMusic()
        soundPool.release()
    }
}
