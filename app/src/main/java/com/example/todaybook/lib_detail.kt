package com.example.todaybook

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_lib_detail.*

class lib_detail : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lib_detail)
        val BookInfo by lazy{intent.extras!!["Info"] as parcel_bookinfo}

        titleView.text = BookInfo.title
        authorView.text = BookInfo.author
        pubView.text = BookInfo.pub
        Glide.with(coverimage).load(BookInfo.imageurl).into(coverimage)
    }
}
