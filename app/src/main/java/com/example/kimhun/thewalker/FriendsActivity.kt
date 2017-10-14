package com.example.kimhun.thewalker

import android.app.Activity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

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


        database = FirebaseDatabase.getInstance().reference

        friendsListView.adapter = adapter

        adapter.addItem(1,"KIM",1555123)
        adapter.addItem(2,"Lee",12323)
        adapter.addItem(3,"Park",123)

        friendsBtn = findViewById(R.id.addFriend) as Button
        friendsBtn.setOnClickListener{
            database.child("friends").child(path).push().setValue(friendsID.text.toString())
            friendsID.setText("")
        }
    }
}
