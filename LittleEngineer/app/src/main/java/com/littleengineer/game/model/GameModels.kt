package com.littleengineer.game.model

import android.graphics.Color

/** State of a single tile as it moves through Survey -> Dig -> Pour. */
enum class TileState {
    OBSTACLE,   // Stage 1: rock/bush blocking the plot
    CLEARED,    // Stage 1 done: plain empty ground
    MARKED,     // Stage 2: outline of the house footprint, not yet dug
    DUG,        // Stage 2 done: brown dug earth
    CONCRETE    // Stage 3 done: solid foundation
}

enum class BrickColor(val colorInt: Int) {
    RED(Color.parseColor("#E53935")),
    YELLOW(Color.parseColor("#FDD835")),
    BLUE(Color.parseColor("#1E88E5"))
}

enum class RoofType(val label: String) {
    POINTED("Pointed"),
    DOME("Dome"),
    FLAT_CHIMNEY("Flat + Chimney")
}

enum class TreeChoice { NONE, ROUND, TALL }

/** Everything the player chose while building one house, used to render Stage 7 (Reveal). */
data class HouseConfig(
    var wallRows: MutableList<BrickColor> = mutableListOf(),
    var roof: RoofType = RoofType.POINTED,
    var paintColor: Int = Color.parseColor("#FDD835"),
    var tree: TreeChoice = TreeChoice.NONE,
    var hasFence: Boolean = false
)

/** Which kind of structure the current 7-stage loop is building. */
enum class BuildingType(val displayName: String) {
    HOUSE("House"),
    BRIDGE("Bridge"),
    TOWER("Tower")
}
