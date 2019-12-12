package com.example.todaybook.utils

import android.util.Log
import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
//retrofit 통신
//retrofit을 처음 호출할때만 객체를 생성하고
//다음 호출할때부터는 사용하던 객체를 사용하면 됨 (싱글톤)
class NetworkHelper {
    companion object {
        private var retrofit: Retrofit? = null
        private val baseUrl = "http://api.data.go.kr/openapi/"
        val networkInstance: RetrofitInterface?
            get() {
                val gson = GsonBuilder()
                    .setLenient()
                    .create()
                if (retrofit == null)
                    retrofit = Retrofit.Builder()
                        .baseUrl(baseUrl)

                        .addConverterFactory(GsonConverterFactory.create(gson))
                        .build()
                return retrofit?.create(RetrofitInterface::class.java)
            }
    }

}