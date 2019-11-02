package com.example.todaybook

import android.media.Image
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_mylib.*


//import android.support.v7.widget.RecyclerView

class MylibActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mylib)

        displayList()
    }
    val UserId = "fg"
    fun getUser(uID: String) {
        val database = FirebaseDatabase.getInstance().getReference("users")
        database.child(UserId).child("didBook").addListenerForSingleValueEvent(
            object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                // ...
                }
                override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                }
            })
    }
    private fun displayList() {
         val imageList = ArrayList<ImageDataModel>()
        imageList.clear()
        imageList.add(ImageDataModel("https://conversionxl.com/wp-content/uploads/2018/09/coding-language.jpg", "Test"))
        imageList.add(ImageDataModel("https://makeawebsitehub.com/wp-content/uploads/2016/02/learn-code-e1455713167295.jpg", "Test"))
        imageList.add(ImageDataModel("https://www.tecmint.com/wp-content/uploads/2016/11/Convert-PNG-to-JPG-Using-for-loop-Command.png", "Test"))
        imageList.add(ImageDataModel("https://conversionxl.com/wp-content/uploads/2018/09/coding-language.jpg", "Test"))
        imageList.add(ImageDataModel("https://www.tecmint.com/wp-content/uploads/2016/11/Convert-PNG-to-JPG-Using-for-loop-Command.png", "Test"))
        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayout.HORIZONTAL, false)
        recyclerView.adapter = ViewAdapter(imageList)
        recyclerView2.layoutManager = LinearLayoutManager(this, LinearLayout.HORIZONTAL, false)
        recyclerView2.adapter = ViewAdapter(imageList)
    }
}

