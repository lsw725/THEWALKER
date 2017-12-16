package com.example.kimhun.thewalker

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView

class ShopActivity : AppCompatActivity() {

    private lateinit var moneyText : TextView
    private lateinit var shoesImage : ImageView
    private lateinit var shoesNameText : TextView
    private lateinit var shoesCostText : TextView
    private lateinit var shoesContextText : TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shop)

        moneyText = findViewById(R.id.money) as TextView
        shoesImage = findViewById(R.id.now_shoes_img) as ImageView
        shoesNameText = findViewById(R.id.now_shoes_name) as TextView
        shoesCostText = findViewById(R.id.now_shoes_cost) as TextView
        shoesContextText = findViewById(R.id.now_shoes_context) as TextView

        moneyText.text = "5000 개"
        shoesImage.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.shoes))
        shoesNameText.text = "맨 발"
        shoesCostText.text = 0.toString()
        shoesContextText.text = "신발이 없다... ㅠㅠ [걸음당 +10pt]"

        val itemsList = findViewById(R.id.items_list) as ListView
        val adapter = ItemsListViewAdapter()

        itemsList.adapter = adapter

        adapter.addItem(ContextCompat.getDrawable(this,R.drawable.shoes),"맨 발",0,"신발이 없다... ㅠㅠ [걸음당 +10pt]")
        adapter.addItem(ContextCompat.getDrawable(this,R.drawable.shoes),"슬리퍼",10000,"슬리퍼가 더 느릴수도?...비싼건 묻지말자 [걸음당 +20pt]")
        adapter.addItem(ContextCompat.getDrawable(this,R.drawable.shoes),"고무신",30000,"홍길동이 신고다녔다.ㅋㅋㅋ [걸음당 +30pt]")
    }
}
