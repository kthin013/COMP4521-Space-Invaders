package com.example.compproject

import android.content.Context
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.view.setPadding
import androidx.fragment.app.DialogFragment

class settingFragment : DialogFragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var rootView: View = inflater.inflate(R.layout.fragment_settings, container, false)

        val prefs = context?.getSharedPreferences("Space Invader", Context.MODE_PRIVATE)
        val fragmentWidth= this.resources.displayMetrics.widthPixels * 0.7f

        rootView.findViewById<ImageButton>(R.id.backingButton).setPadding((fragmentWidth * 0.07).toInt())
        rootView.findViewById<TextView>(R.id.settingTitle).textSize = fragmentWidth * 0.06f
        rootView.findViewById<TextView>(R.id.backgroundTitleText).textSize = fragmentWidth * 0.04f
        rootView.findViewById<TextView>(R.id.backgroundNum).textSize = fragmentWidth * 0.04f
        rootView.findViewById<TextView>(R.id.sfxTitleText).textSize = fragmentWidth * 0.04f
        rootView.findViewById<TextView>(R.id.sfxNum).textSize = fragmentWidth * 0.04f

        var backgroundStartPoint = prefs?.getInt(VOLUME_BGM, 100)
        var sfxStartPoint = prefs?.getInt(VOLUME_SFX, 100)
        var backgroundEndPoint = 0
        var sfxEndPoint = 0

        val imageButton = rootView.findViewById<ImageButton>(R.id.backingButton)
        val sfxNum = rootView.findViewById<TextView>(R.id.sfxNum)
        val sfxSeekBar = rootView.findViewById<SeekBar>(R.id.sfxSeekBar)
        val backgroundNum = rootView.findViewById<TextView>(R.id.backgroundNum)
        val backgroundSeekBar = rootView.findViewById<SeekBar>(R.id.backgroundSeekBar)

        if (sfxStartPoint != null) {
            sfxSeekBar.setProgress(sfxStartPoint, false)
            sfxNum.text = sfxStartPoint.toString()
        }
        if (backgroundStartPoint != null) {
            backgroundSeekBar.setProgress(backgroundStartPoint, false)
            backgroundNum.text = backgroundStartPoint.toString()
        }

        imageButton.setOnClickListener() {
            dismiss()
        }

        sfxSeekBar.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                sfxNum.text = p1.toString()
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {
                Log.i("Test--", sfxStartPoint.toString())
            }

            override fun onStopTrackingTouch(p0: SeekBar?) {
                sfxEndPoint = p0!!.progress
                val editor = prefs?.edit()
                editor?.putInt(VOLUME_SFX,sfxEndPoint)
                editor?.apply()
                Log.i("Test--", sfxEndPoint.toString())
            }

        })

        backgroundSeekBar.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                backgroundNum.text = p1.toString()
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {
                Log.i("Test--", backgroundStartPoint.toString())
            }

            override fun onStopTrackingTouch(p0: SeekBar?) {
                backgroundEndPoint = p0!!.progress
                val editor = prefs?.edit()
                editor?.putInt(VOLUME_BGM,backgroundEndPoint)
                editor?.apply()

                Log.i("Test--", backgroundEndPoint.toString())
            }

        })

        return rootView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val fragmentWidth= this.resources.displayMetrics.widthPixels * 0.7
        val fragmentHeight = this.resources.displayMetrics.heightPixels * 0.7

        dialog?.window?.attributes?.width = fragmentWidth.toInt()
        dialog?.window?.attributes?.height = fragmentHeight.toInt()

    }
}