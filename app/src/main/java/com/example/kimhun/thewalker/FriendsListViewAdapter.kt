package com.example.kimhun.thewalker

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView

/**
 * Created by Hp on 2017-09-13.
 */
class FriendsListViewAdapter() : BaseAdapter() {
    private var friendsList = ArrayList<FriendsListItem>()

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

        var friend = friendsList[position]

        // 위젯에 데이터 반영
        rankTextView.text = friend.getRank()!!.toString()
        nameTextView.text = friend.getFriendName()!!.toString()
        pointTextView.text = friend.getWeeklyPoint()!!.toString()

        return view
    }

    override fun getItem(position: Int): Any {
        return friendsList[position]
    }

    override fun getItemId(position: Int): Long {
        return position as Long
    }

    fun addItem(rank : Int?, name : String, point : Int?) {
        val item = FriendsListItem()

        item.setRank(rank)
        item.setFriendName(name)
        item.setWeeklyPoint(point)

        friendsList.add(item)
    }
}