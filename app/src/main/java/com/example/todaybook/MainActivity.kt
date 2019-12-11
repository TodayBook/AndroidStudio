package com.example.todaybook

import android.app.*
import android.app.usage.UsageStats
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Process
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*
import android.app.NotificationManager
import android.app.NotificationChannel
import android.app.PendingIntent.getActivity
import com.bumptech.glide.Glide
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import java.text.ParseException
import java.text.SimpleDateFormat


class MainActivity : AppCompatActivity() {
    val database = FirebaseDatabase.getInstance().reference
    val cuser = FirebaseAuth.getInstance().currentUser
    val storage = FirebaseStorage.getInstance()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val storageRef = storage.reference
        if(cuser!=null) {
            var ref = storageRef.child("profileimg/" + cuser.uid)
            ref.getDownloadUrl().addOnSuccessListener(OnSuccessListener<Any> { uri ->
                val imageURL = uri.toString()
                Glide.with(this).load(imageURL).into(bt_profile)
            }).addOnFailureListener(OnFailureListener {
                print("download failed")
            })
        }
            bt_mylib.setOnClickListener {
                    val detailIntent = Intent(this, MylibActivity::class.java)
                    startActivityForResult(detailIntent, 1)

            }
            bt_friendlib.setOnClickListener {
                    val detailIntent = Intent(this, friendList::class.java)
                    startActivityForResult(detailIntent, 1)

            }
            bt_profile.setOnClickListener {
                    val detailIntent = Intent(this, profile::class.java)
                    startActivityForResult(detailIntent, 1)
            }

        if(cuser==null){
            val detailIntent = Intent(this, login::class.java)
            startActivityForResult(detailIntent, 1)
        }
                 bt_Search.setOnClickListener {
                var bookTitle = edit_title.text.toString()
                if (bookTitle.length <= 0) {
                    Toast.makeText(baseContext, "제목을 입력하세요", Toast.LENGTH_SHORT).show()
                    val detailIntent = Intent(this, MainActivity::class.java)
                    startActivityForResult(detailIntent, 1)
                } else {
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

            bt_login.setOnClickListener {
                val detailIntent = Intent(this, login::class.java)
                startActivityForResult(detailIntent, 1)
            }

    }

    override fun onBackPressed() {
        finish()
        super.onBackPressed()
    }


}