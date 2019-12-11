package com.example.todaybook

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.ImageView
import androidx.annotation.NonNull
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_camera.*
import kotlinx.android.synthetic.main.activity_camera2.*
import kotlinx.android.synthetic.main.activity_camera2.bt_camera
import kotlinx.android.synthetic.main.activity_camera2.bt_gallery

class CameraActivity2 : AppCompatActivity() {
    val TAG = "MyMessage"
    private lateinit var auth: FirebaseAuth
    var database = FirebaseDatabase.getInstance().reference
    val cuser = FirebaseAuth.getInstance().currentUser
    var Camera=0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera2)


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

        bt_gallery.setOnClickListener{

        }
        /*reload()*/

    }

    fun reload() {
        println("reload")
        var photoList = ArrayList<Camera>()
        photoList.clear()


       /* val photoAdapter = CameraAdapter(this, photoList) { CameraDataModel ->
            /*val detailIntent = Intent(this, didbooklib_detail::class.java)
            detailIntent.putExtra(
                "Info",
                BookInfo(
                    imageDataModel.url,
                    imageDataModel.title,
                    imageDataModel.author,
                    imageDataModel.pub
                )
            )
            startActivityForResult(detailIntent, 1)*//////사진 누르면 이동하는 코드
        }*/

        val cameraAdapter = CameraAdapter2(this, photoList)
        BookListView.adapter = cameraAdapter


        var UserId: String
        if (cuser != null) {
            UserId = cuser.uid

            val photolistener = object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    for (snapshot in dataSnapshot.children) {
                        var key: String = snapshot.key.toString()
                        var value = snapshot.getValue(CameraDB::class.java)
                        photoList.add(
                            Camera(
                                value!!.imageurl
                            )
                        )
                    }
                    cameraAdapter.notifyDataSetChanged()
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Log.w("FFFFFF", "loadPost:onCancelled", databaseError.toException())
                }
            }
            val bookinfo by lazy { intent.extras!!["Info"] as BookInfo2 }
            database.child("users").child(UserId).child("didBook").child(bookinfo?.title)
                .addValueEventListener(photolistener)

        }
        else {

        }
    }

    private fun turnOnCamera(){
        var intent= Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent,Camera)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            val extras = data?.getExtras()
            val imageBitmap = extras?.get("data") as Bitmap
            var UserId: String
            val bookinfo by lazy { intent.extras!!["Info"] as BookInfo2 }
            if (cuser != null) {
                UserId = cuser.uid
                database.child("users").child(UserId).child("didBook").child(bookinfo.title)
                    .push().setValue(imageBitmap)
            }

            /*(findViewById(R.id.cameraimg) as ImageView).setImageBitmap(imageBitmap)*/

        }
    }
}
