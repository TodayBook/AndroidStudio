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
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_lib_detail.*

class lib_detail : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lib_detail)
        val bookinfo by lazy{intent.extras!!["Info"] as BookInfo}
        titleView.text = bookinfo.title
        authorView.text = bookinfo.author
        pubView.text = bookinfo.pub
        Glide.with(coverimage).load(bookinfo.imageurl).into(coverimage)
        bt_complete.setOnClickListener{
            val database = FirebaseDatabase.getInstance().reference
            val UserId="sdfsdf"
            //database.child("users").child(UserId).child("Books").child(bookinfo.title).setValue(comments.text)
        }
    }
}
