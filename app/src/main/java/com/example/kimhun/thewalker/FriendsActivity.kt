package com.example.kimhun.thewalker

import android.app.Activity
import android.os.Bundle
import android.widget.ListView

class FriendsActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_friends)

        var friendsListView = findViewById(R.id.friendsListView) as ListView
        var adapter = FriendsListViewAdapter()

        friendsListView.adapter = adapter

        adapter.addItem(1,"KIM",1555123)
        adapter.addItem(2,"Lee",12323)
        adapter.addItem(3,"Park",123)
    }
}
