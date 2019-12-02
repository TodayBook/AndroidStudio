import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.todaybook.BaseFragment

import com.example.todaybook.FriendlibActivity
import com.example.todaybook.R
import com.example.todaybook.friendlistAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener



class follower : BaseFragment() {
    var database = FirebaseDatabase.getInstance().reference
    val cuser = FirebaseAuth.getInstance().currentUser

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_following, container, false)
        val list : ListView = view.findViewById(R.id.friendlist)
        println("following")
        var friendsId = ArrayList<String>()
        friendsId.clear()
        var friendsUid = ArrayList<String>()
        friendsUid.clear()
        var ct = container!!.getContext()
        val adapter = friendlistAdapter(ct,friendsId,friendsUid)
        list.adapter = adapter

        val friendlistener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (snapshot in dataSnapshot.children) {
                    var key : String = snapshot.key.toString()
                    var value = snapshot.value.toString()
                    friendsUid.add(key)
                    friendsId.add(value)
                }
                adapter.notifyDataSetChanged()
            }
            override fun onCancelled(databaseError: DatabaseError) {
                Log.w("FFFFFF", "loadPost:onCancelled", databaseError.toException())
            }
        }
        database.child("users").child(cuser!!.uid).child("follower").addValueEventListener(friendlistener)

        list.setOnItemClickListener{parant,itemView,position,id->
            val myprivatelistener = object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    for (snapshot in dataSnapshot.children) {
                        var key: String = snapshot.key.toString()
                        println(key)
                        var value = snapshot.value.toString()
                        if (key == "private") {
                            if(value=="false"){
                                val detailIntent = Intent(ct, FriendlibActivity::class.java)
                                detailIntent.putExtra("FriendUid",friendsUid[position])
                                startActivityForResult(detailIntent,1)
                            }
                            else{
                                Toast.makeText(view.context, "비공개입니다", Toast.LENGTH_SHORT).show()
                            }
                            break
                        }
                    }
                }
                override fun onCancelled(databaseError: DatabaseError) {
                }
            }
            database.child("users").child(friendsUid[position]).addValueEventListener(myprivatelistener)
        }
        return view
    }

    override fun title(): String {
        return "팔로워"
    }
}

