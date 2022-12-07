package com.example.compproject

import android.content.Context
import android.content.Context.SENSOR_SERVICE
import android.content.SharedPreferences
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.SurfaceView
import androidx.appcompat.app.AppCompatActivity


class SpaceInvaderView(context: Context, private val screenWidth: Int, private val screenHeight: Int ): SurfaceView(context), Runnable, SensorEventListener {
    private val sfxPlayer = SFXPlayer(context)
    private val mediaPlayer: MediaPlayer = MediaPlayer.create(context, R.raw.background)

    private lateinit var gameThread: Thread

    // A boolean which we will set and unset
    private var playing = false
    private var paused = true

    // A Canvas and a Paint object
    private var canvas: Canvas = Canvas()
    private val paint: Paint = Paint()

    // Player Ship Object
    private var playerShip: PlayerShip = PlayerShip(context, screenWidth, screenHeight)

    // Invaders Object
    private val invaders = ArrayList<Invader>()
    private var numInvaders = 0

    // Bricks Object
    val bricks = ArrayList<DefenseBrick>()
    var numBricks: Int = 0

    // Player Bullet Object
    private var playerBullet = Bullet(screenHeight, 1200f, 40f)

    // Invaders bullets Object
    private val invadersBullets = ArrayList<Bullet>()
    private var nextBullet = 0
    private val maxInvaderBullets = 10

    //Sensor
    lateinit var sensorManager: SensorManager
    lateinit var gyro: Sensor
    private var gyroData: SensorData? = null
    private var gyroY:Float = 0f
    private var timeGyro:Long = 0

    // Indicators
    private var score = 0
    private var waves = 1
    private var lives = 3

    // To remember
    private var prefs: SharedPreferences = context.getSharedPreferences("Space Invader", Context.MODE_PRIVATE)
    private var highScore =  prefs.getInt(HIGHSCORE, 0)

    // How menacing should the sound be?
    private var menaceInterval: Long = 1000

    // Which menace sound should play next
    private var upOrDown: Boolean = false
    // When did we last play a menacing sound
    private var lastMenaceTime = System.currentTimeMillis()

    private fun prepareLevel() {
        val width = screenWidth * 0.055f * 2
        val height = screenHeight * 0.04f * 2
        var x = ((screenWidth/width - 7) * 0.2).toInt()
        Log.i("Invader", ((screenWidth/width - 7) * 0.2).toInt().toString())
        Log.i("Invader", ((screenHeight/height - 3) * 0.1).toInt().toString())
        if(x < 0)
            x = 0


        // Invaders initialization based on sreen width
        Invader.numberOfInvaders = 0
        numInvaders = 0

        for (column in 0..5 + x) {
            for (row in 0..3) {
                invaders.add(Invader(context, row, column, screenWidth, screenHeight))
                numInvaders++
            }
        }

        // Bricks initialization
        numBricks = 0
        for (shelterNumber in 0..4) {
            for (column in 0..18) {
                for (row in 0..3) {
                    bricks.add(DefenseBrick(row, column, shelterNumber, screenWidth, screenHeight))
                    numBricks++
                }
            }
        }

        // Invaders Bullets initialization
        for (i in 0 until maxInvaderBullets) {
            invadersBullets.add(Bullet(screenHeight))
        }

        Log.i("Test--thread", "prepareLevel ")
    }

    override fun run() {
        Log.i("Test--thread", "run")

        //Volume initialization based on preferences
        val prefs: SharedPreferences = context.getSharedPreferences("Space Invader", Context.MODE_PRIVATE)
        var volume = prefs.getInt(VOLUME_BGM, 100).toFloat()/100

        Log.i("Test--",volume.toString())
        mediaPlayer.setVolume(1f * volume, 1f * volume)
        mediaPlayer.start()

        var fps: Long = 0
        // Game loop
        while (playing) {
            // Current time
            val startFrameTime = System.currentTimeMillis()

            // Update the game
            if (!paused) {
                update(fps)

                if(!mediaPlayer.isPlaying) {
                    mediaPlayer.start()
                    mediaPlayer.setLooping(true)
                }
            }

            // Update the screen
            draw()

            // Calculate the fps rate this frame
            val timeThisFrame = System.currentTimeMillis() - startFrameTime
            if (timeThisFrame >= 1) {
                fps = 1000 / timeThisFrame
            }

            // Play a sound based on the menace level
            if (!paused && ((startFrameTime - lastMenaceTime) > menaceInterval))
                menacePlayer()
        }
    }

    private fun menacePlayer() {
        if (upOrDown) {
            sfxPlayer.playSound(SFXPlayer.uhID, prefs.getInt(VOLUME_SFX, 100).toFloat())
        } else {
            sfxPlayer.playSound(SFXPlayer.ohID, prefs.getInt(VOLUME_SFX, 100).toFloat())
        }
        // Reset the last menace time
        lastMenaceTime = System.currentTimeMillis()
        upOrDown = !upOrDown
    }

