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
    var friendUid:String?=null
    fun friendfind(friendId:String){
        var res=false
        if(cuser!=null) {
            val friendfindlistener = object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    for (snapshot in dataSnapshot.children) {
                        var key: String = snapshot.key.toString()
                        var value = snapshot.value.toString()
                        if (key == "uid") {
                            friendUid = value
                            database.child("users").child(cuser.uid).child("following").child(friendUid!!).setValue(friendId)
                            val mil=MyIdfindlistener()
                            mil.MyIdfind(friendUid.toString())
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
}