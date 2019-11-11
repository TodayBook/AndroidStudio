package com.example.todaybook

import android.icu.text.CaseMap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_read_or_read.*

class ReadOrRead : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    var database = FirebaseDatabase.getInstance().reference
    val cuser = FirebaseAuth.getInstance().currentUser
    fun EncodeString(title:String):String {
        return title.replace(".", " ")
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_read_or_read)
        val bookinfo by lazy { intent.extras!!["Info"] as BookInfo }
        println(bookinfo.title)
        bt_didbook.setOnClickListener {
            database.child("users").child(cuser?.uid!!).child("didBook").child(EncodeString(bookinfo.title)).setValue(bookDB(bookinfo.imageurl, bookinfo.author, bookinfo.pub))
            Toast.makeText(baseContext, "Success!!", Toast.LENGTH_SHORT).show()
        }
        bt_willbook.setOnClickListener {
            database.child("users").child(cuser?.uid!!).child("willBook").child(EncodeString(bookinfo.title)).setValue(bookDB(bookinfo.imageurl, bookinfo.author, bookinfo.pub))
            Toast.makeText(baseContext, "Success!!", Toast.LENGTH_SHORT).show()
        }
    }
}
