package com.example.todaybook

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_search.*

class EmailPasswordActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_email_password)
        auth = FirebaseAuth.getInstance()
    }
    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        //updateUI(currentUser)
    }
    private fun createUserId(email:String, password:String){
        auth.createUserWithEmailAndPassword(email,password)
            .addOnCompleteListener(this) { task->
                if(task.isSuccessful){
                    TextView.text = "셍성 성공"
                    val user=auth.currentUser
                }else{
                    //아이디 생성이 실패했을 경우
                }
            }
    }
    private fun loginUserId(email:String, password: String){
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this){
                if(it.isSuccessful){
                    TextView.text = "로그인 성공"
                }else{
                    //로그인 실패시 이벤트 발생
                }
            }
    }

}
