package com.example.compproject

import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.setPadding
import com.example.compproject.databinding.ActivityMainBinding
import kotlin.random.Random


class MainActivity : AppCompatActivity(){

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.playButton.setOnClickListener{
            val gameIntent = Intent(this, GameActivity::class.java)
            startActivity(gameIntent)
        }

        binding.settingButton.setOnClickListener{
            var settings = settingFragment()

            settings.show(supportFragmentManager, "settingFragment")
        }

        binding.endButton.setOnClickListener{
            finish()
        }

        val screenWidth:Int = this.resources.displayMetrics.widthPixels
        val screenHeight: Int = this.resources.displayMetrics.heightPixels
        val prefs = this.getSharedPreferences("Space Invader", Context.MODE_PRIVATE)

        binding.highestScoreNum.text = prefs.getInt(HIGHSCORE, 0).toString()
        binding.titleText.textSize = screenWidth * 0.08f
        binding.highestScore.textSize = screenWidth * 0.04f
        binding.highestScoreNum.textSize = screenWidth * 0.04f
        binding.playButton.setPadding((screenWidth * 0.05).toInt())
        binding.settingButton.setPadding((screenWidth * 0.05).toInt())
        binding.endButton.setPadding((screenWidth * 0.05).toInt())

        val playerShipWidth = screenWidth / 9f
        val playerShipHeight = screenHeight / 25f
        binding.playerShipImage.layoutParams.height = playerShipHeight.toInt()
        binding.playerShipImage.layoutParams.width = playerShipWidth.toInt()
        binding.playerShipImage.x =  (screenWidth / 2f - (playerShipWidth / 2f))
        binding.playerShipImage.y = (screenHeight - playerShipHeight - 100)

        val invaderWidth = screenWidth * 0.08f
        val invaderHeight = screenHeight * 0.04f
        binding.invadersImage0.layoutParams.height = invaderHeight.toInt()
        binding.invadersImage0.layoutParams.width = invaderWidth.toInt()
        binding.invadersImage0.x = screenWidth * 0.2f
        binding.invadersImage0.y = screenHeight * 0.65f

        binding.invadersImage1.layoutParams.height = invaderHeight.toInt()
        binding.invadersImage1.layoutParams.width = invaderWidth.toInt()
        binding.invadersImage1.x = screenWidth * 0.8f
        binding.invadersImage1.y = screenHeight * 0.75f

        binding.invadersImage2.layoutParams.height = invaderHeight.toInt()
        binding.invadersImage2.layoutParams.width = invaderWidth.toInt()
        binding.invadersImage2.x = screenWidth * 0.3f
        binding.invadersImage2.y = screenHeight * 0.32f

        binding.invadersImage3.layoutParams.height = invaderHeight.toInt()
        binding.invadersImage3.layoutParams.width = invaderWidth.toInt()
        binding.invadersImage3.x = screenWidth * 0.75f
        binding.invadersImage3.y = screenHeight * 0.55f

        binding.invadersImage4.layoutParams.height = invaderHeight.toInt()
        binding.invadersImage4.layoutParams.width = invaderWidth.toInt()
        binding.invadersImage4.x = screenWidth * 0.82f
        binding.invadersImage4.y = screenHeight * 0.1f

        binding.invadersImage0.rotation = (-120..120).random(Random(System.nanoTime())).toFloat()
        binding.invadersImage1.rotation = (-120..120).random(Random(System.nanoTime())).toFloat()
        binding.invadersImage2.rotation = (-120..120).random(Random(System.nanoTime())).toFloat()
        binding.invadersImage3.rotation = (-120..120).random(Random(System.nanoTime())).toFloat()
        binding.invadersImage4.rotation = (-120..120).random(Random(System.nanoTime())).toFloat()

        val starWidth = screenWidth * 0.15f
        binding.starImage0.layoutParams.height = starWidth.toInt()
        binding.starImage0.layoutParams.width = starWidth.toInt()
        binding.starImage0.x = screenWidth * 0.15f
        binding.starImage0.y = screenHeight * 0.05f

        binding.starImage1.layoutParams.height = starWidth.toInt()
        binding.starImage1.layoutParams.width = starWidth.toInt()
        binding.starImage1.x = screenWidth * 0.55f
        binding.starImage1.y = screenHeight * 0.5f

        val scaleDown: ObjectAnimator = ObjectAnimator.ofPropertyValuesHolder(
            binding.highestScoreNum,
            PropertyValuesHolder.ofFloat("scaleX", 1.5f),
            PropertyValuesHolder.ofFloat("scaleY", 1.5f))

        scaleDown.duration = 500
        scaleDown.repeatCount = ObjectAnimator.INFINITE
        scaleDown.repeatMode = ObjectAnimator.REVERSE

        scaleDown.start()
    }

    override fun onResume() {
        super.onResume()
        val prefs = this.getSharedPreferences("Space Invader", Context.MODE_PRIVATE)
        binding.highestScoreNum.text = prefs.getInt(HIGHSCORE, 0).toString()
        Log.i("Test--MainActivity", "onResume:")
    }

    override fun onPause() {
        super.onPause()
        Log.i("Test--MainActivity", "onPause:")
    }

    override fun onStop() {
        super.onStop()
        Log.i("Test--MainActivity", "onStop:")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.i("Test--MainActivity", "onDestroy:")
    }

    override fun onRestart() {
        super.onRestart()
        Log.i("Test--MainActivity", "onReStart:")
    }
}