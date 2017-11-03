package com.example.kimhun.thewalker

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageButton
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.*



/**
 * Created by Hp on 2017-09-13.
 */
class FriendsListViewAdapter() : BaseAdapter() {
    private var friendsList = ArrayList<FriendsListItem>()

    private lateinit var mAuth: FirebaseAuth

    override fun getCount() = friendsList.size

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val context = parent?.context
        var view = convertView

        if(convertView == null) {
            var inflater = context?.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            view = inflater.inflate(R.layout.friendslist_item, parent, false);
        }

        // 화면에 표시될 view로부터 위젯에 대한 참조 획득
        var rankTextView = view?.findViewById(R.id.rankView) as TextView
        var nameTextView = view.findViewById(R.id.nameView) as TextView
        var pointTextView = view.findViewById(R.id.pointView) as TextView
        var removeButton = view.findViewById(R.id.removeBtn) as ImageButton

        var friend = friendsList[position]

        // 위젯에 데이터 반영
        rankTextView.text = (position + 1).toString()
        nameTextView.text = friend.getFriendName()!!.toString()
        pointTextView.text = friend.getWeeklyPoint()!!.toString()

        removeButton.setOnClickListener{
            mAuth = FirebaseAuth.getInstance()
            val currentUser = mAuth.currentUser
            val userId = currentUser!!.email
            var index = userId!!.indexOf("@")
            var path = userId.substring(0,index)

            var alertDialogBuilder : AlertDialog.Builder = AlertDialog.Builder(view!!.context)
            alertDialogBuilder.setMessage("친구를 삭제 하시겠습니까?").setCancelable(false).setPositiveButton("확인",
                    DialogInterface.OnClickListener { dialog, which ->
                        FirebaseDatabase.getInstance().getReference("/friends/" + path + "/" + friendsList[position].getFriendName()).removeValue()
                        var intent = Intent(view!!.context, FriendsActivity::class.java)
                        view!!.context.startActivity(intent)
                        (context as Activity).finish()

                    }).setNegativeButton("취소",
                    DialogInterface.OnClickListener { dialog, which ->

                    })
            val alert = alertDialogBuilder.create()
            alert.show()

        }

        return view
    }

    override fun getItem(position: Int): Any {
        return friendsList[position]
    }

    override fun getItemId(position: Int): Long {
        return position as Long
    }

    fun addItem(name : String) {
        val item = FriendsListItem()

        item.setFriendName(name)

        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot?) {
                for (snapshot in dataSnapshot!!.children) {
                    var order = snapshot.key
                    Log.d("FriendsActivity", "Order :" + order.toString())
                        if (order == item.getFriendName()) {
                            item.setWeeklyPoint(snapshot.value.toString().toInt())
                            friendsList.add(item)

                            val cmpAsc = object : Comparator<FriendsListItem> {

                                override fun compare(p0: FriendsListItem?, p1: FriendsListItem?): Int {
                                    if(p0!!.getWeeklyPoint()!! > p1!!.getWeeklyPoint()!!){
                                        return 1
                                    } else {
                                        return -1
                                    }

                                }
                            }

                            Collections.sort(friendsList, cmpAsc)
                            Collections.reverse(friendsList)

                    }
                }
            }

                override fun onCancelled(dataSnapshot: DatabaseError?) {

                }
            }

        var currentRef = FirebaseDatabase.getInstance().getReference("/user")
        currentRef.addListenerForSingleValueEvent(postListener)



    }

}