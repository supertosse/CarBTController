package com.supertosse.carbtcontroller

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.SeekBar
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.os.ParcelUuid
import java.io.InputStream
import java.io.OutputStream
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

class MainActivity : AppCompatActivity() {

    private var speed:Int = 0   // max is 20
    private var turn:Int = 0    // Value from 0 to 20(max seekBar value is 20). 10 is straight ahead, 0 is full left.

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupSeekBars(this)

        connectButton.setOnClickListener {
            connect()
        }

        hornButton.setOnClickListener {
            honk(null)
        }

        stopButton.setOnClickListener{
            sendStop()
        }
    }

    override fun onResume() {
        super.onResume()
        // TODO: add label "connected" based on global I/O streams null or not
        if ((applicationContext as MyApplication).outputStream != null) {
            connectionTextView.setText("Connected")
        } else {
            connectionTextView.setText("Unconnected")
        }
    }


    //Updates speed and turn fields when seekBar is updated
    private fun setupSeekBars(m: MainActivity) {
        speedSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, i: Int, b: Boolean) {
                m.speed = i
                speedTextView.text = "Speed: " + m.speed.toString()

                if ((applicationContext as MyApplication).outputStream != null) {
                    Log.d("#############", "val: " + i)
                    sendDriveCommand()
                }
            }
            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {
                seekBar.setProgress(0)
                sendDriveCommand()
            }
        })
        turnSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, i: Int, b: Boolean) {
                m.turn = i
                turnTextView.text = "Turn: " + m.turn.toString()

                if ((applicationContext as MyApplication).outputStream != null) {
                    Log.d("#############", "val: " + i)
                    sendDriveCommand()
                }
            }
            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {
                seekBar.setProgress(10)
                sendDriveCommand()
            }
        })
        speedSeekBar.progress = 0
        turnSeekBar.progress = 10
    }

    // OnClik horn button
    fun honk(view: View?){
        Log.d("MainActivity", "Horn")

        if ((applicationContext as MyApplication).outputStream != null) {
            (applicationContext as MyApplication).outputStream!!.write("honk 100 100\n".toByteArray())
        } else {
            Toast.makeText(applicationContext, "BT Not Connected", Toast.LENGTH_SHORT).show()
        }
    }

    fun connect() {
        val intent = Intent(this, Main2Activity::class.java).apply {}
        startActivity(intent)
    }

    fun sendStop() {
        val outputStream = (applicationContext as MyApplication).outputStream

        if (outputStream == null) {
            return
        }

        outputStream.write(("stop\n").toByteArray())
    }

    fun sendDriveCommand() {
        val outputStream = (applicationContext as MyApplication).outputStream

        if (outputStream == null) {
            return
        }

        var turnVal = turnSeekBar.progress - 10// this.turn - 10
        var speedVal = (speedSeekBar.progress + 9) * 8 // this.speed * 12

        if (speedVal < 90) { // Just turn
            if (abs(turnVal) >= 4) {
                if (turnVal <= -4) {
                    outputStream.write(("right " + abs(turnVal)*24 + "\n").toByteArray())
                    outputStream.write(("left 0\n").toByteArray())
                } else if(turnVal >= 4) {
                    outputStream.write(("left " + abs(turnVal)*24 + "\n").toByteArray())
                    outputStream.write(("right 0\n").toByteArray())
                }
            } else {  // Stop if speed and turn both too low
                outputStream.write("stop\n".toByteArray())
            }
        } else { // Driving fast enough
            if (turnVal == 0) {
                outputStream.write(("fwd " + speedVal + "\n").toByteArray())
            } else if (turnVal < 0) { // LEFT
                /*
                val delta = (((255-speedVal)* abs(turnVal))/10) / 2
                val rightMotor = max(speedVal + delta, min(255, speedVal + 2*delta)) //(speedVal + delta)
                val leftMotor = max(speedVal, 90)
                */

                val deltaMax = 255-speedVal
                // val deltaMin = speedVal-90

                val leftMotor = 90//  max(90, speedVal - deltaMax * abs(turnVal/5))
                val rightMotor = min(255, speedVal + deltaMax * ((abs(turnVal/2)) /5 + 5))


                outputStream.write(("left " + leftMotor + "\n").toByteArray())
                outputStream.write(("right " + rightMotor + "\n").toByteArray())
            } else if (turnVal > 0) { // RIGHT
                val deltaMax = 255-speedVal
                // val deltaMin = speedVal-90

                val rightMotor = 90 //max(90, speedVal - deltaMax * abs(turnVal/5))
                val leftMotor = min(255, speedVal + (deltaMax * abs(turnVal/2) /5 + 5)  )

                //val delta = (((255-speedVal)* abs(turnVal))/10) / 2
                //val leftMotor = max(speedVal + delta, min(255, speedVal + 2*delta)) //(speedVal + delta)
                //val rightMotor = max(speedVal - delta, 90)

                outputStream.write(("right " + rightMotor + "\n").toByteArray())
                outputStream.write(("left " + leftMotor + "\n").toByteArray())
            }
        }
    }
}
