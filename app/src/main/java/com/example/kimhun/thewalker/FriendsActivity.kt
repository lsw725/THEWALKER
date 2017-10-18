package com.example.kimhun.thewalker

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class FriendsActivity : Activity() {

    private lateinit var friendsBtn : Button
    private lateinit var friendsID : EditText
    private lateinit var database : DatabaseReference
    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_friends)

        var friendsListView = findViewById(R.id.friendsListView) as ListView
        var adapter = FriendsListViewAdapter()
        friendsID = findViewById(R.id.add_friends) as EditText

        mAuth = FirebaseAuth.getInstance()
        val currentUser = mAuth.currentUser
        val userId = currentUser!!.email
        var index = userId!!.indexOf("@")
        var path = userId.substring(0,index)
        var userArray = ArrayList<String>()

        database = FirebaseDatabase.getInstance().reference

        friendsListView.adapter = adapter

        adapter.addItem(1,"KIM",1555123)
        adapter.addItem(2,"Lee",12323)
        adapter.addItem(3,"Park",123)

        friendsBtn = findViewById(R.id.addFriend) as Button
        friendsBtn.setOnClickListener{
            val postListener = object : ValueEventListener {
                override fun onDataChange(dataSnapshot : DataSnapshot?) {
                    userArray.clear()
                    for(snapshot in dataSnapshot!!.children) {
                        var order = snapshot.key
                        Log.d("FriendsActivity","Order :" + order.toString())
                        userArray.add(order.toString())
                    }
                }

                override fun onCancelled(dataSnapshot: DatabaseError?) {

                }
            }
            for(ite in 1..userArray.size) {
                Log.d("FriendsActivity", "userArray:" + userArray[ite-1])
            }
            var currentRef = FirebaseDatabase.getInstance().getReference("/user")
            currentRef.addListenerForSingleValueEvent(postListener)
            for(user in userArray) {
                if (user == friendsID.text.toString()) {
                    database.child("friends").child(path).child(friendsID.text.toString()).setValue(friendsID.text.toString())
                    friendsID.setText("")
                } else {
                    Log.w("FriendsActivity","failed order")
                }
            }
        }
    }
}
