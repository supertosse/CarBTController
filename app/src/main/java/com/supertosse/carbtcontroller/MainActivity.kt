package com.supertosse.carbtcontroller

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.SeekBar
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    private var speed:Int = 0   // max is 20
    private var turn:Int = 0    // Value from 0 to 20(max seekBar value is 20). 10 is straight ahead, 0 is full left.

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupSeekBars(this)
    }


    //Updates speed and turn fields when seekBar is updated
    private fun setupSeekBars(m: MainActivity) {
        speedSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, i: Int, b: Boolean) {
                m.speed = i
                speedTextView.text = "Speed: " + m.speed.toString()

            }
            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {}
        })
        turnSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, i: Int, b: Boolean) {
                m.turn = i
                turnTextView.text = "Turn: " + m.turn.toString()

            }
            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {}
        })
        speedSeekBar.progress = 10
        turnSeekBar.progress = 10
    }

    // OnClik horn button
    fun honk(view: View){

    }
}
