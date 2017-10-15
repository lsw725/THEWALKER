package com.example.kimhun.thewalker

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
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

        val pointDB = FirebaseDatabase.getInstance().getReference("/user")
        pointDB.addListenerForSingleValueEvent(postListener)


        mAuth = FirebaseAuth.getInstance()
        val currentUser = mAuth.currentUser
        val userId = currentUser!!.email
        var index = userId!!.indexOf("@")
        path = userId.substring(0,index)

        var serviceData : String = intent.getStringExtra("point")

        pointText = findViewById(R.id.point) as TextView?
        pointText!!.text = serviceData
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
