package com.example.compproject

import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.core.view.setPadding
import androidx.fragment.app.DialogFragment

class gameoverFragment: DialogFragment()  {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var rootView: View = inflater.inflate(R.layout.fragment_gameover, container, false)
        val fragmentWidth= this.resources.displayMetrics.widthPixels * 0.5f
        val score = arguments?.getInt("score")

        rootView.findViewById<ImageButton>(R.id.backingButton).setPadding((fragmentWidth * 0.08).toInt())
        rootView.findViewById<ImageButton>(R.id.retryButton).setPadding((fragmentWidth * 0.08).toInt())
        rootView.findViewById<TextView>(R.id.gameOverTitle).textSize = fragmentWidth * 0.07f
        rootView.findViewById<TextView>(R.id.scoreTitleText).textSize = fragmentWidth * 0.05f
        rootView.findViewById<TextView>(R.id.scoreNum).textSize = fragmentWidth * 0.05f
        rootView.findViewById<TextView>(R.id.scoreNum).text = score.toString()

        val backingButton = rootView.findViewById<ImageButton>(R.id.backingButton)
        val retryButton = rootView.findViewById<ImageButton>(R.id.retryButton)

        backingButton.setOnClickListener() {
            activity?.finish()
        }

        retryButton.setOnClickListener() {
            dismiss()
        }

        val scaleDown: ObjectAnimator = ObjectAnimator.ofPropertyValuesHolder(
            rootView.findViewById<TextView>(R.id.scoreNum),
            PropertyValuesHolder.ofFloat("scaleX", 1.5f),
            PropertyValuesHolder.ofFloat("scaleY", 1.5f))

        scaleDown.duration = 500
        scaleDown.repeatCount = ObjectAnimator.INFINITE
        scaleDown.repeatMode = ObjectAnimator.REVERSE

        scaleDown.start()

        return rootView
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val fragmentWidth= this.resources.displayMetrics.widthPixels * 0.7
        val fragmentHeight = this.resources.displayMetrics.heightPixels * 0.5

        dialog?.window?.attributes?.width = fragmentWidth.toInt()
        dialog?.window?.attributes?.height = fragmentHeight.toInt()

    }
}