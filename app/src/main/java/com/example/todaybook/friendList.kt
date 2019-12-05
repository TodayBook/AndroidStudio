package com.example.todaybook

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.viewpager.widget.ViewPager
import com.example.todaybook.R.id.pager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_freindlist.*
import org.jetbrains.anko.toast

class friendList : AppCompatActivity() {
    var database = FirebaseDatabase.getInstance().reference
    val cuser = FirebaseAuth.getInstance().currentUser
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_freindlist)
        val vadapter = PagerAdapter(supportFragmentManager)
        pager.adapter = vadapter

        tab.setupWithViewPager(pager)

        bt_friendfind.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            val dialogView = layoutInflater.inflate(R.layout.friendfindpop, null)
            val dialogText = dialogView.findViewById<EditText>(R.id.friendId)
            builder.setView(dialogView)
                .setPositiveButton("확인") { dialogInterface, i ->
                    var friendId=dialogText.text.toString()
                    val detailIntent = Intent(baseContext, friendfindLib::class.java)
                    detailIntent.putExtra("FriendId",friendId)
                    startActivityForResult(detailIntent,1)
                }
                .setNegativeButton("취소") { dialogInterface, i ->
                    /* 취소일 때 아무 액션이 없으므로 빈칸 */
                }
                .show()
        }
        bt_followrequest.setOnClickListener {
            val detailIntent = Intent(baseContext, friendnoti::class.java)
            startActivityForResult(detailIntent,1)
        }
        val namelistener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (snapshot in dataSnapshot.children) {
                    var key: String = snapshot.key.toString()
                    var value = snapshot.value.toString()
                    nametext.text = value + "님의 친구"
                    if (key == "UserId") break
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {
                Log.w("FFFFFF", "loadPost:onCancelled", databaseError.toException())
            }
        }
        database.child("users").child(cuser!!.uid).addValueEventListener(namelistener)

    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // check if the requestCode is the wanted one and if the result is what we are expecting
        if (requestCode == 1 && resultCode == RESULT_OK) {
        }
        else{
            //Toast.makeText(baseContext, "실패", Toast.LENGTH_SHORT).show()
        }
    }

}
