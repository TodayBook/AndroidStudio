package com.example.todaybook

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MyIdfindlistener {
    var database = FirebaseDatabase.getInstance().reference
    val cuser = FirebaseAuth.getInstance().currentUser
    fun MyIdfind(friendUid:String){
        println("ffl")
        if(cuser!=null) {
            val myIdlistener = object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {

                    for (snapshot in dataSnapshot.children) {
                        var key: String = snapshot.key.toString()
                        var value = snapshot.value.toString()
                        if (key == "UserId") {
                            var Myid = value
                            println("Myid  " + Myid)
                            database.child("users").child(friendUid).child("follower").child(cuser.uid).setValue(Myid)
                        }
                    }
                }
                override fun onCancelled(databaseError: DatabaseError) {
                    println("no friend")
                }
            }
            database.child("users").child(cuser.uid).addValueEventListener(myIdlistener)
        }
    }
}