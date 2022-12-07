package com.example.compproject

import android.content.Context
import android.graphics.Bitmap
import android.graphics.RectF
import java.util.*
import android.graphics.BitmapFactory
import android.util.Log
import com.example.compproject.PlayerShip.Companion.left
import com.example.compproject.R

class Invader(context: Context, row: Int, column: Int, screenX: Int, screenY: Int) {
    var width = screenX * 0.08f
    private var height = screenY * 0.04f
    private val padding = screenX * 0.04f

    var position = RectF(
            column * (width + padding),
            (screenY * 0.05f) + row * (width + padding + padding / 4),
            column * (width + padding) + width,
            (screenY * 0.05f) + row * (width + padding + padding / 4) + height
    )

    private var speed = 40f

    private val left = 1
    private val right = 2

    private var shipMoving = right

    var isVisible = true

    companion object {
        lateinit var bitmap1: Bitmap
        lateinit var bitmap2: Bitmap

        var numberOfInvaders = 0
    }

    init {
        bitmap1 = BitmapFactory.decodeResource(context.resources, R.drawable.invader1)
        bitmap2 = BitmapFactory.decodeResource(context.resources, R.drawable.invader2)

        bitmap1 = Bitmap.createScaledBitmap(bitmap1, (width.toInt()), (height.toInt()),false)
        bitmap2 = Bitmap.createScaledBitmap(bitmap2, (width.toInt()), (height.toInt()),false)

        numberOfInvaders ++
    }

    fun update(fps: Long) {
        if (shipMoving == left) {
            position.left -= speed / fps
        }

        if (shipMoving == right) {
            position.left += speed / fps
        }

        position.right = position.left + width
    }

    fun dropDownAndReverse(waveNumber: Int) {
        shipMoving = if (shipMoving == left) {
            right
        } else {
            left
        }

        position.top += height
        position.bottom += height

        speed *=  (1.1f + (waveNumber.toFloat() / 20))
    }

    fun takeAim(playerShipX: Float, playerShipLength: Float, waves: Int): Boolean {
        val generator = Random()
        var randomNumber: Int

        // If near the player consider taking a shot
        if (playerShipX + playerShipLength > position.left &&
                playerShipX + playerShipLength < position.left + width ||
                playerShipX > position.left && playerShipX < position.left + width) {

            // The fewer invaders the more each invader shoots
            // The higher the wave the more the invader shoots
            randomNumber = generator.nextInt((100 * numberOfInvaders) / waves)
            if (randomNumber == 0) {
                return true
            }

        }

        randomNumber = generator.nextInt(150 * numberOfInvaders)
        return randomNumber == 0

    }
}
