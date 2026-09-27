package com.littleengineer.game.model

import android.content.Context

/**
 * Persists how many houses/buildings the player has completed, used by the
 * "My Town" screen and to unlock a new building type every 5 completions.
 */
object GameProgress {
    private const val PREFS = "little_engineer_progress"
    private const val KEY_HOUSES_BUILT = "houses_built"
    private const val UNLOCK_EVERY = 5

    fun recordCompletedBuild(context: Context, type: BuildingType) {
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        val key = "count_${type.name}"
        val newCount = prefs.getInt(key, 0) + 1
        prefs.edit()
            .putInt(key, newCount)
            .putInt(KEY_HOUSES_BUILT, prefs.getInt(KEY_HOUSES_BUILT, 0) + 1)
            .apply()
    }

    fun totalCompleted(context: Context): Int =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getInt(KEY_HOUSES_BUILT, 0)

    fun countFor(context: Context, type: BuildingType): Int =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getInt("count_${type.name}", 0)

    /** Building types unlocked so far: HOUSE is always available; one more unlocks every 5 builds. */
    fun unlockedBuildingTypes(context: Context): List<BuildingType> {
        val total = totalCompleted(context)
        val extraUnlocks = total / UNLOCK_EVERY
        val extras = BuildingType.entries.filter { it != BuildingType.HOUSE }
        return listOf(BuildingType.HOUSE) + extras.take(extraUnlocks)
    }

    fun buildsUntilNextUnlock(context: Context): Int {
        val total = totalCompleted(context)
        val remainder = total % UNLOCK_EVERY
        return if (remainder == 0 && total > 0) UNLOCK_EVERY else UNLOCK_EVERY - remainder
    }
}
