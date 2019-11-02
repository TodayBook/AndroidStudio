package com.example.todaybook


import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_search.*
import kotlinx.android.synthetic.main.search_listview_item.*
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder








class SearchActivity : AppCompatActivity() {

    /*var bookList = arrayListOf<Book>(
        Book("데이터베이스기초", "황수찬", "1", "dog00"),
        Book("이산수학", "이인복", "2", "dog01"),
        Book("대중문화의 이해", "이승준", "3", "dog02"),
        Book("컴퓨터구조론", "길현영", "4", "dog03"),
        Book("사회봉사", "고병무", "5", "dog04")
    )*/


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)


        var jsonresult=getjson()
        var gson = Gson()
        var gsonresult=gson.fromJson(jsonresult,Book::class.java)////json을 gson으로 convert

        val bookAdapter = SearchListviewAdapter(this, gsonresult)
        mainListView.adapter = bookAdapter

        }



    /*override*/ fun getjson():String{/////json파일 받아오는 함수

        val clientId =  "pQ0v12xLr1Lcz3qYOVVx"
        val clientSecret =  "aymtkJ9CUp"
        try {

            var text = URLEncoder.encode("알라딘", "UTF-8")
            val apiURL = "https://openapi.naver.com/v1/search/book?query=" + text
            val url = URL(apiURL)
            val con = url.openConnection() as HttpURLConnection
            con.setRequestMethod("GET")
            con.setRequestProperty("X-Naver-Client-Id", clientId)
            con.setRequestProperty("X-Naver-Client-Secret", clientSecret)
            val responseCode = con.responseCode
            val br: BufferedReader
            if (responseCode == 200) { // 정상 호출
                br = BufferedReader(InputStreamReader(con.inputStream))
            } else {  // 에러 발생
                br = BufferedReader(InputStreamReader(con.errorStream))
            }
            var inputLine: String
            val response = StringBuffer()
            do {
                inputLine = br.readLine()
                response.append(inputLine)
            } while (inputLine != null)

            br.close()
            return(response.toString())

        } catch (e: Exception) {
            return(e.toString())
        }
    }


    }






