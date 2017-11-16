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
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import java.util.logging.Handler


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
    private lateinit var DBinfoRef : DatabaseReference

    // 날짜 관련 변수
    private val simpleDateFormat : SimpleDateFormat = SimpleDateFormat("yyyy.MM.dd.")

    private var now : Long = 0
    private lateinit var today : Date
    private var savedDate : Date? = null

    // 날짜 관련 함수
    fun getDate() : String {
        now = System.currentTimeMillis()
        today = Date(now)
        DBinfoRef.addValueEventListener(tomorrowListener)
        Timer().schedule(object : TimerTask(){
            override fun run() {
                val todayStr = simpleDateFormat.format(today)
                val savedDateStr = simpleDateFormat.format(savedDate)
                Log.d("haha","today: " + todayStr + " savedDate: " + savedDateStr)

                Log.d("haha","isNextday: " + isNextday(today,savedDate))
            }
        }, 2500)
        return today.toString()
    }
    fun isNextday( today: Date, savedToday : Date?) : Boolean {
        if(savedToday == null) {
            Log.d("haha","error!! savedToday is null: " + savedToday)
            return false
        }
        val cal = Calendar.getInstance()
        val formatter : DateFormat = DateFormat.getDateInstance(DateFormat.MEDIUM)
        formatter.timeZone = cal.timeZone

        val compareToday = simpleDateFormat.parse(formatter.format(today))
        val compareSavedToday = simpleDateFormat.parse(formatter.format(savedToday))
        Log.d("haha","compareToday: " + compareToday.toString() + " compareSavedToday: " + compareSavedToday.toString())

        Log.d("haha",compareToday.toString() + " " + compareSavedToday.toString() + " " + (compareToday > compareSavedToday).toString() + " " + (compareToday == compareSavedToday).toString() + " " + (compareToday < compareSavedToday).toString())
        return compareToday > compareSavedToday
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

        DBinfoRef = FirebaseDatabase.getInstance().getReference("/info/" + path + "/today")
        getDate()
        Log.d("haha","today: " + today + " savedDate: " + savedDate)



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
                    getDate()

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
                val cal = Calendar.getInstance()
                val formatter = DateFormat.getDateInstance()
                formatter.timeZone = cal.timeZone
                Log.d("haha","p0.value = " + p0.value.toString())
                val dateStr = p0.value.toString()
                savedDate = simpleDateFormat.parse(dateStr)
                Log.d("haha", "savedDate: " + savedDate.toString())
            } else {
                Log.d("haha", "today: " + today)
                DBinfoRef.child(path).child("today").setValue(simpleDateFormat.format(today))
                Log.d("haha", "today: " + today)
            }
        }
        override fun onCancelled(p0: DatabaseError?) {

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
