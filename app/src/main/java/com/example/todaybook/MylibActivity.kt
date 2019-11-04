package com.example.todaybook

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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
        val readList = ArrayList<ImageDataModel>()
        readList.clear()
        readList.add(ImageDataModel("https://image.aladin.co.kr/product/21344/20/cover500/8965963494_1.jpg",  "마음에도 근육이 붙나 봐요","AM327"))
        readList.add(ImageDataModel("https://image.aladin.co.kr/product/21292/13/cover500/8936438034_1.jpg", "일의 기쁨과 슬픔","장류진"))
        readList.add(ImageDataModel("https://image.aladin.co.kr/product/21168/79/cover500/s642636886_1.jpg", "방랑자들","올가 토카르추크"))
        readList.add(ImageDataModel("https://image.aladin.co.kr/product/21290/5/cover500/k912636388_1.jpg", "지금 이대로 좋다 ","법륜"))
        readList.add(ImageDataModel("https://image.aladin.co.kr/product/21352/75/cover500/8986022109_1.jpg", "식물의 책","이소영"))

        val willreadList = ArrayList<ImageDataModel>()
        willreadList.clear()
        willreadList.add(ImageDataModel("https://image.aladin.co.kr/product/20634/66/cover500/k552636969_1.jpg",  "혼자가 혼자에게","이병률"))
        willreadList.add(ImageDataModel("https://image.aladin.co.kr/product/18827/60/cover500/8954655971_2.jpg", "여행의 이유","김영하"))
        willreadList.add(ImageDataModel("https://image.aladin.co.kr/product/20815/14/cover500/s072636972_1.jpg", "지쳤거나 좋아하는 게 없거나","글배우"))
        willreadList.add(ImageDataModel("https://image.aladin.co.kr/product/21263/32/cover500/s732636485_1.jpg", "나를 돌보지 않는 나에게 ","정여울"))
        willreadList.add(ImageDataModel("https://image.aladin.co.kr/product/18705/49/cover500/8950980339_2.jpg", "빈센트 나의 빈센트","정여울"))

        recyclerView_readbook.layoutManager = LinearLayoutManager(this, RecyclerView.HORIZONTAL, false)
        recyclerView_willbook.layoutManager = LinearLayoutManager(this, RecyclerView.HORIZONTAL, false)

        val rbAdapter = ViewAdapter(this, readList) { imageDataModel ->
            val detailIntent = Intent(this, lib_detail::class.java)
            startActivityForResult(detailIntent, 1)
        }
        val wbAdapter = ViewAdapter(this, willreadList) { imageDataModel ->
            val detailIntent = Intent(this, lib_detail::class.java)
            startActivityForResult(detailIntent, 1)
        }
        recyclerView_readbook.adapter = rbAdapter
        recyclerView_willbook.adapter = wbAdapter
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




}

