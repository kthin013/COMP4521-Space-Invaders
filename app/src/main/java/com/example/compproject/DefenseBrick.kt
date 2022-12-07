package com.example.compproject

import android.graphics.RectF


class DefenseBrick(row: Int, column: Int, shelterNumber: Int, screenX: Int, screenY: Int) {

    var isVisible = true

    private val width = screenX / 180
    private val height = screenY / 80

    // The number of shelters
    private val shelterPadding = screenX / 12f
    private val startHeight = screenY - screenY / 10f * 2f

    val position = RectF(
        column * width +
                shelterPadding * shelterNumber +
                shelterPadding + shelterPadding * shelterNumber,
        row * height + startHeight,
        column * width + width +
                shelterPadding * shelterNumber +
                shelterPadding + shelterPadding * shelterNumber,
        row * height + height + startHeight)
}