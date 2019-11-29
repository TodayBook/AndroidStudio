package com.example.todaybook

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
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
import org.jetbrains.anko.toast


class profile : AppCompatActivity() {
    val database = FirebaseDatabase.getInstance().reference
    private lateinit var auth: FirebaseAuth
    private val GET_GALLERY_IMAGE = 200
    val storage = FirebaseStorage.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        auth = FirebaseAuth.getInstance()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
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
        }

    }
     private fun downloadInMemory() {
        var ref = storage.getReference().child("profileimg.jpg")
        var ONE_MEGABYTE: Long = 1024 * 1024
        ref?.getBytes(ONE_MEGABYTE).addOnCompleteListener {
            if (it.isSuccessful)
                toast("이미지 다운로드 성공")
            profile_img.setImageBitmap(byteArrayToBitmap(it.result!!))
        }
    }

    private fun byteArrayToBitmap(byteArry: ByteArray): Bitmap {
        var bitmap:Bitmap?=null
        bitmap = BitmapFactory.decodeByteArray(byteArry,0,byteArry.size)
        return bitmap
    }

    private fun changeId(){
        val myIdlistener = object : ValueEventListener {
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
        database.child("users").child(auth.uid!!).addValueEventListener(myIdlistener)
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == GET_GALLERY_IMAGE && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            val selectedImageUri = data.data
            profile_img.setImageURI(selectedImageUri)
            val riversRef = storage.getReference().child("profileimg.jpg")
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
