package com.littleengineer.game.util

import android.os.SystemClock

/**
 * Prevents a single remote-control press from registering twice when a
 * young child mashes the D-pad or holds a key down. Any given "action key"
 * (e.g. DPAD_UP, or the game's Select action) is only allowed to fire once
 * per [windowMs] window.
 */
class InputDebouncer(private val windowMs: Long = 180L) {

    private val lastFireTime = HashMap<Int, Long>()

    /** Returns true if [key] is allowed to fire now (and records that it did). */
    fun allow(key: Int): Boolean {
        val now = SystemClock.elapsedRealtime()
        val last = lastFireTime[key] ?: 0L
        if (now - last < windowMs) return false
        lastFireTime[key] = now
        return true
    }

    fun reset() {
        lastFireTime.clear()
    }
}
