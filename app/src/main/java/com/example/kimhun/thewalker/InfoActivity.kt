package com.example.kimhun.thewalker

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class InfoActivity : Activity() {
    private var pointText : TextView? = null
    private lateinit var mAuth: FirebaseAuth
    private lateinit var path:String
    private lateinit var tallEdit : EditText
    private lateinit var weightEdit : EditText
    private var point:Any = 0
    private var today_dist : Double = 0.0
    private var total_dist : Double = 0.0
    private var tall : Double = 0.0
    private var weight : Double = 0.0


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

        tallEdit = findViewById(R.id.tall_edit) as EditText
        weightEdit = findViewById(R.id.weight_edit) as EditText

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
        dailyCalorie!!.text = (0.00.toString() + " kcal")
        val totalCalorie = findViewById(R.id.total_calorie) as TextView?
        totalCalorie!!.text = (0.00.toString() + " kcal")

        val calculBtn = findViewById(R.id.cal_btn) as Button
        calculBtn.setOnClickListener {
            if(tallEdit.text.toString() == "" || weightEdit.text.toString() == "") {
                Toast.makeText(this, "키와 몸무게를 입력해주세요", Toast.LENGTH_SHORT).show()
            } else {
                tall = tallEdit.text.toString().toDouble()
                weight = weightEdit.text.toString().toDouble()
                today_dist = (tall * 0.37) * (dailyData.toDouble() /10)
                total_dist = (tall * 0.37) * (serviceData.toDouble() /10)
                val calPerMile :Double = 3.7103 + 0.2678 * weight + (0.0359*(weight*60*0.0006213)*2)*weight
                val todayCalorieStr = String.format("%.2f",today_dist * calPerMile / 1000.0)
                val totalCalorieStr = String.format("%.2f",total_dist * calPerMile / 1000.0)

                dailyCalorie!!.text = (todayCalorieStr + " kcal")
                totalCalorie!!.text = (totalCalorieStr + " kcal")
            }
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
}
