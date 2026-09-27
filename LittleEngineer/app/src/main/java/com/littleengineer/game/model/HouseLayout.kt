package com.littleengineer.game.model

/** Fixed layout shared by Survey/Dig/Pour so the same footprint carries through all three stages. */
object HouseLayout {
    const val GRID_ROWS = 3
    const val GRID_COLS = 3

    /** Obstacle tiles scattered on the plot for Stage 1 (Survey). */
    val OBSTACLES: List<Pair<Int, Int>> = listOf(0 to 0, 0 to 2, 1 to 1, 2 to 0, 2 to 2)

    /** The house footprint: top two rows of the 3x3 plot, dug/poured/built on in Stages 2-4. */
    val FOOTPRINT: List<Pair<Int, Int>> = listOf(
        0 to 0, 0 to 1, 0 to 2,
        1 to 0, 1 to 1, 1 to 2
    )

    const val WALL_ROW_COUNT = 3
}
