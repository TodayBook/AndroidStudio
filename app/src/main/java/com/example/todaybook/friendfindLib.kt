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
import kotlinx.android.synthetic.main.friendfindpop.*

class friendfindLib : AppCompatActivity() {
    var database = FirebaseDatabase.getInstance().reference
    val cuser = FirebaseAuth.getInstance().currentUser
    var friendUid:String?=null
    var friendId:String?=null
    var myId:String?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_guestlib)
        friendId= intent.extras!!["FriendId"].toString()
        myId= intent.extras!!["Myid"].toString()
        println(friendId)
        guest_name.text=friendId.toString()+"님의 도서관"
        val friendfindlistener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (snapshot in dataSnapshot.children) {
                    var key: String = snapshot.key.toString()
                    var value = snapshot.value.toString()
                    if (key == "uid") {
                        println(key)
                        friendUid=value
                        friendlib()
                    }
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(baseContext, "존재하지 않는 사용자입니다", Toast.LENGTH_SHORT).show()
                val intent = Intent()
                setResult(Activity.RESULT_OK, intent)
                finish()
            }
        }
        database.child("UserId").child(friendId.toString()).addValueEventListener(friendfindlistener)

        bt_follow.setOnClickListener {
            database.child("users").child(friendUid!!).child("follow_request").child(cuser!!.uid).setValue(myId)
            var fcmPush=FcmPush()
            var message = myId+"님이 팔로우를 요청했습니다."
            fcmPush.sendMessage(friendUid!!, "", message)
            print("sending")
            val intent = Intent()
            setResult(Activity.RESULT_OK, intent)
            finish()
        }
    }
    fun friendlib(){
        println("friendlib")
        val myprivatelistener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (snapshot in dataSnapshot.children) {
                    var key: String = snapshot.key.toString()
                    var value = snapshot.value.toString()
                    if (key == "private") {
                        if(value!="true"){
                            createlib()
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
        database.child("users").child(friendUid!!).addValueEventListener(myprivatelistener)
    }
    fun createlib(){
        println("createlib")
        guest_name.text=friendId+"님의 도서관"
        var readList = ArrayList<ImageDataModel>()
        readList.clear()

        val willreadList = ArrayList<ImageDataModel>()
        willreadList.clear()

        val rbAdapter = ViewAdapter(this, readList) { imageDataModel ->
            val detailIntent = Intent(this, frienddidbook_detail::class.java)
            detailIntent.putExtra("Info",FriendBookInfo(friendUid!!,imageDataModel.url,imageDataModel.title,imageDataModel.author,imageDataModel.pub))
            startActivityForResult(detailIntent, 1)
        }
        val wbAdapter = ViewAdapter(this, willreadList) { imageDataModel ->
            val detailIntent = Intent(this, friendwillbook_detail::class.java)
            detailIntent.putExtra("Info",BookInfo(imageDataModel.url,imageDataModel.title,imageDataModel.author,imageDataModel.pub))
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
                    var value = snapshot.getValue(bookDB::class.java)
                    readList.add(ImageDataModel(value!!.imageurl,key,value.author,value.publisher))
                }
                rbAdapter.notifyDataSetChanged()
            }
            override fun onCancelled(databaseError: DatabaseError) {
                Log.w("FFFFFF", "loadPost:onCancelled", databaseError.toException())
            }
        }
        database.child("users").child(friendUid!!).child("didBook").addValueEventListener(readbooklistener)

        val willbooklistener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (snapshot in dataSnapshot.children) {
                    var key : String = snapshot.key.toString()
                    var value = snapshot.getValue(bookDB::class.java)
                    willreadList.add(ImageDataModel(value!!.imageurl,key,value.author,value.publisher))
                }
                wbAdapter.notifyDataSetChanged()
            }
            override fun onCancelled(databaseError: DatabaseError) {
                Log.w("FFFFFF", "loadPost:onCancelled", databaseError.toException())
            }
        }
        database.child("users").child(friendUid!!).child("willBook").addValueEventListener(willbooklistener)

    }
}
