package com.example.kimhun.thewalker

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class InfoActivity : Activity() {
    private var pointText : TextView? = null
    private lateinit var mAuth: FirebaseAuth
    private lateinit var path:String
    private var point:Any = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_info)

        var intent : Intent = getIntent()

        mAuth = FirebaseAuth.getInstance()
        val currentUser = mAuth.currentUser
        val userId = currentUser!!.email
        var index = userId!!.indexOf("@")
        path = userId.substring(0,index)

        val pointDB = FirebaseDatabase.getInstance().getReference("/user")
        pointDB.addListenerForSingleValueEvent(postListener)

        val serviceData : String = intent.getStringExtra("point")
        val dailyData = intent.getStringExtra("dailyPt")
        val dayMaxData = intent.getStringExtra("dayMaxPt")
        val dayCountData = intent.getStringExtra("dayCount")

        pointText = findViewById(R.id.point) as TextView?
        pointText!!.text = serviceData

        val dailyPoint = findViewById(R.id.now_point) as TextView?
        dailyPoint!!.text = (dailyData + "pt")
        val dailyStep = findViewById(R.id.now_step) as TextView?
        dailyStep!!.text = ((dailyData.toInt() / 10).toString() + "걸음")

        val dayMaxPt = findViewById(R.id.day_max) as TextView?
        dayMaxPt!!.text = (dayMaxData + "pt")

        val totalPoint = findViewById(R.id.total_point) as TextView?
        totalPoint!!.text = (serviceData + " pt")
        val totalStep = findViewById(R.id.total_step) as TextView?
        totalStep!!.text = ((serviceData.toInt() / 10).toString() + " 걸음")
        val dayAverage = findViewById(R.id.day_aver) as TextView?
        dayAverage!!.text = ((serviceData.toInt() / dayCountData.toInt()).toString() + " pt")

        val dailyCalorie = findViewById(R.id.now_calorie) as TextView?
        dailyCalorie!!.text = 0.toString()
        val totalCalorie = findViewById(R.id.total_calorie) as TextView?
        totalCalorie!!.text = 0.toString()
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
}
