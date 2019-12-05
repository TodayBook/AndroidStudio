package com.example.todaybook.utils

import com.example.todaybook.mapsmodel.ResponseBody
import com.example.todaybook.mapsmodel.ResponseBodyBox
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

//retrofit에서 사용할 url들을 선언

interface RetrofitInterface {

    @GET("lbrry-std")
    fun getItems(
        @Query("serviceKey") serviceKey: String,
        @Query("ctprvnNm") cityName: String,
        @Query("signguNm") sigungu: String,
        @Query("type") type: String = "json",
        @Query("pageNo") page: Int = 1,
        @Query("numOfRows") numOfRows: Int = 100
    ): Call<ResponseBodyBox>

    @GET("lbrry-std")
    fun getItem(
        @Query("serviceKey") serviceKey: String,
        @Query("lbrryNm") libName: String,
        @Query("type") type: String = "json",
        @Query("pageNo") page: Int = 1
    ): Call<ResponseBodyBox>
}