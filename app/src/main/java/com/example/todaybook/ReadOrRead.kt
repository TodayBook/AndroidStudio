package com.example.todaybook

import android.content.Intent
import android.icu.text.CaseMap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_read_or_read.*

class ReadOrRead : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    var database = FirebaseDatabase.getInstance().reference
    val cuser = FirebaseAuth.getInstance().currentUser
    fun EncodeString(title:String):String {
        return title.replace(".", " ")
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_read_or_read)
        val bookinfo by lazy { intent.extras!!["Info"] as BookInfo2 }

        val bookimage = findViewById<ImageView>(R.id.coverimage)
        val booktitle = findViewById<TextView>(R.id.titleView)
        val bookauthor =findViewById<TextView>(R.id.authorView)
        val bookpublisher =findViewById<TextView>(R.id.pubView)
        val bookcontents =findViewById<TextView>(R.id.detailcontents)

        Glide.with(this).load(bookinfo.imageurl).into(bookimage)

        booktitle.text = bookinfo.title
        bookpublisher.text =  bookinfo.pub
        bookauthor.text = ( bookinfo.author).toString()
        bookcontents.text =  bookinfo.contents

        println(bookinfo.title)
        bt_didbook.setOnClickListener {
            database.child("users").child(cuser?.uid!!).child("didBook").child(EncodeString(bookinfo.title)).setValue(bookDB(bookinfo.imageurl, bookinfo.author, bookinfo.pub))
            val myIdlistener = object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    for (snapshot in dataSnapshot.children) {
                        var key: String = snapshot.key.toString()
                        var value = snapshot.value.toString()
                        if (key == "UserId") {
                            database.child("Books").child(bookinfo.title).child(cuser.uid).setValue(value)
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
            database.child("users").child(cuser?.uid!!).child("willBook").child(EncodeString(bookinfo.title)).setValue(bookDB(bookinfo.imageurl, bookinfo.author, bookinfo.pub))
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
}
