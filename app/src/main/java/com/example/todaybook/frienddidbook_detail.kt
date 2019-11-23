package com.example.todaybook

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_frienddidbook_detail.*

class frienddidbook_detail : AppCompatActivity() {
    var database = FirebaseDatabase.getInstance().reference
    val cuser = FirebaseAuth.getInstance().currentUser
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_frienddidbook_detail)
        val bookinfo by lazy{intent.extras!!["Info"] as FriendBookInfo}
        titleView.text = bookinfo.title
        authorView.text = bookinfo.author
        pubView.text = bookinfo.pub
        Glide.with(coverimage).load(bookinfo.imageurl).into(coverimage)

        val commentslistener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (snapshot in dataSnapshot.children) {
                    var key : String = snapshot.key.toString()
                    var value = snapshot.value.toString()
                    if(key=="comments"){
                        comments.text=value
                        break
                    }
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {
                Log.w("FFFFFF", "loadPost:onCancelled", databaseError.toException())
            }
        }
        database.child("users").child(bookinfo.FriendUid).child("didBook").child(bookinfo.title).addValueEventListener(commentslistener)
    }
}
