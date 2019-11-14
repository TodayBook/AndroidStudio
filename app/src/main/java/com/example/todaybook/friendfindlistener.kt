package com.example.todaybook

import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class friendfindlistener{
    var database = FirebaseDatabase.getInstance().reference
    val cuser = FirebaseAuth.getInstance().currentUser
    fun friendfind(friendId:String){
        println("ffl")
        var friendUid:String?=null
        val friendfindlistener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                for (snapshot in dataSnapshot.children) {
                    var key : String = snapshot.key.toString()
                    var value = snapshot.value.toString()
                    println("key"+key)
                    println("value"+value)
                    if(key=="uid"){
                        friendUid=value
                        println("after"+friendUid)
                        database.child("users").child(cuser!!.uid).child("friends").child(friendUid!!).setValue(friendId)
                    }
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {
                println("no friend")
            }
        }
        database.child("UserId").child(friendId).addValueEventListener(friendfindlistener)
    }
}