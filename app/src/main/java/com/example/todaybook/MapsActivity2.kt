package com.example.todaybook

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.TextView
import com.example.todaybook.R.id
import com.example.todaybook.R.layout

class MapsActivity2 : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layout.activity_maps2)
        var title = ""
        var address = ""
        val extras: Bundle? = getIntent().getExtras()
        if (extras == null) {
            title = "error"
        } else {
            val title = extras.getString("title")
            val address = extras.getString("address")
        }
        val textView =
            findViewById(id.textView_newActivity_contentString) as TextView
        val str = title + '\n'.toString() + address + '\n'
        textView.text = str
    }
}