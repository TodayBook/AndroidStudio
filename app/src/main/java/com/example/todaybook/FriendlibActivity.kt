package com.example.todaybook

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_mylib.*
import android.widget.Toast
import android.R.attr.data
import androidx.core.app.ComponentActivity.ExtraData
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T



class FriendlibActivity : AppCompatActivity() {
    var database = FirebaseDatabase.getInstance().reference
    var resultcode=200
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mylib)
        reload()
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
                reload()
    }
    fun reload(){
        println("reload")
        var readList = ArrayList<ImageDataModel>()
        readList.clear()
        val willreadList = ArrayList<ImageDataModel>()
        willreadList.clear()

        val FriendUid by lazy { intent.extras!!["FriendUid"] }

        val namelistener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (snapshot in dataSnapshot.children) {
                    var key : String = snapshot.key.toString()
                    var value = snapshot.value.toString()
                    if(key=="UserId")
                        nametext.text=value+"님의 도서관"
                    break
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {
                Log.w("FFFFFF", "loadPost:onCancelled", databaseError.toException())
            }
        }
        database.child("users").child(FriendUid.toString()).addValueEventListener(namelistener)


        val rbAdapter = ViewAdapter(this, readList) { imageDataModel ->
            val detailIntent = Intent(this, frienddidbook_detail::class.java)
            detailIntent.putExtra("Info",FriendBookInfo(FriendUid.toString(),imageDataModel.url,imageDataModel.title,imageDataModel.author,imageDataModel.pub))
            startActivityForResult(detailIntent, 1)
        }
        val wbAdapter = ViewAdapter(this, willreadList) { imageDataModel ->
            val detailIntent = Intent(this, friendwillbook_detail::class.java)
            detailIntent.putExtra("Info",FriendBookInfo(FriendUid.toString(),imageDataModel.url,imageDataModel.title,imageDataModel.author,imageDataModel.pub))
            startActivityForResult(detailIntent, 1)
        }
        recyclerView_readbook.layoutManager = LinearLayoutManager(this, RecyclerView.HORIZONTAL, false)
        recyclerView_willbook.layoutManager = LinearLayoutManager(this, RecyclerView.HORIZONTAL, false)

        recyclerView_readbook.adapter = rbAdapter
        recyclerView_willbook.adapter = wbAdapter

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
        database.child("users").child(FriendUid.toString()).child("didBook").addValueEventListener(readbooklistener)

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
        database.child("users").child(FriendUid.toString()).child("willBook").addValueEventListener(willbooklistener)
    }
    override fun onBackPressed() {
        val resultIntent = Intent(this,MainActivity::class.java)
        setResult(1,resultIntent)
        super.onBackPressed()
    }

    }
