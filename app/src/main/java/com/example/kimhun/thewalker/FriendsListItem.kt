package com.example.kimhun.thewalker

/**
 * Created by Hp on 2017-09-13.
 */
class FriendsListItem() {
    private var rank : Int? = null
    private var friendName : String = ""
    private var point : Int? = null

    fun setRank( rank : Int? ) { this.rank = rank }
    fun setFriendName( name : String ) { this.friendName = name }
    fun setWeeklyPoint( point : Int? ) { this.point = point }

    fun getRank() : Int? = this.rank
    fun getFriendName() : String = this.friendName
    fun getWeeklyPoint() : Int?  = this.point
}