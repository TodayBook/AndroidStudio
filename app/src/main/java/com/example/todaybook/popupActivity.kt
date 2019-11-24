package com.example.todaybook

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log.d
import android.view.ContextThemeWrapper
import android.view.View
import android.view.View.OnClickListener
import android.widget.EditText
import android.widget.FrameLayout
import androidx.appcompat.app.AlertDialog
import kotlinx.android.synthetic.main.activity_read_or_read.*

class popupActivity : AppCompatActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_read_or_read)
        bt_didbook.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            val dialogView = layoutInflater.inflate(R.layout.activity_popup, null)
            /*val dialogText = dialogView.findViewById<EditText>(R.id.dialogEt)*/

            builder.setView(dialogView)
                .setPositiveButton("머물기") { dialogInterface, i ->
                    /* 취소일 때 아무 액션이 없으므로 빈칸 */
                }
                .setNegativeButton("나의도서관으로 이동") { dialogInterface, i ->
                    val detailIntent = Intent(this, MylibActivity::class.java)
                    startActivityForResult(detailIntent, 1)
                    /* 확인일 때 main의 View의 값에 dialog View에 있는 값을 적용 */

                }

                .show()
        }
    }

}

