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


    // 날짜를 위한 포맷
    private val simpleFormat = SimpleDateFormat("yyyy-MM-dd",Locale.KOREA)

    private var dailyPt: Any = 0
    private var point:Any = 0
    private var savedTodayStr : String = "0000-00-00"


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

        // 날짜의 관한 변수
        var cal  = Calendar.getInstance()
        var today = cal.time
        var todayStr = simpleFormat.format(today)

        Log.d("MainActivity", "today: $todayStr")

        //날이 넘어가는 거 체크
        val dailyDataDB = FirebaseDatabase.getInstance().getReference("/info/" + path)
        dailyDataDB.addListenerForSingleValueEvent(dailyDataListener)

        val todayYear = todayStr.substring(0,todayStr.indexOf("-"))
        val todayMonth = todayStr.substring(5,todayStr.indexOf("-",5))
        val todayDate = todayStr.substring(8)

        var savedTodayYear = savedTodayStr.substring(0,savedTodayStr.indexOf("-"))
        var savedTodayMonth = savedTodayStr.substring(5,savedTodayStr.indexOf("-",5))
        var savedTodayDate = savedTodayStr.substring(8)

        if(todayDate.toInt() > savedTodayDate.toInt()) {
            Log.i("MainActivity","nextday!  " + todayDate.toInt().toString() + "  " + savedTodayDate.toInt())
            database.child("info").child(path).child("dailyPt").setValue(0)
        } else {
            if (todayDate.toInt() < savedTodayDate.toInt() && todayMonth.toInt() > savedTodayMonth.toInt()) {
                database.child("info").child(path).child("dailyPt").setValue(0)
            } else {
                if(todayDate.toInt() < savedTodayDate.toInt() && todayMonth.toInt() < savedTodayMonth.toInt() && todayYear.toInt() > savedTodayYear.toInt()) {
                    database.child("info").child(path).child("dailyPt").setValue(0)
                }
            }
        }

        database.child("info").child(path).child("today").setValue(todayStr)

        savedTodayYear = savedTodayStr.substring(0,savedTodayStr.indexOf("-"))
        savedTodayMonth = savedTodayStr.substring(5,savedTodayStr.indexOf("-",5))
        savedTodayDate = savedTodayStr.substring(8)

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
                    database.child("info").child(path).child("dailyPt").setValue(serviceData.toInt() + dailyPt.toString().toInt())

                    //날이 넘어가는 거 체크
                    if(todayDate.toInt() > savedTodayDate.toInt()) {
                        Log.i("MainActivity","nextday!  " + todayDate.toInt().toString() + "  " + savedTodayDate.toInt())
                        database.child("info").child(path).child("dailyPt").setValue(0)
                    } else {
                        if (todayDate.toInt() < savedTodayDate.toInt() && todayMonth.toInt() > savedTodayMonth.toInt()) {
                            database.child("info").child(path).child("dailyPt").setValue(0)
                        } else {
                            if(todayDate.toInt() < savedTodayDate.toInt() && todayMonth.toInt() < savedTodayMonth.toInt() && todayYear.toInt() > savedTodayYear.toInt()) {
                                database.child("info").child(path).child("dailyPt").setValue(0)
                            }
                        }
                    }

                    database.child("info").child(path).child("today").setValue(todayStr)
                    Log.d("dayday", " " + todayStr)

                    savedTodayYear = savedTodayStr.substring(0,savedTodayStr.indexOf("-"))
                    savedTodayMonth = savedTodayStr.substring(5,savedTodayStr.indexOf("-",5))
                    savedTodayDate = savedTodayStr.substring(8)

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
            intent.putExtra("dailyPt", (serviceData.toInt() + dailyPt.toString().toInt()).toString())
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

    val dailyDataListener = object: ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot?) {
            for(snapshot in dataSnapshot!!.children) {
                if(snapshot.key == "dailyPt") {
                    dailyPt = snapshot.value!!
                } else if (snapshot.key == "today") {

                    savedTodayStr = snapshot.value!!.toString()
                    Log.d("dailyDataListener","is it run?  " + savedTodayStr)
                }
            }
        }
        override fun onCancelled(p0: DatabaseError?) {

        }
    }

    private inner class PlayingReceiver : BroadcastReceiver() {

        override fun onReceive(context: Context, intent: Intent) {

            val pointDB = FirebaseDatabase.getInstance().getReference("/user")
            pointDB.addListenerForSingleValueEvent(postListener)

            val dailyPtDB = FirebaseDatabase.getInstance().getReference("/info/" + path)
            dailyPtDB.addListenerForSingleValueEvent(dailyDataListener)
            Log.d("OnReceive","dailyPt: $dailyPt")

            Log.i("PlayignReceiver", "IN")
            serviceData = intent.getStringExtra("stepService")
            dailyPt = serviceData.toInt() + dailyPt.toString().toInt()
            countText!!.text = (serviceData.toInt() + point.toString().toInt()).toString()
            Log.i("test", "$serviceData $point $dailyPt")
        }
    }

}
