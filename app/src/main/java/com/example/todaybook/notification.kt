package com.example.todaybook

import android.app.AppOpsManager
import android.app.AppOpsManager.MODE_ALLOWED
import android.app.AppOpsManager.OPSTR_GET_USAGE_STATS
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Toast

class notification : AppCompatActivity() {
    val TAG = "MyMessage"

    /*override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notification)


        if (!checkForPermission())
        {
            Log.i(TAG, "The user may not allow the access to apps usage. ")
            Toast.makeText(
                this,
                ("Failed to retrieve app usage statistics. " +
                        "You may need to enable access for this app through " +
                        "Settings > Security > Apps with usage access"),
                Toast.LENGTH_LONG
            ).show()
            startActivity(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS))
        }
        else
        {
            // We have the permission. Query app usage stats.
        }
    }

    private fun checkForPermission(): Boolean {
        val appOps = getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
        val mode = appOps.checkOpNoThrow(OPSTR_GET_USAGE_STATS, Process.myUid(), packageName)
        return mode == MODE_ALLOWED
    }*/
}
