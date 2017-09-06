package com.example.kimhun.thewalker

import android.app.Activity
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.widget.TextView


class MainActivity : Activity(), SensorEventListener {

    private var lastTime: Long = 0
    private var speed: Float = 0.toFloat()
    private var lastX: Float = 0.toFloat()
    private var lastY: Float = 0.toFloat()
    private var lastZ: Float = 0.toFloat()
    private var x: Float = 0.toFloat()
    private var y: Float = 0.toFloat()
    private var z: Float = 0.toFloat()
    private var countWalk: Int = 0.toInt()
    private var textCount: TextView = findViewById(R.id.count_text) as TextView

    private var sensorManager: SensorManager? = null
    private var accelerormeterSensor: Sensor? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        accelerormeterSensor = sensorManager!!.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        textCount.setText(Integer.toString(countWalk))

    }

    public override fun onStart() {
        super.onStart()
        if (accelerormeterSensor != null)
            sensorManager!!.registerListener(this, accelerormeterSensor,
                    SensorManager.SENSOR_DELAY_GAME)
    }

    public override fun onStop() {
        super.onStop()
        if (sensorManager != null)
            sensorManager!!.unregisterListener(this)
    }

    override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {

    }

    override fun onSensorChanged(event: SensorEvent) {
        if (event.sensor.type == Sensor.TYPE_ACCELEROMETER) {
            val currentTime = System.currentTimeMillis()
            val gabOfTime = currentTime - lastTime
            if (gabOfTime > 100) {
                lastTime = currentTime
                x = event.values[SensorManager.DATA_X]
                y = event.values[SensorManager.DATA_Y]
                z = event.values[SensorManager.DATA_Z]

                speed = Math.abs(x + y + z - lastX - lastY - lastZ) / gabOfTime * 10000

                if (speed > SHAKE_THRESHOLD) {
                    // 이벤트발생!!
                    countWalk++
                    textCount.setText(Integer.toString(countWalk))
                }

                lastX = event.values[DATA_X]
                lastY = event.values[DATA_Y]
                lastZ = event.values[DATA_Z]
            }

        }

    }

    companion object {
        private val SHAKE_THRESHOLD = 800
        private val DATA_X = SensorManager.DATA_X
        private val DATA_Y = SensorManager.DATA_Y
        private val DATA_Z = SensorManager.DATA_Z
    }
}