    // Update the state of all the game objects
    private fun update(fps: Long) {
        playerShip.update(fps)

        // Invader need a turn?
        var bumped = false

        // Player lost?
        var lost = false

        // Update all the invaders if visible
        for (invader in invaders) {
            if (invader.isVisible) {
                invader.update(fps)

                // Does he want to take a shot?
                if (invader.takeAim(playerShip.position.left, playerShip.width, waves)) {
                    // If so try and spawn a bullet
                    if (invadersBullets[nextBullet].shoot(invader.position.left + invader.width / 2,
                            invader.position.top, playerBullet.down)) {
                        nextBullet++

                        if (nextBullet == maxInvaderBullets) {
                            nextBullet = 0
                        }
                    }
                }

                // Invaders need to turn
                if (invader.position.left > screenWidth - invader.width || invader.position.left < 0) {
                    bumped = true
                }
            }
        }

        // Update Player Bullet
        if (playerBullet.isActive) {
            playerBullet.update(fps)
        }

        // Update Invaders bullets
        for (bullet in invadersBullets) {
            if (bullet.isActive) {
                bullet.update(fps)
            }
        }

        // Turning the invaders if required
        if (bumped) {
            for (invader in invaders) {
                invader.dropDownAndReverse(waves)

                // Reach the player -> Player lost
                if (invader.position.bottom >= (screenHeight - playerShip.height) && invader.isVisible) {
                    lost = true
                }
            }
        }

        // Recycle Player Bullets
        if (playerBullet.position.bottom < 0) {
            playerBullet.isActive = false
        }

        // Recycle Invaders Bullets
        for (bullet in invadersBullets) {
            if (bullet.position.top > screenHeight) {
                bullet.isActive = false
            }
        }

        // Player Bullet & Invader
        // If Player Bullet shoot
        // Any intersections with Invaders? If so, update and display
        if (playerBullet.isActive) {
            for (invader in invaders) {
                if (invader.isVisible) {
                    if (RectF.intersects(playerBullet.position, invader.position)) {
                        invader.isVisible = false

                        sfxPlayer.playSound(SFXPlayer.invaderExplodeID, prefs.getInt(VOLUME_SFX, 100).toFloat())
                        playerBullet.isActive = false
                        Invader.numberOfInvaders --
                        score += 10
                        if(score > highScore){
                            highScore = score
                        }

                        // Game winning check
                        if (Invader.numberOfInvaders == 0) {
                            paused = true
                            lives ++
                            invaders.clear()
                            bricks.clear()
                            invadersBullets.clear()
                            prepareLevel()
                            waves ++
                            break
                        }

                        break
                    }
                }
            }
        }

        // Player & Invader Bullet
        // Any intersections with Bricks? If so, update and display
        for (bullet in invadersBullets) {
            if (bullet.isActive) {
                for (brick in bricks) {
                    if (brick.isVisible) {
                        if (RectF.intersects(bullet.position, brick.position)) {
                            bullet.isActive = false
                            brick.isVisible = false
                            sfxPlayer.playSound(SFXPlayer.damageShelterID, prefs.getInt(VOLUME_SFX, 100).toFloat())
                        }
                    }
                }
            }

        }

        if (playerBullet.isActive) {
            for (brick in bricks) {
                if (brick.isVisible) {
                    if (RectF.intersects(playerBullet.position, brick.position)) {
                        playerBullet.isActive = false
                        brick.isVisible = false
                        sfxPlayer.playSound(SFXPlayer.damageShelterID, prefs.getInt(VOLUME_SFX, 100).toFloat())
                    }
                }
            }
        }

        // Invader Bullet & Player
        // If Invader Bullet shoot
        // Any intersections with Player? If so, update and display
        for (bullet in invadersBullets) {
            if (bullet.isActive) {
                if (RectF.intersects(playerShip.position, bullet.position)) {
                    bullet.isActive = false
                    lives --
                    sfxPlayer.playSound(SFXPlayer.playerExplodeID, prefs.getInt(VOLUME_SFX, 100).toFloat())

                    // Game over check
                    if (lives == 0) {
                        lost = true
                        break
                    }
                }
            }
        }


        // Game over check
        if (lost) {
            var gameover = gameoverFragment()
            var bundle = Bundle()
            bundle.putInt("score", score)

            paused = true
            lives = 3
            score = 0
            waves = 1
            invaders.clear()
            bricks.clear()
            invadersBullets.clear()

            gameover.arguments = bundle
            gameover.show((context as AppCompatActivity).supportFragmentManager, "gameoverFragment")

            // Game reset
            prepareLevel()
        }
    }


