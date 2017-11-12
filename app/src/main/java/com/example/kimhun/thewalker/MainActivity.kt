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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.text.SimpleDateFormat
import java.util.*


class MainActivity : Activity() {

    private var manboService: Intent? = null
    private var receiver: BroadcastReceiver? = null
    private var flag = true
    private var serviceData: String = "0"
    private var countText: TextView? = null
    private var playingBtn: Button? = null
    private var infoBtn: Button? = null
    private var buddyBtn: Button? = null
    private var outBtn: Button? = null
    private lateinit var database : DatabaseReference
    private lateinit var mAuth: FirebaseAuth
    private lateinit var path:String

    private var dailyPt: Int = 0
    private var point:Any = 0

    // info접근 db레퍼런스
    var DBinfoRef = FirebaseDatabase.getInstance().getReference("/info")

    // 날짜 관련 변수
    private var simpleDateFormat : SimpleDateFormat = SimpleDateFormat("yyyy-MM-dd  HH:mm:ss")

    private var now : Long = 0
    private lateinit var today : Date
    private var tomorrow : Date? = null

    // 날짜 관련 함수
    fun getDate() : String {
        now = System.currentTimeMillis()
        today = Date(now)
        DBinfoRef.addValueEventListener(tomorrowListener)
        if(tomorrow == null) {
            tomorrow = Date(now+(1000*60*60*24)*+1)
        }
        val todayStr = simpleDateFormat.format(today)
        val tomorrowStr = simpleDateFormat.format(tomorrow)
        Log.d("haha","today: " + todayStr + " tomorrow: " + tomorrowStr)
        return today.toString()
    }

    fun isNextDay() : Boolean {
        return (today.compareTo(tomorrow) == 0)
    }

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



        database = FirebaseDatabase.getInstance().reference
        mAuth = FirebaseAuth.getInstance()
        val currentUser = mAuth.currentUser
        val userId = currentUser!!.email
        var index = userId!!.indexOf("@")
        path = userId.substring(0,index)

        getDate()
        Log.d("haha","today: " + today + " tomorrow: " + tomorrow)
        Log.d("haha","answer: " + isNextDay().toString())

        playingBtn!!.setOnClickListener {
            if (flag) {
                // TODO Auto-generated method stub

                playingBtn!!.text = "Go !!"
                try {
                    val mainFilter = IntentFilter("make.a.yong.manbo")
                    registerReceiver(receiver, mainFilter)
                    startService(manboService)
                    //Toast.makeText(applicationContext, "Playing game", Toast.LENGTH_SHORT).show()
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
                    database.child("user").child(path).setValue(serviceData.toInt() + point.toString().toInt())

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
            intent.putExtra("point", countText!!.text)
            intent.putExtra("dailyPt", (dailyPt - 5).toString())
            startActivity(intent)
        }

        buddyBtn!!.setOnClickListener{
            var intent : Intent = Intent(this, FriendsActivity::class.java);
            startActivity(intent)
        }

        outBtn!!.setOnClickListener{
            FirebaseAuth.getInstance().signOut()
            var intent : Intent = Intent(this, StartActivity::class.java)
            startActivity(intent)
            finish()
        }

    }

    val postListener = object: ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot?) {
            for(snapshot in dataSnapshot!!.children) {

                if(snapshot.key == path) {
                    point = snapshot.value!!
                }

                Log.d("FriendsActivity","ValueEventListener:" + point)

            }
        }
        override fun onCancelled(p0: DatabaseError?) {

        }
    }

    val tomorrowListener = object : ValueEventListener{
        override fun onDataChange(p0: DataSnapshot?) {
            Log.d("haha","exists(): " + p0!!.exists())
            if(p0.exists()) {
                Log.d("addValueEventListener", "DB DATA: " + simpleDateFormat.format(p0?.getValue(Date::class.java)))
                tomorrow = p0.getValue(Date::class.java)!!
            } else {
                tomorrow = Date(now+(1000*60*60*24)*+1)
            }
        }
        override fun onCancelled(p0: DatabaseError?) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }
    }

    private inner class PlayingReceiver : BroadcastReceiver() {

        override fun onReceive(context: Context, intent: Intent) {

            val pointDB = FirebaseDatabase.getInstance().getReference("/user")
            pointDB.addListenerForSingleValueEvent(postListener)

            Log.i("PlayignReceiver", "IN")
            serviceData = intent.getStringExtra("stepService")
            dailyPt += intent.getIntExtra("scorePerStep", -1)
            countText!!.text = (serviceData.toInt() + point.toString().toInt()).toString()
            Log.i("test", "$serviceData $point $dailyPt")
        }
    }

}
