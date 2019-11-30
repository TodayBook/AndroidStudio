import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import androidx.fragment.app.Fragment
import com.example.todaybook.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class following : BaseFragment() {
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

        var ct = container!!.getContext();
        val adapter = friendlistAdapter(ct,friendsId,friendsUid)
        list.adapter = adapter

        val friendlistener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (snapshot in dataSnapshot.children) {
                    var key : String = snapshot.key.toString()
                    var value = snapshot.value.toString()
                    println("friendsUid"+friendsUid)
                    friendsUid.add(key)
                    friendsId.add(value)
                }
                adapter.notifyDataSetChanged()
            }
            override fun onCancelled(databaseError: DatabaseError) {
                Log.w("FFFFFF", "loadPost:onCancelled", databaseError.toException())
            }
        }
        database.child("users").child(cuser!!.uid).child("following").addValueEventListener(friendlistener)

        list.setOnItemClickListener{parant,itemView,position,id->
            val detailIntent = Intent(ct, FriendlibActivity::class.java)
            detailIntent.putExtra("FriendUid",friendsUid[position])
            startActivityForResult(detailIntent,1)
        }
        return view
    }

     override fun title(): String {
        return "팔로잉"
    }
}

