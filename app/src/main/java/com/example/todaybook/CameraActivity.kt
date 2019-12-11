package com.example.todaybook

import android.Manifest
import android.Manifest.permission.CAMERA
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
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
import kotlinx.android.synthetic.main.activity_camera.*
import kotlinx.android.synthetic.main.activity_didbooklib_detail.*
import kotlinx.android.synthetic.main.activity_mylib.*
import kotlinx.android.synthetic.main.lib_book.*
import kotlinx.android.synthetic.main.tester.*
import android.os.Build
import android.util.Base64
import androidx.annotation.NonNull
import androidx.appcompat.widget.AppCompatImageView
import androidx.fragment.app.FragmentActivity
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import kotlinx.android.synthetic.main.activity_profile.*
import java.io.ByteArrayOutputStream


class CameraActivity : AppCompatActivity() {
    val TAG = "MyMessage"
    private lateinit var auth: FirebaseAuth
    var database = FirebaseDatabase.getInstance().reference
    val cuser = FirebaseAuth.getInstance().currentUser
    var Camera=0
    val storage = FirebaseStorage.getInstance()



    override fun onCreate(savedInstanceState: Bundle?) {
        auth = FirebaseAuth.getInstance()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)
        val mStorageRef: StorageReference
        mStorageRef = FirebaseStorage.getInstance().getReference()

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
                    CameraDataModel.photourl
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
                        var value = snapshot.getValue(bookDB::class.java)
                        var value2=(snapshot.child("cameraimageurl").getValue())
                        /*var value = snapshot.getValue(CameraDB::class.java)*/
                        photoList.add(
                            CameraDataModel(
                                value!!.imageurl,
                                key,
                                value.author,
                                value.publisher,
                                value2.toString()
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

            /*val imageBitmap = extras?.get("data").toString()*/
            val imgBitmap:Bitmap = extras?.get("data") as Bitmap
            val selectedImageUri= getImageUriFromBitmap(this,imgBitmap)




            database.child("users").child(cuser?.uid!!).child("didBook")
                .child(EncodeString(bookinfo.title)).child("cameraimageurl")
                .push().setValue(selectedImageUri.toString())////database저장




            var title=EncodeString(bookinfo.title)
            val riversRef = storage.getReference().child("photo/").child(auth.uid!!).child(title).child(imgBitmap.toString())
            riversRef.putFile(selectedImageUri!!)
                .addOnSuccessListener(OnSuccessListener<UploadTask.TaskSnapshot> { taskSnapshot ->
                    Toast.makeText(baseContext, "upload!", Toast.LENGTH_SHORT).show()
                })
                .addOnFailureListener(OnFailureListener {
                    Toast.makeText(baseContext, "fail!", Toast.LENGTH_SHORT).show()
                })




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
    fun BitmapToString(bitmap:Bitmap):String {
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 70, baos)
        val bytes = baos.toByteArray()
        val temp = Base64.encodeToString(bytes, Base64.DEFAULT)
        return temp
    }
    fun getImageUriFromBitmap(context: Context, bitmap: Bitmap): Uri{
        val bytes = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path = MediaStore.Images.Media.insertImage(context.contentResolver, bitmap, "Title", null)
        return Uri.parse(path.toString())
    }

}


