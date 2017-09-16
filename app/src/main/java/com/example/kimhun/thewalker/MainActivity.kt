package com.example.kimhun.thewalker

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast


class MainActivity : Activity() {

    private var manboService: Intent? = null
    private var receiver: BroadcastReceiver? = null
    private var flag = true
    private var serviceData: String = ""
    private var countText: TextView? = null
    private var playingBtn: Button? = null
    private var infoBtn: Button? = null
    private var buddyBtn: Button? = null
    private var outBtn: Button? = null

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        manboService = Intent(this, StepCheckService::class.java)
        receiver = PlayingReceiver()

        countText = findViewById(R.id.stepText) as TextView
        playingBtn = findViewById(R.id.btnStopService) as Button
        infoBtn = findViewById(R.id.status) as Button
        buddyBtn = findViewById(R.id.friends) as Button
        outBtn = findViewById(R.id.logout) as Button

        playingBtn!!.setOnClickListener {
            if (flag) {
                // TODO Auto-generated method stub

                playingBtn!!.text = "Go !!"
                try {
                    val mainFilter = IntentFilter("make.a.yong.manbo")
                    registerReceiver(receiver, mainFilter)
                    startService(manboService)
                    Toast.makeText(applicationContext, "Playing game", Toast.LENGTH_SHORT).show()
                } catch (e: Exception) {
                    // TODO: handle exception
                    Toast.makeText(applicationContext, e.message,
                            Toast.LENGTH_LONG).show()
                }

            } else {

                playingBtn!!.text = "Stop !!"

                // TODO Auto-generated method stub
                try {

                    unregisterReceiver(receiver)

                    stopService(manboService)
                    Toast.makeText(applicationContext, "Stop", Toast.LENGTH_SHORT).show()

                    // txtMsg.setText("After stoping Service:\n"+service.getClassName());
                } catch (e: Exception) {
                    // TODO: handle exception
                    Toast.makeText(applicationContext, e.message,
                            Toast.LENGTH_LONG).show()
                }

            }

            flag = !flag
        }

        infoBtn!!.setOnClickListener{
            var intent : Intent = Intent(this, InfoActivity::class.java)
            intent.putExtra("point",serviceData)
            startActivity(intent);
        }

        buddyBtn!!.setOnClickListener{
            var intent : Intent = Intent(this, FriendsActivity::class.java);
            startActivity(intent);
        }

        outBtn!!.setOnClickListener{

        }







    }

    private inner class PlayingReceiver : BroadcastReceiver() {

        override fun onReceive(context: Context, intent: Intent) {
            Log.i("PlayignReceiver", "IN")
            serviceData = intent.getStringExtra("stepService")
            countText!!.text = serviceData

        }
    }

}
