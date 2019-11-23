package com.example.todaybook

import android.icu.text.CaseMap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
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
        val bookinfo by lazy { intent.extras!!["Info"] as BookInfo2 }

        val bookimage = findViewById<ImageView>(R.id.thumbnail)
        val booktitle = findViewById<TextView>(R.id.detailtitle)
        val bookauthor =findViewById<TextView>(R.id.detailauthor)
        val bookpublisher =findViewById<TextView>(R.id.detailpublisher)
        val bookcontents =findViewById<TextView>(R.id.detailcontents)

        Glide.with(this).load(bookinfo.imageurl).into(bookimage)

        booktitle.text = bookinfo.title
        bookpublisher.text =  bookinfo.pub
        bookauthor.text =  bookinfo.author
        bookcontents.text =  bookinfo.contents

        println(bookinfo.title)
        var count=0
        bt_didbook.setOnClickListener {
            database.child("users").child(cuser!!.uid).child("didBook").child(EncodeString(bookinfo.title)).setValue(bookDB(bookinfo.imageurl, bookinfo.author, bookinfo.pub))
            var bcl=bookcntlistener()
            //bcl.bookcnt("didbook",EncodeString(bookinfo.title))
            Toast.makeText(baseContext, "Success!!", Toast.LENGTH_SHORT).show()
        }
        bt_willbook.setOnClickListener {
            database.child("users").child(cuser!!.uid!!).child("willBook").child(EncodeString(bookinfo.title)).setValue(bookDB(bookinfo.imageurl, bookinfo.author, bookinfo.pub))
            var bcl=bookcntlistener()
            //bcl.bookcnt("willbook",EncodeString(bookinfo.title))
            Toast.makeText(baseContext, "Success!!", Toast.LENGTH_SHORT).show()
        }
    }
}
