package com.example.todaybook

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.example.todaybook.R
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.friendnoti_item.*

class FriendnotiAdapter(val context : Context, val friendId:ArrayList<String>, val friendUid:ArrayList<String>): BaseAdapter() {
    var database = FirebaseDatabase.getInstance().reference
    val cuser = FirebaseAuth.getInstance().currentUser
    val storage = FirebaseStorage.getInstance()
    override fun getCount(): Int {
        return friendId.size
    }

    override fun getItem(idx: Int): Any {
        return friendId[idx]
    }

    override fun getItemId(idx: Int): Long {
        return idx.toLong()
    }

    override fun getView(idx: Int, convertView: View?, parent: ViewGroup?): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.friendnoti_item, parent, false) as View
        view.findViewById<TextView>(R.id.friendnoti_text).text = friendId[idx]
        val friend_profileimg=view.findViewById<ImageView>(R.id.friendnoti_img)
        val storageRef = storage.reference
        var ref = storageRef.child("profileimg/"+friendUid[idx])
        ref.getDownloadUrl().addOnSuccessListener(OnSuccessListener<Any> { uri ->
            val imageURL = uri.toString()
            Glide.with(view.context).load(imageURL).into(friend_profileimg)
        }).addOnFailureListener(OnFailureListener {
            print("download failed")
        })
        var bt_friendnoti_ok: Button = view.findViewById(R.id.bt_friendnoti_ok)
        var bt_friendnoti_deny: Button = view.findViewById(R.id.bt_friendnoti_deny)
        bt_friendnoti_ok.setOnClickListener{
            val myIdlistener = object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    for (snapshot in dataSnapshot.children) {
                        var key: String = snapshot.key.toString()
                        var value = snapshot.value.toString()
                        if (key == "UserId") {
                            database.child("users").child(cuser!!.uid).child("follower").child(friendUid[idx]).setValue(friendId)
                            database.child("users").child(friendUid[idx]).child("following").child(cuser.uid).setValue(value)
                            database.child("users").child(friendUid[idx]).child("follow_request").child(cuser!!.uid).removeValue()
                            var fcmPush=FcmPush()
                            var message = value+"님이 팔로우를 수락했습니다."
                            fcmPush.sendMessage(friendUid[idx], "", message)
                            view.findViewById<TextView>(R.id.friendnoti_text).text = "요청이 삭제되었습니다."
                            bt_friendnoti_ok.isVisible=false
                            bt_friendnoti_deny.isVisible=false
                            break
                        }
                    }
                }
                override fun onCancelled(databaseError: DatabaseError) {
                }
            }
            database.child("users").child(cuser!!.uid).addValueEventListener(myIdlistener)
        }
        bt_friendnoti_deny.setOnClickListener {
            database.child("users").child(friendUid[idx]).child("follow_request").child(cuser!!.uid).removeValue()
            view.findViewById<TextView>(R.id.friendnoti_text).text = "요청이 삭제되었습니다."
            bt_friendnoti_ok.isVisible=false
            bt_friendnoti_deny.isVisible=false
        }
        return view
    }
}