package com.example.todaybook

import android.app.*
import android.app.usage.UsageStats
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Process
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*
import android.app.NotificationManager
import android.app.NotificationChannel
import android.app.PendingIntent.getActivity
import com.bumptech.glide.Glide
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import java.text.ParseException
import java.text.SimpleDateFormat


class MainActivity : AppCompatActivity() {
    val database = FirebaseDatabase.getInstance().reference
    val cuser = FirebaseAuth.getInstance().currentUser
    val storage = FirebaseStorage.getInstance()
    companion object {
        const val TAG = "MainActivity"
        const val NOTIFICATION_ID = 1001
}



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val storageRef = storage.reference
        if(cuser!=null) {
            var ref = storageRef.child("profileimg/" + cuser.uid)
            ref.getDownloadUrl().addOnSuccessListener(OnSuccessListener<Any> { uri ->
                val imageURL = uri.toString()
                Glide.with(this).load(imageURL).into(bt_profile)
            }).addOnFailureListener(OnFailureListener {
                print("download failed")
            })
        }
            bt_mylib.setOnClickListener {
                if(cuser!=null) {
                    val detailIntent = Intent(this, MylibActivity::class.java)
                    startActivityForResult(detailIntent, 1)
                }
            }
            bt_friendlib.setOnClickListener {
                if(cuser!=null) {
                    val detailIntent = Intent(this, friendList::class.java)
                    startActivityForResult(detailIntent, 1)
                }
            }
            bt_profile.setOnClickListener {
                if(cuser!=null) {
                    val detailIntent = Intent(this, profile::class.java)
                    startActivityForResult(detailIntent, 1)
                }
            }

        if(cuser==null){
            val detailIntent = Intent(this, login::class.java)
            startActivityForResult(detailIntent, 1)
        }
        if (!checkForPermission()) {
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
        else {
            /*val usageStats = getAppUsageStats()
            showAppUsageStats(usageStats)*/


            /*if (showAppUsageStats(usageStats) != 0.0) {
                if (showAppUsageStats(usageStats) > 100) {
                    createNotificationChannel(
                        this, NotificationManagerCompat.IMPORTANCE_DEFAULT,
                        false, getString(R.string.app_name), "App notification channel"
                    )

                    val channelId = "$packageName-${getString(R.string.app_name)}"
                    val title = "Today Book"
                    val content = "도서관을 방문한지 너무 오래됐어요!"

                    val intent = Intent(baseContext, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    val pendingIntent = PendingIntent.getActivity(
                        baseContext, 0,
                        intent, PendingIntent.FLAG_UPDATE_CURRENT
                    )

                    val builder = NotificationCompat.Builder(this, channelId)
                    builder.setSmallIcon(R.drawable.icon)
                    builder.setContentTitle(title)
                    builder.setContentText(content)
                    builder.priority = NotificationCompat.PRIORITY_DEFAULT
                    builder.setAutoCancel(true)
                    builder.setContentIntent(pendingIntent)

                    val notificationManager = NotificationManagerCompat.from(this)
                    notificationManager.notify(NOTIFICATION_ID, builder.build())
                }
            }*/
        }
                 bt_Search.setOnClickListener {
                var bookTitle = edit_title.text.toString()
                if (bookTitle.length <= 0) {
                    Toast.makeText(baseContext, "제목을 입력하세요", Toast.LENGTH_SHORT).show()
                    val detailIntent = Intent(this, MainActivity::class.java)
                    startActivityForResult(detailIntent, 1)
                } else {
                    val detailIntent = Intent(this, SearchActivity::class.java)
                    detailIntent.putExtra("BookTitle", bookTitle)
                    startActivityForResult(detailIntent, 1)

                    edit_title.text.clear()

                }


            }


            bt_map.setOnClickListener {
                val detailIntent = Intent(this, MapsActivity::class.java)
                startActivityForResult(detailIntent, 1)
            }

            bt_login.setOnClickListener {
                val detailIntent = Intent(this, login::class.java)
                startActivityForResult(detailIntent, 1)
            }

    }

    override fun onBackPressed() {
        finish()
        super.onBackPressed()
    }

    private fun checkForPermission(): Boolean {
        val appOps = getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
        val mode = appOps.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, Process.myUid(), packageName)
        return mode == AppOpsManager.MODE_ALLOWED
    }

    private fun getAppUsageStats(): MutableList<UsageStats> {
        val cal = Calendar.getInstance()
        cal.add(Calendar.DATE, -1)    // 1

        val usageStatsManager =
            getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager // 2
        val queryUsageStats = usageStatsManager.queryUsageStats(
            UsageStatsManager.INTERVAL_DAILY, cal.timeInMillis, System.currentTimeMillis() // 3
        )
        return queryUsageStats
    }

    private fun showAppUsageStats(usageStats: MutableList<UsageStats>):Double {
        usageStats.sortWith(Comparator { right, left ->
            compareValues(left.lastTimeUsed, right.lastTimeUsed)
        })
        var duration=0.0
        usageStats.forEach { it ->
            /*var dataFormat = SimpleDateFormat("yyyyMMddHHmmss", Locale.KOREA)
            dataFormat.setTimeZone(TimeZone.getTimeZone("UTC"))
            var minus:Double=dataFormat.format(Date(System.currentTimeMillis())).toDouble()-dataFormat.format(Date(it.lastTimeUsed)).toDouble()
            Log.d(TAG, "packageName: ${it.packageName}, lastTimeUsed: ${Date(it.lastTimeUsed)}, " +
                    "totalTimeInForeground: ${it.totalTimeInForeground},${dataFormat.format(Date(System.currentTimeMillis()))},${dataFormat.format(Date(it.lastTimeUsed))},${minus}")*/
            if(it.packageName==getApplicationContext().getPackageName()){
                var dateInMillis = System.currentTimeMillis()
                var dataFormat = SimpleDateFormat("yyyyMMddHHmmss", Locale.KOREA)
                dataFormat.setTimeZone(TimeZone.getTimeZone("UTC"))
                var lasttime=dataFormat.format(Date(it.lastTimeUsed))
                var currenttime=dataFormat.format(Date(System.currentTimeMillis()))
                duration=currenttime.toDouble()-lasttime.toDouble()
            }
        }
        return duration
    }

    fun TimeZone() {
    }
    @Throws(ParseException::class)
    fun convertTimeZone(time:String):String {
        val form = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
        val inputFormat = SimpleDateFormat(form, Locale.KOREA)
        inputFormat.setTimeZone(TimeZone.getTimeZone("Etc/UTC"))
        val outputFormat = SimpleDateFormat(form)
        // Adjust locale and zone appropriately
        val date = inputFormat.parse(time)
        val outputText = outputFormat.format(date)
        return outputText
    }

    private fun createNotificationChannel(context: Context, importance: Int, showBadge: Boolean,
                                          name: String, description: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = "${context.packageName}-$name"
            val channel = NotificationChannel(channelId, name, importance)
            channel.description = description
            channel.setShowBadge(showBadge)

            val notificationManager = context.getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }

}