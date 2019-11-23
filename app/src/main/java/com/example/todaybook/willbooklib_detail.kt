package com.example.todaybook

import android.app.Activity
import android.content.Intent
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
import kotlinx.android.synthetic.main.activity_willbooklib_detail.*


class willbooklib_detail : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    var database = FirebaseDatabase.getInstance().reference
    val cuser = FirebaseAuth.getInstance().currentUser
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_willbooklib_detail)
        val bookinfo by lazy{intent.extras!!["Info"] as BookInfo}
        titleView.text = bookinfo.title
        authorView.text = bookinfo.author
        pubView.text = bookinfo.pub
        Glide.with(coverimage).load(bookinfo.imageurl).into(coverimage)

        bt_delete.setOnClickListener {
            database.child("users").child(cuser!!.uid).child("willBook").child(bookinfo.title).removeValue()
            val intent = Intent()
            setResult(Activity.RESULT_OK, intent)
            finish()
        }
        var friendsId = ArrayList<String>()
        friendsId.clear()
        var friendsUid = ArrayList<String>()
        friendsUid.clear()
        val list : ListView = findViewById(R.id.didbookpeople)
        val adapter = friendlistAdapter(this,friendsId)
        list.adapter = adapter
        val bookreadelistener = object : ValueEventListener {
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
        database.child("Books").child(bookinfo.title).addValueEventListener(bookreadelistener)

        list.setOnItemClickListener{parant,itemView,position,id->
            val detailIntent = Intent(this, FriendlibActivity::class.java)
            detailIntent.putExtra("FriendUid",friendsUid[position])
            startActivityForResult(detailIntent,1)
        }
    }
}
