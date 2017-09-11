package com.example.kimhun.thewalker

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.TextView

class InfoActivity : Activity() {
    private var pointText : TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_info)

        var intent : Intent = getIntent()
        var point : String = intent.getStringExtra("point")

        pointText = findViewById(R.id.point) as TextView?
        pointText!!.setText(point)
    }
}
