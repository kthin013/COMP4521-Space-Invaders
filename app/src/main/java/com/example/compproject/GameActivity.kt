package com.example.compproject

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity


const val HIGHSCORE = "highScore"
const val VOLUME_SFX = "volume_sfx"
const val VOLUME_BGM = "volume_bgm"

class GameActivity : AppCompatActivity() {
    private var spaceInvaderView: SpaceInvaderView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val screenWidth:Int = this.resources.displayMetrics.widthPixels
        val screenHeight:Int = this.resources.displayMetrics.heightPixels
        spaceInvaderView = SpaceInvaderView(this, screenWidth, screenHeight)
        setContentView(spaceInvaderView)

    }

//    override fun onSaveInstanceState(outState: Bundle) {
//        outState.putIntegerArrayList(KEY_BRICKS, spaceInvaderView?.saveBrickList)
//
//        Log.i("Test--onSaveInstance", "Called: " + spaceInvaderView?.saveBrickList.toString())
//        savedInstanceState = outState
//        super.onSaveInstanceState(outState)
//    }

    override fun onStart() {
        super.onStart()
        Log.i("Test--GameActivity", "onStart:")
        spaceInvaderView?.start()
    }

    override fun onResume() {
        super.onResume()
        Log.i("Test--GameActivity", "onResume:")
        spaceInvaderView?.resume()
    }

    override fun onPause() {
        super.onPause()
        Log.i("Test--GameActivity", "onPause:")
        spaceInvaderView?.pause()

    }

    override fun onStop() {
        super.onStop()
        finish()
        Log.i("Test--GameActivity", "onStop:")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.i("Test--GameActivity", "onDestroy:")
    }

    override fun onRestart() {
        super.onRestart()
        Log.i("Test--GameActivity", "onReStart: ")
    }

}