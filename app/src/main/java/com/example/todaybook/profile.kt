package com.example.todaybook


import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_profile.*
import com.google.firebase.storage.FirebaseStorage
import com.google.android.gms.tasks.OnFailureListener
import com.google.firebase.storage.UploadTask
import com.google.android.gms.tasks.OnSuccessListener
import com.bumptech.glide.Glide


class profile : AppCompatActivity() {
    val database = FirebaseDatabase.getInstance().reference
    private lateinit var auth: FirebaseAuth
    private val GET_GALLERY_IMAGE = 200
    val storage = FirebaseStorage.getInstance()
    var private:Boolean=false
    override fun onCreate(savedInstanceState: Bundle?) {
        auth = FirebaseAuth.getInstance()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        setmyid()
        setmyprivate()
        downloadInMemory()

        bt_profileimg_change.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*")
            startActivityForResult(intent, GET_GALLERY_IMAGE)
        }
        bt_change.setOnClickListener {
            val changedid=change_id.text.toString()
            if(changedid!=null) {
                database.child("users").child(auth.uid!!).child("UserId").setValue(changedid)
                changeId()
                database.child("UserId").child(changedid).child("uid").setValue(auth.uid!!)
                val intent = Intent()
                setResult(Activity.RESULT_OK, intent)
                finish()
            }
            database.child("users").child(auth.uid!!).child("private").setValue(private)
        }

        switch_private.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                private=true
            } else {
                private=true
            }
        }

    }
     private fun downloadInMemory() {
         val storageRef = storage.reference
         var ref = storageRef.child("profileimg/"+auth.uid)
         ref.getDownloadUrl().addOnSuccessListener(OnSuccessListener<Any> { uri ->
             val imageURL = uri.toString()
             Glide.with(applicationContext).load(imageURL).into(profile_img)
         }).addOnFailureListener(OnFailureListener {
             print("download failed")
         })
    }

    private fun setmyid(){
        val myIdlistener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (snapshot in dataSnapshot.children) {
                    var key: String = snapshot.key.toString()
                    println(key)
                    var value = snapshot.value.toString()
                    if (key == "UserId") {
                        change_id.setText(value)
                        break
                    }
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {
            }
        }
        database.child("users").child(auth.uid!!).addValueEventListener(myIdlistener)
    }

    private fun setmyprivate(){
        val myprivatelistener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (snapshot in dataSnapshot.children) {
                    var key: String = snapshot.key.toString()
                    println(key)
                    var value = snapshot.value.toString()
                    if (key == "private") {
                        if(value=="true"){
                            switch_private.isChecked=true
                        }
                        break
                    }
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {
            }
        }
        database.child("users").child(auth.uid!!).addValueEventListener(myprivatelistener)
    }

    private fun changeId(){
        val myIdchangelistener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (snapshot in dataSnapshot.children) {
                    var key: String = snapshot.key.toString()
                    var value = snapshot.value.toString()
                    if (key == "UserId") {
                        database.child("UserId").child(value).child("uid").removeValue()
                    }
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {
            }
        }
        database.child("users").child(auth.uid!!).addValueEventListener(myIdchangelistener)
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == GET_GALLERY_IMAGE && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            val selectedImageUri = data.data
            profile_img.setImageURI(selectedImageUri)
            val riversRef = storage.getReference().child("profileimg/").child(auth.uid!!)
            riversRef.putFile(selectedImageUri!!)
                .addOnSuccessListener(OnSuccessListener<UploadTask.TaskSnapshot> { taskSnapshot ->
                    Toast.makeText(baseContext, "upload!", Toast.LENGTH_SHORT).show()
                })
                .addOnFailureListener(OnFailureListener {
                    Toast.makeText(baseContext, "fail!", Toast.LENGTH_SHORT).show()
                })
        }
    }
}
