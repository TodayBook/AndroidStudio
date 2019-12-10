package com.example.todaybook

import android.Manifest
import android.Manifest.permission.CAMERA
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_camera.*
import kotlinx.android.synthetic.main.activity_didbooklib_detail.*
import kotlinx.android.synthetic.main.activity_mylib.*
import kotlinx.android.synthetic.main.lib_book.*
import kotlinx.android.synthetic.main.tester.*
import android.os.Build
import androidx.annotation.NonNull
import androidx.appcompat.widget.AppCompatImageView
import androidx.fragment.app.FragmentActivity


class CameraActivity : AppCompatActivity() {
    val TAG = "MyMessage"
    private lateinit var auth: FirebaseAuth
    var database = FirebaseDatabase.getInstance().reference
    val cuser = FirebaseAuth.getInstance().currentUser
    var Camera=0



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED &&
                checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
            {
                Log.d(TAG, "권한 설정 완료")
            }
            else
            {
                Log.d(TAG, "권한 설정 요청")
                ActivityCompat.requestPermissions(this, arrayOf<String>(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE), 1)
            }
        }

        fun onRequestPermissionsResult(requestCode:Int, @NonNull permissions:Array<String>, @NonNull grantResults:IntArray) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
            Log.d(TAG, "onRequestPermissionsResult")
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED)
            {
                Log.d(TAG, "Permission: " + permissions[0] + "was " + grantResults[0])
            }
        }
        bt_camera.setOnClickListener{
            turnOnCamera()
        }
        reload()


    }
    fun reload() {
        println("reload")
        var photoList = ArrayList<CameraDataModel>()
        photoList.clear()


       val photoAdapter = CameraAdapter(this, photoList) { CameraDataModel ->
            val detailIntent = Intent(this, CameraDetailActivity::class.java)
            detailIntent.putExtra(
                "picture",
                    CameraDataModel.url
            )
            startActivityForResult(detailIntent, 1)
        }


        recyclerView_photo.layoutManager =
            LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        recyclerView_photo.adapter = photoAdapter


        var UserId: String
        if (cuser != null) {
            UserId = cuser.uid

            val photolistener = object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    for (snapshot in dataSnapshot.children) {
                        var key: String = snapshot.key.toString()
                        var value = snapshot.getValue(newDB::class.java)
                        /*var value = snapshot.getValue(CameraDB::class.java)*/
                        photoList.add(
                            CameraDataModel(
                                value!!.imageurl,
                                value.imageurl,
                                key,
                                value.author,
                                value.publisher
                            )
                        )
                    }
                    photoAdapter.notifyDataSetChanged()
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Log.w("FFFFFF", "loadPost:onCancelled", databaseError.toException())
                }
            }
            val bookinfo by lazy { intent.extras!!["result"] as BookInfo }
            database.child("users").child(UserId).child("didBook")
                .addValueEventListener(photolistener)
            /*database.child("users").child(UserId).child("didBook").child(bookinfo.title)
                .addValueEventListener(photolistener)*/

        }
        else {

        }
    }

    private fun turnOnCamera(){
        var intent= Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent,Camera)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {/////camera로 찍은 사진을 처리하는 함수
    fun EncodeString(title:String):String {
        return title.replace(".", " ")
    }
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
           /* val extras = data?.getExtras()
            val imageBitmap = extras?.get("data") as Bitmap
            val detailIntent = Intent(this, CameraActivity::class.java)
            detailIntent.putExtra(
                "Info",imageBitmap
            )
            startActivityForResult(detailIntent, 1)*/

            /*val extras = data?.getExtras()
            val imageBitmap = extras?.get("data") as Bitmap
            var UserId: String
            val bookinfo by lazy { intent.extras!!["Info"] as BookInfo2 }
            if (cuser != null) {
                UserId = cuser.uid
                database.child("users").child(UserId).child("didBook").child(bookinfo.title)
                    .push().setValue(imageBitmap)

            }*/
            /*val extras = data?.getExtras()
            val imageBitmap = extras?.get("data") as Bitmap
            (findViewById(R.id.cameraimg) as ImageView).setImageBitmap(imageBitmap)*//////그냥 tester에 띄우던 코드
            val bookinfo by lazy { intent.extras!!["result"] as BookInfo }
            val extras = data?.getExtras()
            val imageBitmap = extras?.get("data").toString()
            database.child("users").child(cuser?.uid!!).child("didBook")
                .child(EncodeString(bookinfo.title))
                .push().setValue(newDB(imageBitmap))
            val myIdlistener = object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    for (snapshot in dataSnapshot.children) {
                        var key: String = snapshot.key.toString()
                        var value = snapshot.value.toString()
                        if (key == "UserId") {
                            database.child("Books").child(bookinfo.title).child(cuser.uid)
                                .setValue(value)
                        }
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                }
            }
            database.child("users").child(cuser.uid).addValueEventListener(myIdlistener)

        }
    }
    override fun onBackPressed() {
        val resultIntent = Intent(this,MainActivity::class.java)
        setResult(1,resultIntent)
        super.onBackPressed()
    }

}


