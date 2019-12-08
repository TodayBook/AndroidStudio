package com.example.todaybook

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.ListView
import androidx.appcompat.app.AlertDialog
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_willbooklib_detail.*
import java.util.*

class friendwillbook_detail : AppCompatActivity() {
    var database = FirebaseDatabase.getInstance().reference
    val cuser = FirebaseAuth.getInstance().currentUser
    var title:String?=null
    var frienduid:String?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_friendwillbook_detail)
        val bookinfo by lazy{intent.extras!!["Info"] as FriendBookInfo}
        titleView.text = bookinfo.title
        authorView.text = bookinfo.author
        pubView.text = bookinfo.pub
        Glide.with(coverimage).load(bookinfo.imageurl).into(coverimage)
        title=bookinfo.title
        frienduid=bookinfo.FriendUid
        commentsload()
        bt_comments_input.setOnClickListener {
            val comment=comments_input.text.toString()
            val comment_writer=cuser!!.uid
            val comment_time = Calendar.getInstance().getTime()
            database.child("users").child(frienduid!!).child("willBook").child(bookinfo.title).child("comment").child(comment_time.toString()).setValue(comment_item(comment,comment_writer))
            comments_input.text.clear()
            commentsload()
        }
    }
    override fun onBackPressed() {
        println("back")
        super.onBackPressed()
        val intent = Intent()
        setResult(100, intent)
        finish()
    }
    fun commentsload(){
        var comment_time_array = ArrayList<String>()
        comment_time_array.clear()
        var comment_array = ArrayList<String>()
        comment_array.clear()
        var comment_writer_array = ArrayList<String>()
        comment_writer_array.clear()
        val comments_list: ListView = findViewById(R.id.comments_list)
        val comments_adapter = commentlistAdapter(this, comment_time_array, comment_array,comment_writer_array)
        comments_list.adapter = comments_adapter
        val commentsreadlistener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (snapshot in dataSnapshot.children) {
                    var key: String = snapshot.key.toString()
                    var value = snapshot.getValue(comment_item::class.java)
                    comment_time_array.add(key)
                    comment_array.add(value!!.comment_item)
                    comment_writer_array.add(value!!.writer)
                }
                comments_adapter.notifyDataSetChanged()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.w("FFFFFF", "loadPost:onCancelled", databaseError.toException())
            }
        }
        database.child("users").child(frienduid!!).child("willBook").child(title!!).child("comment").addValueEventListener(commentsreadlistener)

        comments_list.setOnItemClickListener { parant, itemView, position, id ->
            if(comment_writer_array[position]==cuser!!.uid) {
                val dialogView = layoutInflater.inflate(R.layout.comment_popup, null)
                val editcomment = dialogView.findViewById<EditText>(R.id.edit_comment)
                editcomment.setText(comment_array[position])
                val builder = AlertDialog.Builder(this)
                builder.setView(dialogView)
                    .setPositiveButton("수정") { dialogInterface, i ->
                        database.child("users").child(frienduid!!)
                            .child("willBook").child(title!!).child("comment")
                            .child(comment_time_array[position]).removeValue()
                        database.child("users").child(frienduid!!)
                            .child("willBook").child(title!!).child("comment")
                            .child(Calendar.getInstance().getTime().toString()).setValue(
                            comment_item(
                                editcomment.text.toString(),
                                comment_writer_array[position]
                            )
                        )
                        commentsload()
                    }
                    .setNegativeButton("삭제") { dialogInterface, i ->
                        database.child("users").child(frienduid!!)
                            .child("willBook").child(title!!).child("comment")
                            .child(comment_time_array[position]).removeValue()
                        commentsload()
                    }
                    .show()
            }
        }
    }
}
