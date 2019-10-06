package com.example.todaybook

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*

class login : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        auth = FirebaseAuth.getInstance()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        bt_create.setOnClickListener{
            val detailIntent = Intent(this,EmailPasswordActivity::class.java)
            startActivityForResult(detailIntent,1)
        }
        bt_login.setOnClickListener {
            loginUser(editText_email.text.toString(),editText_password.text.toString())
        }
    }
    private fun loginUser(email:String,password:String) {
        val currentUser = auth.currentUser
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Toast.makeText(baseContext, "Authentication succeed.", Toast.LENGTH_SHORT).show()
                    val user = auth.currentUser
                } else {
                    // If sign in fails, display a message to the user.
                    Toast.makeText(baseContext, "Authentication failed.", Toast.LENGTH_SHORT).show()
                }
            }
    }
}
