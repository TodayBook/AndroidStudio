package com.example.todaybook

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_mylib.*
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.FirebaseDatabase



class MylibActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    var database = FirebaseDatabase.getInstance().reference
    val cuser = FirebaseAuth.getInstance().currentUser
    var Myid: String?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mylib)
        reload()
    }
    fun reload() {
        println("reload")
        var readList = ArrayList<ImageDataModel>()
        readList.clear()
        val willreadList = ArrayList<ImageDataModel>()
        willreadList.clear()

        val namelistener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (snapshot in dataSnapshot.children) {
                    var key: String = snapshot.key.toString()
                    var value = snapshot.value.toString()
                    nametext.text = value + "님의 도서관"
                    if (key == "UserId")
                        Myid=value
                        break
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.w("FFFFFF", "loadPost:onCancelled", databaseError.toException())
            }
        }
        database.child("users").child(cuser!!.uid).addValueEventListener(namelistener)

        nametext.text = "님의 도서관"

        val rbAdapter = ViewAdapter(this, readList) { imageDataModel ->
            val detailIntent = Intent(this, didbooklib_detail::class.java)
            detailIntent.putExtra(
                "Info",
                BookInfo(
                    imageDataModel.url,
                    imageDataModel.title,
                    imageDataModel.author,
                    imageDataModel.pub
                )
            )
            startActivityForResult(detailIntent, 1)
        }
        val wbAdapter = ViewAdapter(this, willreadList) { imageDataModel ->
            val detailIntent = Intent(this, willbooklib_detail::class.java)
            detailIntent.putExtra(
                "Info",
                BookInfo(
                    imageDataModel.url,
                    imageDataModel.title,
                    imageDataModel.author,
                    imageDataModel.pub
                )
            )
            startActivityForResult(detailIntent, 1)
        }

        recyclerView_readbook.layoutManager =
            LinearLayoutManager(this, RecyclerView.HORIZONTAL, false)
        recyclerView_willbook.layoutManager =
            LinearLayoutManager(this, RecyclerView.HORIZONTAL, false)

        recyclerView_readbook.adapter = rbAdapter
        recyclerView_willbook.adapter = wbAdapter

        var UserId: String
        if (cuser != null) {
            UserId = cuser.uid

            val readbooklistener = object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    for (snapshot in dataSnapshot.children) {
                        var key: String = snapshot.key.toString()
                        var value = snapshot.getValue(bookDB::class.java)
                        readList.add(
                            ImageDataModel(
                                value!!.imageurl,
                                key,
                                value.author,
                                value.publisher
                            )
                        )
                    }
                    rbAdapter.notifyDataSetChanged()
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Log.w("FFFFFF", "loadPost:onCancelled", databaseError.toException())
                }
            }
            database.child("users").child(UserId).child("didBook")
                .addValueEventListener(readbooklistener)

            val willbooklistener = object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    for (snapshot in dataSnapshot.children) {
                        var key: String = snapshot.key.toString()
                        var value = snapshot.getValue(bookDB::class.java)
                        willreadList.add(
                            ImageDataModel(
                                value!!.imageurl,
                                key,
                                value.author,
                                value.publisher
                            )
                        )
                    }
                    wbAdapter.notifyDataSetChanged()
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Log.w("FFFFFF", "loadPost:onCancelled", databaseError.toException())
                }
            }
            database.child("users").child(UserId).child("willBook")
                .addValueEventListener(willbooklistener)
        } else {
            //val loginIntent = Intent(this, login::class.java)
            //startActivityForResult(loginIntent, 1)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if(data!!.getStringExtra("result")!=null){
                database.child("Books").child(data!!.getStringExtra("result")).child(cuser!!.uid).removeValue()
            }
            reload()
        }
    }
    override fun onBackPressed() {
        val resultIntent = Intent(this,MainActivity::class.java)
        setResult(1,resultIntent)
        super.onBackPressed()
    }
}

