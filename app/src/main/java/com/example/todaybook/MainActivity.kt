package com.example.todaybook

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.iid.FirebaseInstanceId
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.db.NULL

class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)



        bt_Search.setOnClickListener {
            var bookTitle = edit_title.text.toString()
            if(bookTitle.length <= 0){
                Toast.makeText(baseContext, "제목을 입력하세요", Toast.LENGTH_SHORT).show()
                val detailIntent = Intent(this, MainActivity::class.java)
                startActivityForResult(detailIntent, 1)
            }
            else{
                val detailIntent = Intent(this, SearchActivity::class.java)
                detailIntent.putExtra("BookTitle", bookTitle)
                startActivityForResult(detailIntent, 1)

                edit_title.text.clear()

            }


        }


        bt_map.setOnClickListener {
            val detailIntent = Intent(this, MapsActivity::class.java)
            startActivityForResult(detailIntent, 1)
        }
        bt_mylib.setOnClickListener {
            val detailIntent = Intent(this, MylibActivity::class.java)
            startActivityForResult(detailIntent, 1)
        }
        bt_friendlib.setOnClickListener {
            val detailIntent = Intent(this, friendList::class.java)
            startActivityForResult(detailIntent, 1)
        }
        bt_login.setOnClickListener {
            val detailIntent = Intent(this, login::class.java)
            startActivityForResult(detailIntent, 1)
        }
    }

}