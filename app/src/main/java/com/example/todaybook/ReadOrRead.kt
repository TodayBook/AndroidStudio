package com.example.todaybook

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_read_or_read.*

class ReadOrRead : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    var database = FirebaseDatabase.getInstance().reference
    val cuser = FirebaseAuth.getInstance().currentUser
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_read_or_read)
        val bookinfo by lazy { intent.extras!!["Info"] as BookInfo }
        bt_didbook.setOnClickListener {
            database.child("users").child(cuser?.uid!!).child("didBook").child(bookinfo.title)
                .setValue(bookDB(bookinfo.imageurl, bookinfo.author, bookinfo.pub))
        }
        bt_willbook.setOnClickListener {
            database.child("users").child(cuser?.uid!!).child("willBook").child(bookinfo.title)
                .setValue(bookDB(bookinfo.imageurl, bookinfo.author, bookinfo.pub))
        }
    }
}
