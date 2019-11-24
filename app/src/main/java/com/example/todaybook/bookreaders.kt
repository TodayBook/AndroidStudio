package com.example.todaybook

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class bookreaders {
    var database = FirebaseDatabase.getInstance().reference
    val cuser = FirebaseAuth.getInstance().currentUser
    fun didbookread(title:String){
        val myIdlistener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (snapshot in dataSnapshot.children) {
                    var key: String = snapshot.key.toString()
                    var value = snapshot.value.toString()
                    if (key == "UserId") {
                        var Myid = value
                        database.child("Books").child(title).child(cuser!!.uid).setValue(Myid)
                    }
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {
            }
        }
        database.child("users").child(cuser!!.uid).addValueEventListener(myIdlistener)
    }
}