package com.example.todaybook

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_guestlib.*

class Guestlib : AppCompatActivity() {
    var database = FirebaseDatabase.getInstance().reference
    val cuser = FirebaseAuth.getInstance().currentUser
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_guestlib)
        val GuestUid = intent.extras!!["GuestUid"]
        var GuestId = intent.extras!!["GuestId"]
        println(GuestUid)
        println(GuestId)
        guest_name.text=GuestId.toString()+"님의 도서관"

        var readList = ArrayList<ImageDataModel>()
        readList.clear()

        val willreadList = ArrayList<ImageDataModel>()
        willreadList.clear()

        val rbAdapter = ViewAdapter(this, readList) { imageDataModel ->
            val detailIntent = Intent(this, frienddidbook_detail::class.java)
            detailIntent.putExtra("Info",FriendBookInfo(GuestUid.toString(),imageDataModel.url,imageDataModel.title,imageDataModel.author,imageDataModel.pub))
            startActivityForResult(detailIntent, 1)
        }
        val wbAdapter = ViewAdapter(this, willreadList) { imageDataModel ->
            val detailIntent = Intent(this, friendwillbook_detail::class.java)
            detailIntent.putExtra("Info",FriendBookInfo(GuestUid.toString(),imageDataModel.url,imageDataModel.title,imageDataModel.author,imageDataModel.pub))
            startActivityForResult(detailIntent, 1)
        }
        guest_didbook.layoutManager = LinearLayoutManager(this, RecyclerView.HORIZONTAL, false)
        guest_willbook.layoutManager = LinearLayoutManager(this, RecyclerView.HORIZONTAL, false)

        guest_didbook.adapter = rbAdapter
        guest_willbook.adapter = wbAdapter

        val readbooklistener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (snapshot in dataSnapshot.children) {
                    var key : String = snapshot.key.toString()
                    var value = snapshot.getValue(newDB::class.java)
                    readList.add(ImageDataModel(value!!.imageurl,key,value.author,value.publisher,value.cameraimageurl))
                }
                rbAdapter.notifyDataSetChanged()
            }
            override fun onCancelled(databaseError: DatabaseError) {
                Log.w("FFFFFF", "loadPost:onCancelled", databaseError.toException())
            }
        }
        database.child("users").child(GuestUid.toString()).child("didBook").addValueEventListener(readbooklistener)

        val willbooklistener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (snapshot in dataSnapshot.children) {
                    var key : String = snapshot.key.toString()
                    var value = snapshot.getValue(newDB::class.java)
                    willreadList.add(ImageDataModel(value!!.imageurl,key,value.author,value.publisher,value.cameraimageurl))
                }
                wbAdapter.notifyDataSetChanged()
            }
            override fun onCancelled(databaseError: DatabaseError) {
                Log.w("FFFFFF", "loadPost:onCancelled", databaseError.toException())
            }
        }
        database.child("users").child(GuestUid.toString()).child("willBook").addValueEventListener(willbooklistener)

        bt_follow.setOnClickListener {
            val myIdlistener = object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    for (snapshot in dataSnapshot.children) {
                        var key: String = snapshot.key.toString()
                        var value = snapshot.value.toString()
                        if (key == "UserId") {
                            var Myid = value
                            database.child("users").child(GuestUid.toString()).child("follow_request").child(cuser!!.uid).setValue(Myid)
                            var fcmPush=FcmPush()
                            var message = Myid+"님이 팔로우를 요청했습니다."
                            fcmPush.sendMessage(GuestUid.toString(), "", message,"OPEN_ACTIVITY")
                            Toast.makeText(baseContext, "팔로우 요청 완료", Toast.LENGTH_SHORT).show()
                            val intent = Intent()
                            setResult(Activity.RESULT_OK, intent)
                            finish()
                        }
                    }
                }
                override fun onCancelled(databaseError: DatabaseError) {
                }
            }
            database.child("users").child(cuser!!.uid).addValueEventListener(myIdlistener)
        }
    }
    }

