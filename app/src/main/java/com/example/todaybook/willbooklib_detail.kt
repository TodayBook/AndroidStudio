package com.example.todaybook

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ListView
import android.widget.Toast
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
            intent.putExtra("result", bookinfo.title)
            setResult(Activity.RESULT_OK, intent)
            finish()
        }
        bt_movedidbook.setOnClickListener {
            database.child("users").child(cuser!!.uid).child("willBook").child(bookinfo.title).removeValue()
            database.child("users").child(cuser!!.uid).child("didBook").child(bookinfo.title).setValue(bookinfo)
            val intent = Intent()
            setResult(Activity.RESULT_OK, intent)
            finish()
        }
        var guestId = ArrayList<String>()
        guestId.clear()
        var guestUid = ArrayList<String>()
        guestUid.clear()
        val list : ListView = findViewById(R.id.didbookpeople)
        val adapter = friendlistAdapter(this,guestId,guestUid)
        list.adapter = adapter
        val bookreadelistener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (snapshot in dataSnapshot.children) {
                    var key : String = snapshot.key.toString()
                    var value = snapshot.value.toString()
                    guestUid.add(key)
                    guestId.add(value)
                }
                adapter.notifyDataSetChanged()
            }
            override fun onCancelled(databaseError: DatabaseError) {
                Log.w("FFFFFF", "loadPost:onCancelled", databaseError.toException())
            }
        }
        database.child("Books").child(bookinfo.title).addValueEventListener(bookreadelistener)

        list.setOnItemClickListener{parant,itemView,position,id->
            val myprivatelistener = object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    for (snapshot in dataSnapshot.children) {
                        var key: String = snapshot.key.toString()
                        println(key)
                        var value = snapshot.value.toString()
                        if (key == "private") {
                            if(value=="false"){
                                val detailIntent = Intent(baseContext, Guestlib::class.java)
                                detailIntent.putExtra("GuestUid",guestUid[position])
                                startActivityForResult(detailIntent,1)
                            }
                            else{
                                Toast.makeText(baseContext, "비공개입니다", Toast.LENGTH_SHORT).show()
                            }
                            break
                        }
                    }
                }
                override fun onCancelled(databaseError: DatabaseError) {
                }
            }
            database.child("users").child(guestUid[position]).addValueEventListener(myprivatelistener)
        }
    }

}
