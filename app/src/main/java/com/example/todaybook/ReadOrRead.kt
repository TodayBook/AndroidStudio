package com.example.todaybook

import android.content.Intent
import android.icu.text.CaseMap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AlertDialog
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_read_or_read.*
import java.util.*

class ReadOrRead : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    var database = FirebaseDatabase.getInstance().reference
    val cuser = FirebaseAuth.getInstance().currentUser
    fun EncodeString(title:String):String {
        return title.replace(".", " ")
    }
    var title:String=""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_read_or_read)
        val bookinfo by lazy { intent.extras!!["Info"] as BookInfo2 }

        val bookimage = findViewById<ImageView>(R.id.coverimage)
        val booktitle = findViewById<TextView>(R.id.titleView)
        val bookauthor = findViewById<TextView>(R.id.authorView)
        val bookpublisher = findViewById<TextView>(R.id.pubView)
        val bookcontents = findViewById<TextView>(R.id.detailcontents)

        Glide.with(this).load(bookinfo.imageurl).into(bookimage)
        title= bookinfo.title
        booktitle.text = bookinfo.title
        bookpublisher.text = bookinfo.pub
        bookauthor.text = (bookinfo.author).toString()
        bookcontents.text = bookinfo.contents

        println(bookinfo.title)
        if(cuser!=null) {
            bt_didbook.setOnClickListener {
                database.child("users").child(cuser?.uid!!).child("didBook")
                    .child(EncodeString(bookinfo.title))
                    .setValue(bookDB(bookinfo.imageurl, bookinfo.author, bookinfo.pub))
                val myIdlistener = object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        for (snapshot in dataSnapshot.children) {
                            var key: String = snapshot.key.toString()
                            var value = snapshot.value.toString()
                            if (key == "UserId") {
                                database.child("Books").child(bookinfo.title).child(cuser.uid)
                                    .setValue(value)
                            }
                        }
                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                    }
                }
                database.child("users").child(cuser.uid).addValueEventListener(myIdlistener)

                Toast.makeText(baseContext, "Success!!", Toast.LENGTH_SHORT).show()
                val builder = AlertDialog.Builder(this)
                val dialogView = layoutInflater.inflate(R.layout.activity_popup, null)
                /*val dialogText = dialogView.findViewById<EditText>(R.id.dialogEt)*/

                builder.setView(dialogView)
                    .setPositiveButton("머물기") { dialogInterface, i ->
                    }
                    .setNegativeButton("나의도서관으로 이동") { dialogInterface, i ->
                        val detailIntent = Intent(this, MylibActivity::class.java)
                        startActivityForResult(detailIntent, 1)

                    }

                    .show()
            }
            bt_willbook.setOnClickListener {
                database.child("users").child(cuser?.uid!!).child("willBook")
                    .child(EncodeString(bookinfo.title))
                    .setValue(bookDB(bookinfo.imageurl, bookinfo.author, bookinfo.pub))
                Toast.makeText(baseContext, "Success!!", Toast.LENGTH_SHORT).show()


                val builder = AlertDialog.Builder(this)
                val dialogView = layoutInflater.inflate(R.layout.activity_popup, null)
                /*val dialogText = dialogView.findViewById<EditText>(R.id.dialogEt)*/

                builder.setView(dialogView)
                    .setPositiveButton("머물기") { dialogInterface, i ->
                    }
                    .setNegativeButton("나의도서관으로 이동") { dialogInterface, i ->
                        val detailIntent = Intent(this, MylibActivity::class.java)
                        startActivityForResult(detailIntent, 1)

                    }

                    .show()
            }
        }
        commentsload()
        bt_comments_input.setOnClickListener {
            val comment = comments_input.text.toString()
            val comment_writer = cuser!!.uid
            val comment_time = Calendar.getInstance().getTime()
            database.child("Books").child(bookinfo.title).child("comments").child(comment_time.toString()).setValue(comment_item(comment, comment_writer))
            comments_input.text.clear()
            commentsload()
        }
    }
    fun commentsload(){
        var comment_time_array = ArrayList<String>()
        comment_time_array.clear()
        var comment_array = ArrayList<String>()
        comment_array.clear()
        var comment_writer_array = ArrayList<String>()
        comment_writer_array.clear()
        var comment_anony_array = ArrayList<String>()
        comment_anony_array.clear()
        val comments_list: ListView = findViewById(R.id.comments_list)
        val comments_adapter = commentlistAdapter(this, comment_time_array, comment_array,comment_anony_array)
        comments_list.adapter = comments_adapter
        val commentsreadlistener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (snapshot in dataSnapshot.children) {
                    var key: String = snapshot.key.toString()
                    var value = snapshot.getValue(comment_item::class.java)
                    comment_time_array.add(key)
                    comment_array.add(value!!.comment_item)
                    comment_writer_array.add(value!!.writer)
                    comment_anony_array.add("익명")
                }
                comments_adapter.notifyDataSetChanged()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.w("FFFFFF", "loadPost:onCancelled", databaseError.toException())
            }
        }
        database.child("Books").child(title).child("comments").addValueEventListener(commentsreadlistener)

        comments_list.setOnItemClickListener { parant, itemView, position, id ->
            if(comment_writer_array[position]==cuser!!.uid) {
                val dialogView = layoutInflater.inflate(R.layout.comment_popup, null)
                val editcomment = dialogView.findViewById<EditText>(R.id.edit_comment)
                editcomment.setText(comment_array[position])
                val builder = AlertDialog.Builder(this)
                builder.setView(dialogView)
                    .setPositiveButton("수정") { dialogInterface, i ->
                        database.child("Books").child(title).child("comments").child(comment_time_array[position]).removeValue()
                        database.child("Books").child(title).child("comments").child(Calendar.getInstance().getTime().toString())
                            .setValue(
                                comment_item(
                                    editcomment.text.toString(),
                                    comment_writer_array[position]
                                )
                            )
                        commentsload()
                    }
                    .setNegativeButton("삭제") { dialogInterface, i ->
                        database.child("Books").child(title).child("comments").child(comment_time_array[position]).removeValue()
                        commentsload()
                    }
                    .show()
            }
        }
    }
}
