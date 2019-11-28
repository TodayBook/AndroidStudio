package com.example.todaybook

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
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

/*private fun permissionCheck(){
    var camaeraPermission:Int= ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
    var writeExternalStoragePermission:Int=ContextCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE)

    if(camaeraPermission== PackageManager.PERMISSION_GRANTED&&writeExternalStoragePermission==PackageManager.PERMISSION_GRANTED){
        setupCamera()
    }
    else{
        ActivityCompat.requestPermissions(this,arrayPermission,requestPermission)
    }
}
@override fun onRequestPermissionResult(requestCode:Int,permissions:Array<out String>,grantsResults:IntArray){
    super.onRequestPermissionResult(requestCode,permissions,grantResults)

    if(requestCode==requestPermission && grantResults[0]==PackageManager.PERMISSION_GRANTED&&grantsResults[1]==PackageManager.PERMISSION_GRANTED){
        setupCamera()
    }
    else{
        Toast.makeText(this,"permissions are not granted", Toast.LENGTH_LONG).show()
    }
}

private fun setupCamera(){
    if(mCamera==null){
        mcamera.Camera.open()
    }
    cameraPreview=CameraPreview(this,mcamera!!)
    camera_frameLayout.addView(cameraPreview)
}*/

/*class CameraActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    var database = FirebaseDatabase.getInstance().reference
    val cuser = FirebaseAuth.getInstance().currentUser
    var Camera=0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)
        bt_camera.setOnClickListener{turnOnCamera()}
        reload()
    }
    fun reload() {
        println("reload")
        var photoList = ArrayList<CameraDataModel>()
        photoList.clear()

        /*val namelistener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (snapshot in dataSnapshot.children) {
                    var key: String = snapshot.key.toString()
                    var value = snapshot.value.toString()
                    if (key == "UserId") break
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.w("FFFFFF", "loadPost:onCancelled", databaseError.toException())
            }
        }
        database.child("users").child(cuser!!.uid).addValueEventListener(namelistener)*/////////이름입력


       val photoAdapter = ViewAdapter(this, photoList) { CameraDataModel ->
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
        }


        recyclerView_readbook.layoutManager =
            LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        recyclerView_readbook.adapter = photoAdapter


        var UserId: String
        if (cuser != null) {
            UserId = cuser.uid

            val readbooklistener = object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    for (snapshot in dataSnapshot.children) {
                        var key: String = snapshot.key.toString()
                        var value = snapshot.getValue(CameraDB::class.java)
                        readList.add(
                            ImageDataModel(
                                value!!.imageurl,
                                key,
                                value.author,
                                value.publisher
                            )
                        )
                    }
                    rbAdapter.notifyDataSetChanged()
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Log.w("FFFFFF", "loadPost:onCancelled", databaseError.toException())
                }
            }
            database.child("users").child(UserId).child("didBook")
                .addValueEventListener(readbooklistener)


        }
        else {
            //val loginIntent = Intent(this, login::class.java)
            //startActivityForResult(loginIntent, 1)
        }
    }







    private fun turnOnCamera(){
        var intent= Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent,Camera)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            /*imageview.setImageURL(data.getData())*/

        }
    }


}*/
