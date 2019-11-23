package com.example.todaybook

import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.lang.Exception

class bookcntlistener {
    var database = FirebaseDatabase.getInstance().reference
    var count=1
    fun bookcnt(type:String,title:String){
        try{
        val bookcntlistener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (snapshot in dataSnapshot.children) {
                    var key : String = snapshot.key.toString()
                    var value = snapshot.value.toString()
                    if(key=="cnt"){
                        count=value.toInt()
                        count=count+1;
                        database.child("Ranking").child(type).child(title).child("cnt").setValue(count)
                    }
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {
                Log.w("FFFFFF", "loadPost:onCancelled", databaseError.toException())
            }
        }
        database.child("Ranking").child(type).child(title).addValueEventListener(bookcntlistener)
        }
        catch(execption:Exception){
            database.child("Ranking").child(type).child(title).child("cnt").setValue(1)
        }
    }

}