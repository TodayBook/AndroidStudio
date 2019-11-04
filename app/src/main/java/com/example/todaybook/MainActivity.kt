package com.example.todaybook

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bt_Search.setOnClickListener {
            var bookTitle = edit_title.text.toString()
            val detailIntent = Intent(this, SearchActivity::class.java)
            detailIntent.putExtra("BookTitle", bookTitle)
            startActivityForResult(detailIntent, 1)

            edit_title.text.clear()

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
            val detailIntent = Intent(this, FreindlibActivity::class.java)
            startActivityForResult(detailIntent, 1)
        }
        bt_login.setOnClickListener {
            val detailIntent = Intent(this, login::class.java)
            startActivityForResult(detailIntent, 1)
        }
    }

}