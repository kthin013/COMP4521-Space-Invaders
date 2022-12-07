package com.example.compproject

import android.content.Context
import android.graphics.Bitmap
import android.graphics.RectF
import android.graphics.BitmapFactory
import com.example.compproject.R

class PlayerShip(context: Context, private val screenX: Int, private val screenY: Int) {

    var bitmap: Bitmap = BitmapFactory.decodeResource(context.resources, R.drawable.playership)

    val width = screenX / 9f
    val height = screenY / 25f

    val position = RectF((screenX / 2f - (width / 2f)), screenY - height - 100, screenX/2 + width, screenY.toFloat())

    var speed  = 300f

    companion object {
        const val stopped = 0
        const val left = 1
        const val right = 2
    }

    var moving = stopped

    init{
        bitmap = Bitmap.createScaledBitmap(bitmap, width.toInt() , height.toInt() ,false)
    }

    fun update(fps: Long) {
        if (moving == left && position.left > 0) {
            position.left -= speed / fps
        } else if (moving == right && position.left < screenX - width) {
            position.left += speed / fps
        }

        position.right = position.left + width
    }
}