    // Update on the screen
    private fun draw() {
        if (holder.surface.isValid) {
            canvas = holder.lockCanvas()

            canvas.drawColor(Color.argb(255, 0, 0, 0))
            paint.color = Color.argb(255, 0, 255, 0)

            canvas.drawBitmap(playerShip.bitmap, playerShip.position.left, playerShip.position.top, paint)

            for (invader in invaders) {
                if (invader.isVisible) {
                    if (upOrDown) {
                        canvas.drawBitmap(Invader.bitmap1, invader.position.left, invader.position.top, paint)
                    } else {
                        canvas.drawBitmap(Invader.bitmap2, invader.position.left, invader.position.top, paint)
                    }
                }
            }

            paint.color = Color.argb(255, 0, 255, 0)
            for (brick in bricks) {
                if (brick.isVisible) {
                    canvas.drawRect(brick.position, paint)
                }
            }

            paint.color = Color.argb(255, 255, 255, 0)
            if (playerBullet.isActive) {
                canvas.drawRect(playerBullet.position, paint)
            }

            paint.color = Color.argb(255, 255, 255, 0)
            for (bullet in invadersBullets) {
                if (bullet.isActive) {
                    canvas.drawRect(bullet.position, paint)
                }
            }

            paint.color = Color.argb(255, 255, 255, 255)
            paint.textSize = 70f
            canvas.drawText("Score: $score  Wave: $waves  ♥ x $lives", 20f, 75f, paint)


            holder.unlockCanvasAndPost(canvas)
        }
    }

    // Activity onPaused -> paused
    // Kill Thread & save Highest scored
    // Unregister sensorManager & mediaPlayer
    fun pause() {
        playing = false
        try {
            gameThread.join()
            Log.i("Test--Thread", "onPause: ")
        } catch (e: InterruptedException) {
            Log.e("Error:", "joining thread")
        }

        val prefs = context.getSharedPreferences("Space Invader", Context.MODE_PRIVATE)
        val oldHighScore = prefs.getInt(HIGHSCORE, 0)

        if(highScore > oldHighScore) {
            val editor = prefs.edit()
            editor.putInt(HIGHSCORE, highScore)
            editor.apply()
        }

        mediaPlayer.reset()
        sensorManager.unregisterListener(this, gyro)
    }

    // Thread & sensorManager initialization
    fun start() {
        Log.i("Test--Thread", "onStart: ")
        gameThread = Thread(this)

        sensorManager = context.getSystemService(SENSOR_SERVICE) as SensorManager
        if (sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE) != null) {
            gyro = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)
            sensorManager.registerListener(this, gyro, SensorManager.SENSOR_DELAY_FASTEST, SensorManager.SENSOR_DELAY_FASTEST)
        }

    }

    // Thread Start
    fun resume() {
        Log.i("Test--Thread", "onResume: ")
        playing = true
        prepareLevel()
        gameThread.start()
    }

    fun restart() {
        Log.i("Test--Thread", "onRestart: ")
    }

    // Shoot when Touch
    override fun onTouchEvent(motionEvent: MotionEvent): Boolean {
        val motionArea = screenHeight.toDouble()
        when (motionEvent.action and MotionEvent.ACTION_MASK) {
            MotionEvent.ACTION_POINTER_DOWN,
            MotionEvent.ACTION_DOWN,
            MotionEvent.ACTION_MOVE-> {
                paused = false

                if (motionEvent.y in 0.0 .. motionArea) {
                    if (playerBullet.shoot(
                            playerShip.position.left + playerShip.width / 2f,
                            playerShip.position.top,
                            playerBullet.up)) {

                        sfxPlayer.playSound(SFXPlayer.shootID, prefs.getInt(VOLUME_SFX, 100).toFloat())
                    }
                }
            }
        }
        return true
    }

    // Player move when tilting the device
    override fun onSensorChanged(p0: SensorEvent?) {
        if(gyroData == null) {
            gyroData = SensorData(p0!!.values[0], p0!!.values[1], p0!!.values[2], p0!!.timestamp)
            timeGyro = System.currentTimeMillis()
        } else {
            gyroData!!.x = p0!!.values[0]
            gyroData!!.y = p0!!.values[1]
            gyroData!!.z = p0!!.values[2]
            var time = (System.currentTimeMillis() - timeGyro)/(1000f)
            gyroY += gyroData!!.y * time
        }

        var temp1 = (gyroData!!.y * (180/Math.PI))
        var temp2 = (gyroY * (180/Math.PI))
        Log.i("Test---", temp1.toString() + " " + temp2.toString())
        timeGyro = System.currentTimeMillis()

        if(temp2 in -9.0..9.0) {
            playerShip.moving = PlayerShip.stopped
        } else if(temp2 < -9.0) {
            playerShip.moving = PlayerShip.left
            if(temp2 <= -25.0) {
                playerShip.speed = 400f
            } else {
                playerShip.speed = 300f
            }
        } else if (temp2 > 9.0) {
            playerShip.moving = PlayerShip.right
            if(temp2 >= 25.0) {
                playerShip.speed = 400f
            } else {
                playerShip.speed = 300f
            }
        }
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
        Log.i("Test-- A", p0!!.name)
    }
}

