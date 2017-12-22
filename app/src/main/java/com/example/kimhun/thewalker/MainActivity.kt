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
import com.google.firebase.iid.FirebaseInstanceId
import java.text.DateFormat
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
    private var shopBtn: Button? = null
    private var outBtn: Button? = null
    private lateinit var database : DatabaseReference
    private lateinit var mAuth: FirebaseAuth
    private lateinit var path:String

    // 정보창 관련 변수
    private var dailyPt: Any = 0
    private var point:Any = 0
    private var dayMaxPt: Any = 0
    private var tall : Double = 0.0

    // 상점 관련 변수
    private var shoes : Int? = null
    private var shoesAbility : Int? = ( (shoes ?: 0) + 1 ) * 10

    // info접근 db레퍼런스
    private lateinit var DBinfoRef : DatabaseReference

    // 날짜 관련 변수
    private val simpleDateFormat : SimpleDateFormat = SimpleDateFormat("yyyy.MM.dd.")

    private var now : Long = 0
    private lateinit var today : Date
    private var savedDate : Date? = null
    private lateinit var date:Any
    private var dayCount : Int = 1

    // 날짜 관련 함수
    fun getDate() : String {
        now = System.currentTimeMillis()
        today = Date(now)
        DBinfoRef.child("today").addValueEventListener(dateListener)
        DBinfoRef.child("dayMaxPt").addValueEventListener(dayMaxPtListener)
        Timer().schedule(object : TimerTask(){
            override fun run() {
                val todayStr = simpleDateFormat.format(today)
                val savedDateStr = simpleDateFormat.format(savedDate)
                Log.d("getDate()","today: " + todayStr + " savedDate: " + savedDateStr)

                if(isNextday(today,savedDate)) {
                    dailyPt = 0
                    DBinfoRef.child("dailyPt").setValue(0)
                    DBinfoRef.child("today").setValue(todayStr)
                }
                //Log.d("haha","isNextday: " + isNextday(today,savedDate))
            }
        }, 4000)
        return today.toString()
    }
    fun isNextday( today: Date, savedToday : Date?) : Boolean {
        if(savedToday == null) {
            Log.d("isNextday()","error!! savedToday is null: " + savedToday)
            return false
        }
        val cal = Calendar.getInstance()
        val formatter : DateFormat = DateFormat.getDateInstance(DateFormat.MEDIUM)
        formatter.timeZone = cal.timeZone

        val compareToday = simpleDateFormat.parse(formatter.format(today))
        val compareSavedToday = simpleDateFormat.parse(formatter.format(savedToday))
        Log.d("isNextday()","compareToday: " + compareToday.toString() + " compareSavedToday: " + compareSavedToday.toString())

        Log.d("isNextday()",compareToday.toString() + " " + compareSavedToday.toString() + " " + (compareToday > compareSavedToday).toString() + " " + (compareToday == compareSavedToday).toString() + " " + (compareToday < compareSavedToday).toString())
        return compareToday > compareSavedToday
    }
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val token = FirebaseInstanceId.getInstance().token
        Log.d("FCM_Token", token)

        manboService = Intent(this, StepCheckService::class.java)
        receiver = PlayingReceiver()

        countText = findViewById(R.id.stepText) as TextView
        playingBtn = findViewById(R.id.btnStopService) as Button
        infoBtn = findViewById(R.id.status) as Button
        buddyBtn = findViewById(R.id.friends) as Button
        shopBtn = findViewById(R.id.shop) as Button
        outBtn = findViewById(R.id.logout) as Button

        database = FirebaseDatabase.getInstance().reference
        mAuth = FirebaseAuth.getInstance()
        val currentUser = mAuth.currentUser
        val userId = currentUser!!.email
        var index = userId!!.indexOf("@")
        path = userId.substring(0,index)

        val pointDB = FirebaseDatabase.getInstance().getReference("/user")
        pointDB.addListenerForSingleValueEvent(postListener)
        countText!!.text = point.toString()

        DBinfoRef = FirebaseDatabase.getInstance().getReference("/info/" + path)
        getDate()
        Log.d("onCreate()","today: " + today + " savedDate: " + savedDate)
        val dateDB = FirebaseDatabase.getInstance().getReference("/info/" + path)
        dateDB.addListenerForSingleValueEvent(dayCountListener)

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
                    database.child("info").child(path).child("dayMaxPt").setValue(dayMaxPt)
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
            intent.putExtra("dailyPt", (dailyPt.toString().toInt() + serviceData.toInt()).toString())
            if((dailyPt.toString().toInt() + serviceData.toInt()) > dayMaxPt.toString().toInt()) {
                intent.putExtra("dayMaxPt", (dailyPt.toString().toInt() + serviceData.toInt()).toString())
                database.child("info").child(path).child("dayMaxPt").setValue(dailyPt.toString().toInt() + serviceData.toInt())
            } else {
                intent.putExtra("dayMaxPt", dayMaxPt.toString())
            }
            intent.putExtra("dayCount",dayCount.toString())
            startActivity(intent)
        }

        buddyBtn!!.setOnClickListener{
            var intent : Intent = Intent(this, FriendsActivity::class.java)
            startActivity(intent)
        }

        shopBtn!!.setOnClickListener{
            var intent : Intent = Intent(this, ShopActivity::class.java)
            intent.putExtra("shoes",shoes)
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

    val dateListener = object : ValueEventListener{
        override fun onDataChange(p0: DataSnapshot?) {
            Log.d("dateListener","exists(): " + p0!!.exists())
            if(p0.exists()) {
                val cal = Calendar.getInstance()
                val formatter = DateFormat.getDateInstance()
                formatter.timeZone = cal.timeZone
                Log.d("dateListener","p0.value = " + p0.value.toString())
                val dateStr = p0.value.toString()
                savedDate = simpleDateFormat.parse(dateStr)
                Log.d("dateListener", "savedDate: " + savedDate.toString())
            } else {
                Log.d("dateListener", "today: " + today)
                DBinfoRef.child("today").setValue(simpleDateFormat.format(today))
            }
        }
        override fun onCancelled(p0: DatabaseError?) {

        }
    }

    val dailyPtListener = object : ValueEventListener {
        override fun onDataChange(p0: DataSnapshot?) {
            if(p0 != null) {
                if(p0.exists()) {
                    dailyPt = p0.value!!
                    if(dailyPt.toString().toInt() > dayMaxPt.toString().toInt()) dayMaxPt = dailyPt
                } else {
                    DBinfoRef.child("dailyPt").setValue(0)
                }
            }
        }

        override fun onCancelled(p0: DatabaseError?) {

        }
    }

    val dayMaxPtListener = object : ValueEventListener {
        override fun onDataChange(p0: DataSnapshot?) {
            if(p0 != null) {
                if(p0.exists()) {
                    dayMaxPt = p0.value!!
                } else {
                    DBinfoRef.child("dayMaxPt").setValue(1)
                }
            }
        }

        override fun onCancelled(p0: DatabaseError?) {
        }
    }

    val dayCountListener = object: ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot?) {
            for(snapshot in dataSnapshot!!.children) {
                if(snapshot.key == "startDay") {
                    date = snapshot.value!!

                    val now = System.currentTimeMillis()
                    val today = Date(now)

                    val cal = Calendar.getInstance()
                    val formatter : DateFormat = DateFormat.getDateInstance(DateFormat.MEDIUM)
                    formatter.timeZone = cal.timeZone

                    val compareToday = simpleDateFormat.parse(formatter.format(today))
                    val compareSavedToday = simpleDateFormat.parse(date.toString())

                    val calDate = compareToday.time - compareSavedToday.time
                    var calDateFinal = calDate / (24*60*60*1000)
                    calDateFinal = Math.abs(calDateFinal)

                    dayCount = calDateFinal.toInt() + 1
                    Log.d("datedate1111", calDateFinal.toString())
                    Log.d("datedatedate11111", compareToday.toString() + "  " + compareSavedToday.toString())
                }

                Log.d("FriendsActivity","ValueEventListener:" + point)

            }
        }
        override fun onCancelled(p0: DatabaseError?) {

        }
    }

    val ItemListener = object : ValueEventListener {
        override fun onDataChange(p0: DataSnapshot?) {

        }

        override fun onCancelled(p0: DatabaseError?) {

        }
    }

    private inner class PlayingReceiver : BroadcastReceiver() {

        override fun onReceive(context: Context, intent: Intent) {

            val pointDB = FirebaseDatabase.getInstance().getReference("/user")
            pointDB.addListenerForSingleValueEvent(postListener)

            DBinfoRef.child("dailyPt").addListenerForSingleValueEvent(dailyPtListener)

            Log.i("PlayignReceiver", "IN")
            serviceData = (intent.getStringExtra("stepService").toDouble() * shoesAbility!!).toInt().toString()
            countText!!.text = (serviceData.toInt() + point.toString().toInt()).toString()
            Log.i("test", "$serviceData $point $dailyPt")
        }
    }


}
