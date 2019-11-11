package com.example.todaybook

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ListView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_freindlist.*

class FreindlstActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    var database = FirebaseDatabase.getInstance().reference
    val cuser = FirebaseAuth.getInstance().currentUser
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_freindlist)
        val list : ListView = findViewById(R.id.friendlist)

        val namelistener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (snapshot in dataSnapshot.children) {
                    var key : String = snapshot.key.toString()
                    var value = snapshot.value.toString()
                    nametext.text=value+"님의 친구"
                    if(key=="UserId")break
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {
                Log.w("FFFFFF", "loadPost:onCancelled", databaseError.toException())
            }
        }
        database.child("users").child(cuser!!.uid).addValueEventListener(namelistener)

        var friendsId = ArrayList<String>()
        friendsId.clear()
        var friendsUid = ArrayList<String>()
        friendsUid.clear()

        val adapter = friendlistAdapter(this,friendsId)
        list.adapter = adapter

        val friendlistener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (snapshot in dataSnapshot.children) {
                    var key : String = snapshot.key.toString()
                    var value = snapshot.value.toString()
                    friendsUid.add(key)
                    friendsId.add(value)
                }
                adapter.notifyDataSetChanged()
            }
            override fun onCancelled(databaseError: DatabaseError) {
                Log.w("FFFFFF", "loadPost:onCancelled", databaseError.toException())
            }
        }
        database.child("users").child(cuser!!.uid).child("friends").addValueEventListener(friendlistener)

        list.setOnItemClickListener{parant,itemView,position,id->
            Toast.makeText(baseContext, "success", Toast.LENGTH_SHORT).show()
            val detailIntent = Intent(this,FriendlibActivity::class.java)
            detailIntent.putExtra("FriendUid",friendsUid[position])
            startActivityForResult(detailIntent,1)
        }

        bt_friendfind.setOnClickListener{
            val loginIntent = Intent(this,FriendFind::class.java)
            startActivityForResult(loginIntent,1)
        }
    }
}
