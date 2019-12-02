/*package com.example.todaybook


import android.util.Log
import com.google.firebase.messaging.RemoteMessage
import android.content.Context.NOTIFICATION_SERVICE
import android.app.NotificationManager
import android.media.RingtoneManager
import android.app.PendingIntent
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.support.v4.app.NotificationCompat
import android.widget.Toast
import com.google.firebase.messaging.FirebaseMessagingService






class MyFirebaseMessagingService : FirebaseMessagingService() {

    private val TAG = "MessagingService"

    override fun onMessageReceived(remoteMessage: RemoteMessage?) {

        if (remoteMessage!!.notification != null) {
            // do with Notification payload...
            // remoteMessage.notification.body
            //알림 정보를 가지고 여기서 지지고 볶고 하라는 것이다. 포그라운드일때 수신? 그래서 추가 작업
            Log.d(TAG, "From: " + remoteMessage!!.from)
            Log.d(TAG, "${remoteMessage.notification?.body}")

            sendNotification(remoteMessage.notification?.body.toString())
        }

        if (remoteMessage.data.isNotEmpty()) {
            // do with Data payload...
            // remoteMessage.data
            Log.d(TAG, "${remoteMessage.data} : 이것은 data입니다.")

        }
    }

    fun sendNotification(messageBody : String){
        TODO()
    }
}*/


