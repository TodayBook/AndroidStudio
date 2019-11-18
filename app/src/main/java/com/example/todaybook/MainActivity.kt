package com.example.todaybook

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.iid.FirebaseInstanceId
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
            val detailIntent = Intent(this, friendList::class.java)
            startActivityForResult(detailIntent, 1)
        }
        bt_login.setOnClickListener {
            val detailIntent = Intent(this, login::class.java)
            startActivityForResult(detailIntent, 1)
        }
        registerPushToken()
    }

    private fun registerPushToken() {
        //v17.0.0 이전까지는
        ////var pushToken = FirebaseInstanceId.getInstance().token
        //v17.0.1 이후부터는 onTokenRefresh()-depriciated
        var pushToken: String? = null
        var uid = FirebaseAuth.getInstance().currentUser!!.uid
        var map = mutableMapOf<String, Any>()
        FirebaseInstanceId.getInstance().instanceId.addOnSuccessListener { instanceIdResult ->
            pushToken = instanceIdResult.token
            map["pushtoken"] = pushToken!!
            FirebaseFirestore.getInstance().collection("pushtokens").document(uid!!).set(map)
        }
    }
}