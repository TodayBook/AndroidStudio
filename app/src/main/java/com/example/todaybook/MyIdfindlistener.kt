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
        if(cuser!=null) {
            val myIdlistener = object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {

                    for (snapshot in dataSnapshot.children) {
                        var key: String = snapshot.key.toString()
                        var value = snapshot.value.toString()
                        if (key == "UserId") {
                            var Myid = value
                            database.child("users").child(friendUid).child("follower").child(cuser.uid).setValue(Myid)
                            var fcmPush=FcmPush()
                            var message = Myid+"님이 회원님을 팔로우 했습니다."
                            fcmPush.sendMessage(friendUid, "", message)
                            print("sending")
                        }
                    }
                }
                override fun onCancelled(databaseError: DatabaseError) {
                }
            }
            database.child("users").child(cuser.uid).addValueEventListener(myIdlistener)
        }
    }
}