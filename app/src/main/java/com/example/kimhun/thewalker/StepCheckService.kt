package com.example.kimhun.thewalker

import android.app.Service
import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.IBinder
import android.util.Log

class StepCheckService : Service(), SensorEventListener {

    internal var count = StepValue.step
    private var lastTime: Long = 0
    private var speed: Float = 0.toFloat()
    private var lastX: Float = 0.toFloat()
    private var lastY: Float = 0.toFloat()
    private var lastZ: Float = 0.toFloat()

    private var x: Float = 0.toFloat()
    private var y: Float = 0.toFloat()
    private var z: Float = 0.toFloat()

    private var sensorManager: SensorManager? = null
    private var accelerometerSensor: Sensor? = null

    override fun onCreate() {
        super.onCreate()
        Log.i("onCreate", "IN")
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        accelerometerSensor = sensorManager!!.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    } // end of onCreate

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        Log.i("onStartCommand", "IN")
        if (accelerometerSensor != null) {
            sensorManager!!.registerListener(this, accelerometerSensor, SensorManager.SENSOR_DELAY_GAME)
        } // end of if

        return Service.START_STICKY
    } // end of onStartCommand

    override fun onDestroy() {
        super.onDestroy()
        Log.i("onDestroy", "IN")
        if (sensorManager != null) {
            sensorManager!!.unregisterListener(this)
            StepValue.step = 0
        } // end of if
    } // end of onDestroy

    override fun onSensorChanged(event: SensorEvent) {
        Log.i("onSensorChanged", "IN")
        if (event.sensor.type == Sensor.TYPE_ACCELEROMETER) {
            val currentTime = System.currentTimeMillis()
            val gabOfTime = currentTime - lastTime

            if (gabOfTime > 100) { //  gap of time of step count
                Log.i("onSensorChanged_IF", "FIRST_IF_IN")
                lastTime = currentTime

                x = event.values[0]
                y = event.values[1]
                z = event.values[2]

                speed = Math.abs(x + y + z - lastX - lastY - lastZ) / gabOfTime * 50000

                if (speed > SHAKE_THRESHOLD) {
                    Log.i("onSensorChanged_IF", "SECOND_IF_IN")
                    val myFilteredResponse = Intent("make.a.yong.manbo")

                    StepValue.step = count++

                    val msg = (StepValue.step / 2).toString() + ""
                    myFilteredResponse.putExtra("stepService", msg)

                    sendBroadcast(myFilteredResponse)
                } // end of if

                lastX = event.values[0]
                lastY = event.values[1]
                lastZ = event.values[2]
            } // end of if
        } // end of if

    } // end of onSensorChanged

    override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {

    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    companion object {
        private val SHAKE_THRESHOLD = 800
    }
}
