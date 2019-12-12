package com.example.todaybook

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.Toast
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_didbooklib_detail.*
import android.widget.RatingBar
import android.widget.RatingBar.OnRatingBarChangeListener
import android.widget.ListView
import androidx.appcompat.app.AlertDialog
import kotlinx.android.synthetic.main.activity_didbooklib_detail.authorView
import kotlinx.android.synthetic.main.activity_didbooklib_detail.bt_comments_input
import kotlinx.android.synthetic.main.activity_didbooklib_detail.comments_input
import kotlinx.android.synthetic.main.activity_didbooklib_detail.coverimage
import kotlinx.android.synthetic.main.activity_didbooklib_detail.pubView
import kotlinx.android.synthetic.main.activity_didbooklib_detail.titleView
import java.util.*


class didbooklib_detail : AppCompatActivity() {
    var database = FirebaseDatabase.getInstance().reference
    val cuser = FirebaseAuth.getInstance().currentUser
    var title:String?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_didbooklib_detail)
        val bookinfo by lazy{intent.extras!!["Info"] as BookInfo}
        titleView.text = bookinfo.title
        authorView.text = bookinfo.author
        pubView.text = bookinfo.pub
        Glide.with(coverimage).load(bookinfo.imageurl).into(coverimage)

        title=bookinfo.title

        val commentslistener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (snapshot in dataSnapshot.children) {
                    var key : String = snapshot.key.toString()
                    var value = snapshot.value.toString()
                    if(key=="review"){
                        didreview.setText(value)
                        break
                    }
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {
                Log.w("FFFFFF", "loadPost:onCancelled", databaseError.toException())
            }
        }
        database.child("users").child(cuser!!.uid).child("didBook").child(bookinfo.title).addValueEventListener(commentslistener)

        val dialogRatingBar = findViewById<RatingBar>(R.id.dialogRb)
        var star:Float?=null

        dialogRatingBar.setOnRatingBarChangeListener(OnRatingBarChangeListener { ratingBar, rating, fromUser ->
            star=rating
        })

        val ratinglistener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (snapshot in dataSnapshot.children) {
                    var key : String = snapshot.key.toString()
                    var value = snapshot.value.toString()
                    if(key=="rating"){
                        val star:Float=value.toFloat()
                        dialogRb.setRating(star)
                        break
                    }
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {
                Log.w("FFFFFF", "loadPost:onCancelled", databaseError.toException())
            }
        }
        database.child("users").child(cuser!!.uid).child("didBook").child(bookinfo.title).addValueEventListener(ratinglistener)

        bt_complete.setOnClickListener{
            database.child("users").child(cuser?.uid!!).child("didBook").child(bookinfo.title).child("review").setValue(didreview.text.toString())
            database.child("users").child(cuser?.uid!!).child("didBook").child(bookinfo.title).child("rating").setValue(dialogRb.rating)
            Toast.makeText(baseContext, "저장되었습니다!", Toast.LENGTH_SHORT).show()
        }
        bt_delete.setOnClickListener {
            database.child("users").child(cuser!!.uid).child("didBook").child(bookinfo.title).removeValue()
            database.child("Books").child(bookinfo.title).child(cuser.uid).removeValue()
            println("delete"+bookinfo.title)
            val intent = Intent()
            intent.putExtra("result", bookinfo.title)
            setResult(Activity.RESULT_OK, intent)
            finish()
            Toast.makeText(baseContext, "삭제되었습니다!!", Toast.LENGTH_SHORT).show()
        }
        bt_movecamera.setOnClickListener{
            val detailIntent = Intent(this, CameraActivity2::class.java)
            detailIntent.putExtra("result",BookInfo(bookinfo.imageurl,bookinfo.title,bookinfo.author,bookinfo.pub))
            startActivityForResult(detailIntent, 1)
        }
        commentsload()

        bt_comments_input.setOnClickListener {
            val comment=comments_input.text.toString()
            println(comment)
            val comment_writer=cuser.uid
            val comment_time = Calendar.getInstance().getTime()
            database.child("users").child(cuser?.uid!!).child("didBook").child(bookinfo.title).child("comment").child(comment_time.toString()).setValue(comment_item(comment,comment_writer))
            comments_input.text.clear()
            commentsload()
        }
    }
    override fun onBackPressed() {
        val intent = Intent()
        setResult(Activity.RESULT_OK, intent)
        finish()
        super.onBackPressed()
    }
    fun commentsload(){
        println("commentsload")
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
                    println(key)
                    comment_array.add(value!!.comment_item)
                    println(value!!.comment_item)
                    comment_writer_array.add(value!!.writer)
                    println(value!!.writer)
                }
                comments_adapter.notifyDataSetChanged()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.w("FFFFFF", "loadPost:onCancelled", databaseError.toException())
            }
        }
        database.child("users").child(cuser!!.uid).child("didBook").child(title!!).child("comment").addValueEventListener(commentsreadlistener)

        comments_list.setOnItemClickListener { parant, itemView, position, id ->
            if(comment_writer_array[position]==cuser.uid) {
                val dialogView = layoutInflater.inflate(R.layout.comment_popup, null)
                val editcomment = dialogView.findViewById<EditText>(R.id.edit_comment)
                editcomment.setText(comment_array[position])
                val builder = AlertDialog.Builder(this)
                builder.setView(dialogView)
                    .setPositiveButton("수정") { dialogInterface, i ->
                        database.child("users").child(cuser!!.uid).child("didBook").child(title!!)
                            .child("comment").child(comment_time_array[position]).removeValue()
                        database.child("users").child(cuser!!.uid).child("didBook").child(title!!)
                            .child("comment").child(Calendar.getInstance().getTime().toString())
                            .setValue(
                                comment_item(
                                    editcomment.text.toString(),
                                    comment_writer_array[position]
                                )
                            )
                        commentsload()
                    }
                    .setNegativeButton("삭제") { dialogInterface, i ->
                        database.child("users").child(cuser!!.uid).child("didBook").child(title!!)
                            .child("comment").child(comment_time_array[position]).removeValue()
                        commentsload()
                    }
                    .show()
            }
        }
    }
}