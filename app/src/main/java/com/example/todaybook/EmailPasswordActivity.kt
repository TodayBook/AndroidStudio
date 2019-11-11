package com.example.todaybook

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import kotlinx.android.synthetic.main.activity_email_password.*
import com.google.firebase.database.FirebaseDatabase


class EmailPasswordActivity : AppCompatActivity() {
    val database = FirebaseDatabase.getInstance().reference
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_email_password)
        auth = FirebaseAuth.getInstance()
        email_login_button.setOnClickListener {
            createUser(editText_email.text.toString(),getEditText_password.text.toString(),Userid_text.text.toString())
        }
    }
    private fun createUser(email:String,password:String,UserId:String){
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {database.child("users").child(auth.uid!!).child("UserId").setValue(UserId)

                    database.child("UserId").child(UserId).setValue(auth.uid!!)
                    Toast.makeText(baseContext, "Success!!", Toast.LENGTH_SHORT).show()
                    editText_email.text.clear()
                    getEditText_password.text.clear()
                    Userid_text.text.clear()
                    val loginIntent = Intent(this,login::class.java)
                    startActivityForResult(loginIntent,1)

                } else {
                    Toast.makeText(baseContext, "Authentication failed.", Toast.LENGTH_SHORT).show()
                }
            }


    }
}
