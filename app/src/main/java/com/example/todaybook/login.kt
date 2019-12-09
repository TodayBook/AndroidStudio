package com.example.todaybook

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.iid.FirebaseInstanceId
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.create_account_pop.*
import androidx.core.app.ComponentActivity.ExtraData
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T



class login : AppCompatActivity() {
    val database = FirebaseDatabase.getInstance().reference
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        auth = FirebaseAuth.getInstance()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        bt_create.setOnClickListener{
                val builder = AlertDialog.Builder(this)
                val dialogView = layoutInflater.inflate(R.layout.create_account_pop, null)
                val dialog_userid = dialogView.findViewById<EditText>(R.id.pop_Userid)
                val dialog_email = dialogView.findViewById<EditText>(R.id.pop_email)
                val dialog_psd = dialogView.findViewById<EditText>(R.id.pop_password)

            builder.setView(dialogView)
                    .setPositiveButton("확인") { dialogInterface, i ->
                        print("okk")
                        createUser(dialog_email.text.toString(),dialog_psd.text.toString(),dialog_userid.text.toString())
                    }
                    .setNegativeButton("취소") { dialogInterface, i ->
                        /* 취소일 때 아무 액션이 없으므로 빈칸 */
                    }
                    .show()
        }
        bt_login.setOnClickListener {
            println(edit_email.text.toString())
            loginUser(edit_email.text.toString(),editText_password.text.toString())
        }
    }
    private fun loginUser(email:String,password:String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Toast.makeText(baseContext, "Authentication succeed.", Toast.LENGTH_SHORT).show()
                    val intent = Intent()
                    setResult(Activity.RESULT_OK, intent)
                    finish()
                } else {
                    Toast.makeText(baseContext, "Authentication failed.", Toast.LENGTH_SHORT).show()
                }
            }
    }
    private fun createUser(email:String,password:String,UserId:String){
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    database.child("users").child(auth.uid!!).child("UserId").setValue(UserId)
                    database.child("users").child(auth.uid!!).child("private").setValue("false")
                    database.child("UserId").child(UserId).child("uid").setValue(auth.uid!!)
                    Toast.makeText(baseContext, "Success!!", Toast.LENGTH_SHORT).show()
                    registerPushToken()
                } else {
                    Toast.makeText(baseContext, "Authentication failed.", Toast.LENGTH_SHORT).show()
                }
            }


    }
    private fun registerPushToken() {
        var pushToken: String? = null
        var uid = FirebaseAuth.getInstance().currentUser!!.uid
        var map = mutableMapOf<String, Any>()
        FirebaseInstanceId.getInstance().instanceId.addOnSuccessListener { instanceIdResult ->
            pushToken = instanceIdResult.token
            map["pushtoken"] = pushToken!!
            FirebaseFirestore.getInstance().collection("pushtokens").document(uid!!).set(map)
        }
    }
}
