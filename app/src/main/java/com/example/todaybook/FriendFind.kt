package com.example.todaybook

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_friend_find.*

class FriendFind : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    var database = FirebaseDatabase.getInstance().reference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_friend_find)
        var friendId:String?=null
        bt_search.setOnClickListener {
            friendId=FriendId.text.toString()
        }
        val friendfindlistener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (snapshot in dataSnapshot.children) {
                    var key : String = snapshot.key.toString()
                    var value = snapshot.value.toString()
                    database.child("users").child(auth.uid!!).child("friends").child(key).setValue(value)
                    Toast.makeText(baseContext, "Added!!", Toast.LENGTH_SHORT).show()
                    println("friendUID"+value)
                    if(key==friendId)break
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {
                Log.w("FFFFFF", "loadPost:onCancelled", databaseError.toException())
            }
        }
        database.child("UserId").addValueEventListener(friendfindlistener)
    }
}
