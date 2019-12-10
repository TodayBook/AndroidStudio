package com.example.todaybook.utils

import android.util.Log
import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

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