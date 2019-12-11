package com.example.todaybook

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import kotlinx.android.synthetic.main.activity_camera.*
import kotlinx.android.synthetic.main.activity_camera2.*
import kotlinx.android.synthetic.main.activity_camera2.bt_camera
import kotlinx.android.synthetic.main.activity_camera2.bt_gallery
import kotlinx.android.synthetic.main.activity_profile.*
import java.io.ByteArrayOutputStream

class CameraActivity2 : AppCompatActivity() {
    val TAG = "MyMessage"
    private lateinit var auth: FirebaseAuth
    var database = FirebaseDatabase.getInstance().reference
    val cuser = FirebaseAuth.getInstance().currentUser
    var Camera=0
    var PICK_IMAGE=3
    val storage = FirebaseStorage.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera2)
        auth = FirebaseAuth.getInstance()


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
            takePhotoFromGallery()
        }

        bt_delete.setOnClickListener{
            deletedata()
        }
        reload()

    }

    fun reload() {
        fun EncodeString(title:String):String {
            return title.replace(".", " ")
        }
        val storageRef = storage.reference
        val bookinfo by lazy { intent.extras!!["result"] as BookInfo }
        var title=EncodeString(bookinfo.title)
        /* var ref = storageRef.child("photo/" + "KakaoTalk_20181210_010920433.jpg")*/
        /*var ref = storageRef.child("photo2/" + "${cuser!!.uid}" + "/" + "${title}" + "/" + "1")*/
        var ref = storageRef.child("photo2/" + "${cuser!!.uid}" + "/" +"${title}" + "/" + "1")
        ref.getDownloadUrl().addOnSuccessListener(OnSuccessListener<Any> { uri ->
            val imageURL = uri.toString()
            Glide.with(this).load(imageURL).into(cameraimg)
        }).addOnFailureListener(OnFailureListener {
            print("download failed")
        })

    }

    private fun turnOnCamera(){
        var intent= Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent,Camera)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        fun EncodeString(title:String):String {
            return title.replace(".", " ")
        }
        super.onActivityResult(requestCode, resultCode, data)
        /*val extras = data?.getExtras()
        var bitmap:Bitmap = extras?.get("data") as Bitmap*/
        val bookinfo by lazy { intent.extras!!["result"] as BookInfo }
        var title=EncodeString(bookinfo.title)
        if (resultCode == RESULT_OK && requestCode == PICK_IMAGE) {
            try {
                val bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), data!!.getData())
                (findViewById(R.id.cameraimg) as ImageView).setImageBitmap(bitmap)
            }
            catch (e:Exception) {
                e.printStackTrace()
            }

            val selectedImageUri = data!!.data
            cameraimg.setImageURI(selectedImageUri)
            val riversRef = storage.getReference().child("photo2/").child(auth.uid!!).child(title).child("1")
            riversRef.putFile(selectedImageUri!!)
                .addOnSuccessListener(OnSuccessListener<UploadTask.TaskSnapshot> { taskSnapshot ->
                    Toast.makeText(baseContext, "upload!", Toast.LENGTH_SHORT).show()
                })
                .addOnFailureListener(OnFailureListener {
                    Toast.makeText(baseContext, "fail!", Toast.LENGTH_SHORT).show()
                })//////갤러리에서 골라서 스토리지에 1로 저장
        }


        if (resultCode == RESULT_OK&&requestCode == Camera) {
            val extras = data?.getExtras()
            val imageBitmap = extras?.get("data") as Bitmap
            (findViewById(R.id.cameraimg) as ImageView).setImageBitmap(imageBitmap)
            val selectedImageUri=getImageUriFromBitmap(this,imageBitmap)
            val riversRef = storage.getReference().child("photo2/").child(auth.uid!!).child(title).child("1")
            riversRef.putFile(selectedImageUri!!)
                .addOnSuccessListener(OnSuccessListener<UploadTask.TaskSnapshot> { taskSnapshot ->
                    Toast.makeText(baseContext, "upload!", Toast.LENGTH_SHORT).show()
                })
                .addOnFailureListener(OnFailureListener {
                    Toast.makeText(baseContext, "fail!", Toast.LENGTH_SHORT).show()
                })/////카메라에서 골라서 스토리지에 1로 저장
        }


        /*val storageRef = storage.reference
        var ref = storageRef.child("photo2/" + "${cuser!!.uid}" + "/" + "${title}" + "/" + "1")
        ref.getDownloadUrl().addOnSuccessListener(OnSuccessListener<Any> { uri ->
            val imageURL = uri.toString()
            Glide.with(this).load(imageURL).into(cameraimg)
        }).addOnFailureListener(OnFailureListener {
            print("download failed")
        })/////스토리지에 있는 1 화면에 띄우기*/

    }


    private fun takePhotoFromGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE)
        intent.setType("image/*")
        startActivityForResult(intent, PICK_IMAGE)
    }
    fun getImageUriFromBitmap(context: Context, bitmap: Bitmap): Uri {
        val bytes = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path = MediaStore.Images.Media.insertImage(context.contentResolver, bitmap, "Title", null)
        return Uri.parse(path.toString())
    }
    fun deletedata(){
        fun EncodeString(title:String):String {
            return title.replace(".", " ")
        }
        val bookinfo by lazy { intent.extras!!["result"] as BookInfo }
        var title=EncodeString(bookinfo.title)
        FirebaseStorage.getInstance().getReference().child("photo2").child(auth.uid!!).child(title).child("1").delete()
            .addOnSuccessListener({ taskSnapshot ->
                Toast.makeText(baseContext, "success!", Toast.LENGTH_SHORT).show()
            })
            .addOnFailureListener(OnFailureListener {
                Toast.makeText(baseContext, "fail!", Toast.LENGTH_SHORT).show()
            })
    }


}
/*Bitmap newImage=Bitmap.createBitmap(bitmap).copy(Config.ARGB_8888,true)
        Canvas canvas=new Canvas(newImage)
        canvas.drawCircle(100,100,50,pnt)
        canvas.drawBitmap(bitImage,x,y,pnt)*/