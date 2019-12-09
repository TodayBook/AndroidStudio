package com.example.todaybook

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.Gson
import okhttp3.*
import java.io.IOException

class FcmPush {
    val JSON = MediaType.parse("application/json; charset=utf-8")//Post전송 JSON Type
    val url = "https://fcm.googleapis.com/fcm/send" //FCM HTTP를 호출하는 URL
    val serverKey =
        "AAAATaipIik:APA91bF8i3igWMaOPBxWu7sDyXF28BaqDazZP4JwlLV1BuXuPjw9UTmoL2Pn0ZUgg6uzIre4jazuwYwW1e7IBcntURiL0JUqO_GSkPXOL3ywX4BzZULbjWkva_GIJvobXGWefYMKpsiQ"
    //Firebase에서 복사한 서버키
    var okHttpClient: OkHttpClient
    var gson: Gson

    init {
        gson = Gson()
        okHttpClient = OkHttpClient()
    }

    fun sendMessage(destinationUid: String, title: String, message: String,clickaction:String) {
        FirebaseFirestore.getInstance().collection("pushtokens").document(destinationUid).get()//destinationUid의 값으로 푸시를 보낼 토큰값을 가져오는 코드
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    var token = task.result!!["pushtoken"].toString()
                    Log.i("토큰정보", token)
                    var pushDTO = PushDTO()
                    pushDTO.to = token                   //푸시토큰 세팅
                    pushDTO.notification?.title = title  //푸시 타이틀 세팅
                    pushDTO.notification?.body = message //푸시 메시지 세팅
                    pushDTO.notification?.click_action = clickaction

                    var body = RequestBody.create(JSON, gson?.toJson(pushDTO)!!)
                    var request = Request
                        .Builder()
                        .addHeader("Content-Type", "application/json")
                        .addHeader("Authorization", "key=" + serverKey)
                        .url(url)       //푸시 URL 세팅
                        .post(body)     //pushDTO가 담긴 body 세팅
                        .build()
                    okHttpClient?.newCall(request)?.enqueue(object : Callback {//푸시 전송
                    override fun onFailure(call: Call?, e: IOException?) {
                        println("failure")
                    }

                        override fun onResponse(call: Call?, response: Response?) {
                            println(response?.body()?.string())  //요청이 성공했을 경우 결과값 출력
                        }
                    })
                }
            }
    }

}