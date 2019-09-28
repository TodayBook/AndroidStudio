package com.example.todaybook

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_mylib.*
import kotlinx.android.synthetic.main.book_item.view.*

class MylibActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mylib)
        supportActionBar?.title="검색제목"
        val layoutManager = LinearLayoutManager(this@MylibActivity)
        layoutManager.reverseLayout=true
        layoutManager.stackFromEnd=true
        recyclerView.layoutManager=layoutManager
        recyclerView.adapter=MyAdapter()

    }
    inner class MyViewHolder(book_item:View):RecyclerView.ViewHolder(book_item) {
        val imageView:ImageView=book_item.imageView
        val bookText:TextView=book_item.bookText
    }
    inner class MyAdapter:RecyclerView.Adapter<MyViewHolder>(){
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            return MyViewHolder(LayoutInflater.from(this@MylibActivity).inflate(R.layout.book_item,parent,false))
        }

        override fun getItemCount(): Int {
            //return posts.size
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            holder.bookText.text="몰라"
        }
    }
}
