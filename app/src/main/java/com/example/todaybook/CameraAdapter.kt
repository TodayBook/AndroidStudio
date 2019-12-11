package com.example.todaybook


import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import org.jetbrains.anko.db.NULL

class CameraAdapter(val context: Context, private val CameraDataModelList: ArrayList<CameraDataModel>,val itemClick: (CameraDataModel) -> Unit) : RecyclerView.Adapter<CameraAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =  LayoutInflater.from(parent.context).inflate(R.layout.camera_listview_item, parent, false)
        return ViewHolder(view,itemClick)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItems(CameraDataModelList[position])
    }

    override fun getItemCount(): Int {
        return CameraDataModelList.size
    }

    inner class ViewHolder(itemView: View, itemClick : (CameraDataModel)-> Unit ): RecyclerView.ViewHolder(itemView) {

        fun bindItems(CameraDataModel: CameraDataModel) {
            val imageView = itemView.findViewById<ImageView>(R.id.cameraimg)

            /*val imageBitmap:Bitmap? = StringToBitmap(CameraDataModel.photourl)
            imageView.setImageBitmap(imageBitmap)*/

            /*val uri= Uri.parse(CameraDataModel.photourl)
            imageView.setImageURI(uri)*/

            val cuser = FirebaseAuth.getInstance().currentUser
            val storage = FirebaseStorage.getInstance()
            val storageRef = storage.reference

            /*if(cuser!=null) {
                val fs = FirebaseStorage.getInstance()
                val imagesRef = storageRef.child("photo/" + cuser!!.uid + CameraDataModel.title)
                Glide.with(itemView.context)
                    .load(imagesRef)
                    .into(imageView)
            }*/


            /*val i=0
            if(cuser!=null) {
                while(i<count) {
                    var ref = storageRef.child("photo/" + "${cuser.uid}" + "/" + "${CameraDataModel.title}" + "/" + "KakaoTalk_20181210_010920433.jpg")
                    /*var ref = storageRef.child("photo/" + "KakaoTalk_20181210_010920433.jpg")*/////얘됐음
                    /*var ref = storageRef.child("profileimg/" + cuser.uid)*////얘됐음
                    ref.getDownloadUrl().addOnSuccessListener(OnSuccessListener<Any> { uri ->
                        val imageURL = uri.toString()
                        Glide.with(itemView.context).load(imageURL).into(imageView)
                    }).addOnFailureListener(OnFailureListener {
                        print("download failed")
                    })
                }
            }*/

            /*Glide.with(itemView.context).load(CameraDataModel.url).into(imageView)*/

            itemView.setOnClickListener { itemClick(CameraDataModel) }
        }
    }
    fun StringToBitmap(encodedString:String):Bitmap? {
        try
        {
            val encodeByte = Base64.decode(encodedString, Base64.DEFAULT)
            val bitmap = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.size)
            return bitmap
        }
        catch (e:Exception) {
            e.message
            return null
        }
    }
}