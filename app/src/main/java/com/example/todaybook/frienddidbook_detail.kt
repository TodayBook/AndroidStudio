package com.example.todaybook

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ListView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_frienddidbook_detail.*
import java.util.*

class frienddidbook_detail : AppCompatActivity() {
    var database = FirebaseDatabase.getInstance().reference
    val cuser = FirebaseAuth.getInstance().currentUser
    var title:String?=null
    var frienduid:String?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_frienddidbook_detail)
        val bookinfo by lazy{intent.extras!!["Info"] as FriendBookInfo}
        titleView.text = bookinfo.title
        authorView.text = bookinfo.author
        pubView.text = bookinfo.pub
        Glide.with(coverimage).load(bookinfo.imageurl).into(coverimage)
        title=bookinfo.title
        frienduid=bookinfo.FriendUid
        val commentslistener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (snapshot in dataSnapshot.children) {
                    var key : String = snapshot.key.toString()
                    var value = snapshot.value.toString()
                    if(key=="review"){
                        review.text=value
                        break
                    }
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {
                Log.w("FFFFFF", "loadPost:onCancelled", databaseError.toException())
            }
        }
        database.child("users").child(bookinfo.FriendUid).child("didBook").child(bookinfo.title).addValueEventListener(commentslistener)

        commentsload()
        bt_comments_input.setOnClickListener {
            val comment=comments_input.text.toString()
            val comment_writer=cuser!!.uid
            val comment_time = Calendar.getInstance().getTime()
            database.child("users").child(frienduid!!).child("didBook").child(bookinfo.title).child("comment").child(comment_time.toString()).setValue(comment_item(comment,comment_writer))
            commentsload()
        }
    }
    fun commentsload(){
        var comment_time_array = ArrayList<String>()
        comment_time_array.clear()
        var comment_array = ArrayList<String>()
        comment_array.clear()
        var comment_writer_array = ArrayList<String>()
        comment_writer_array.clear()
        val comments_list: ListView = findViewById(R.id.comments_list)
        val comments_adapter = commentlistAdapter(this, comment_time_array, comment_array,comment_writer_array)
        comments_list.adapter = comments_adapter
        val commentsreadlistener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (snapshot in dataSnapshot.children) {
                    var key: String = snapshot.key.toString()
                    var value = snapshot.getValue(comment_item::class.java)
                    comment_time_array.add(key)
                    comment_array.add(value!!.comment_item)
                    comment_writer_array.add(value!!.writer)
                }
                comments_adapter.notifyDataSetChanged()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.w("FFFFFF", "loadPost:onCancelled", databaseError.toException())
            }
        }
        database.child("users").child(frienduid!!).child("didBook").child(title!!).child("comment").addValueEventListener(commentsreadlistener)
    }
}
