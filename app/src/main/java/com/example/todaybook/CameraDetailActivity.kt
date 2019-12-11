package com.example.todaybook

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_camera_detail.*

class CameraDetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera_detail)
        val testing by lazy { intent.extras!!["picture"]}
        var a=testing.toString()
        testt.setText("$a")
    }

}
