package com.example.todaybook

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_didbooklib_detail.*
import androidx.core.app.ComponentActivity.ExtraData
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T



class didbooklib_detail : AppCompatActivity() {
    var database = FirebaseDatabase.getInstance().reference
    val cuser = FirebaseAuth.getInstance().currentUser
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_didbooklib_detail)
        val bookinfo by lazy{intent.extras!!["Info"] as BookInfo}
        titleView.text = bookinfo.title
        authorView.text = bookinfo.author
        pubView.text = bookinfo.pub
        Glide.with(coverimage).load(bookinfo.imageurl).into(coverimage)

        val commentslistener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (snapshot in dataSnapshot.children) {
                    var key : String = snapshot.key.toString()
                    var value = snapshot.value.toString()
                    if(key=="comments"){
                        comments.setText(value)
                        break
                    }
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {
                Log.w("FFFFFF", "loadPost:onCancelled", databaseError.toException())
            }
        }
        database.child("users").child(cuser!!.uid).child("didBook").child(bookinfo.title).addValueEventListener(commentslistener)

        bt_complete.setOnClickListener{
            database.child("users").child(cuser?.uid!!).child("didBook").child(bookinfo.title).child("comments").setValue(comments.text.toString())
        }
        bt_delete.setOnClickListener {
            database.child("users").child(cuser!!.uid).child("didBook").child(bookinfo.title).removeValue()
            database.child("Books").child(bookinfo.title).child(cuser!!.uid).removeValue()
            println("delete"+bookinfo.title)
            val intent = Intent()
            intent.putExtra("result", bookinfo.title)
            setResult(Activity.RESULT_OK, intent)
            finish()
        }
    }
}
