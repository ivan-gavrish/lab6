package com.example.lab3.ui.gamescreen

object Colors {
    // map of colors for the game
    val colors = mapOf(
        "#FF0000" to "Red",
        "#FFA500" to "Orange",
        "#FFD700" to "Yellow",
        "#008000" to "Green",
        "#000080" to "Navy",
        "#0000FF" to "Blue",
        "#800080" to "Purple",
        "#FFC0CB" to "Pink",
        "#808080" to "Gray",
        "#A52A2A" to "Brown"
    )

    /**
     * Get color names
     *
     * @return a list of color names
     */
    fun colorNames() = colors.values.toList()

    /**
     * Get color values
     *
     * @return a list of color values in hexadecimal format
     */
    fun colorValues() = colors.keys.toList()
}