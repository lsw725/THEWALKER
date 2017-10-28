package com.example.kimhun.thewalker

import android.app.Activity
import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.Toast
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
        var friendsArray = ArrayList<String>()

        database = FirebaseDatabase.getInstance().reference

        val friendsListener = object : ValueEventListener{
            override fun onDataChange(dataSnapshot : DataSnapshot?) {
                friendsArray.clear()
                for(snapshot in dataSnapshot!!.children) {
                    var order = snapshot.key
                    friendsArray.add(order.toString())
                    Log.d("helpme",order.toString())
                    adapter.addItem(1,order.toString(),1)
                }
            }

            override fun onCancelled(dataSnapshot: DatabaseError?) {

            }
        }

        var currentFriendRef = FirebaseDatabase.getInstance().getReference("/friends/" + path)
        currentFriendRef.addListenerForSingleValueEvent(friendsListener)

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

            var alertDialogBuilder : AlertDialog.Builder = AlertDialog.Builder(this)

            var currentRef = FirebaseDatabase.getInstance().getReference("/user")
            currentRef.addListenerForSingleValueEvent(postListener)
            for(user in userArray) {
                if (friendsID.text.toString() == path) {
                    Log.w("FriendsActivity", "Don't add self for friend")
                    alertDialogBuilder.setTitle("친 구 추 가")
                    alertDialogBuilder.setMessage("자신을 친구추가 할 수 없습니다.")
                            .setCancelable(true)
                    alertDialogBuilder.show()
                    friendsID.setText("")
                    break
                } else if(user == friendsID.text.toString()) {
                    database.child("friends").child(path).child(friendsID.text.toString()).setValue(friendsID.text.toString())
                    Toast.makeText(this, "친구추가가 완료됐습니다.",Toast.LENGTH_SHORT)
                    friendsID.setText("")
                    break
                } else {
                    if(user == userArray[userArray.size - 1]){
                        alertDialogBuilder.setTitle("친 구 추 가")
                        alertDialogBuilder.setMessage("해당 아이디의 사용자가 존재하지않습니다.")
                                .setCancelable(true)
                        alertDialogBuilder.show()
                    }
                    Log.w("FriendsActivity","failed order")
                    friendsID.setText("")
                }
            }
        }
        friendsListView.adapter = adapter
    }
}
