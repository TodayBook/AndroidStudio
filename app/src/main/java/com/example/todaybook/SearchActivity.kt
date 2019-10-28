package com.example.todaybook


import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_search.*
import java.net.HttpURLConnection
import java.net.URL
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.net.URLEncoder



class SearchActivity : AppCompatActivity() {

    var bookList=arrayListOf<Book>(
        Book("데이터베이스기초", "황수찬", "1", "dog00"),
        Book("이산수학", "이인복", "2", "dog01"),
        Book("대중문화의 이해", "이승준", "3", "dog02"),
        Book("컴퓨터구조론", "길현영", "4", "dog03"),
        Book("사회봉사", "고병무", "5", "dog04")

    )


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        /*val bookAdapter = SearchListviewAdapter(this, bookList)
        mainListView.adapter = bookAdapter*/
        val bookTitle:String? = intent.getStringExtra("BookTitle").toString()
        /*edit_title.text = bookTitle*/


    }


    fun getJSON() {
        val thread = Thread(object:Runnable {
            override fun run() {
                var result:String
                try
                {
                    val SearchURL = "https://openapi.naver.com/v1/search/book.json?"
                    var bookname=edit_title.text.toString()
                    val RequestURL=SearchURL+"query="+ URLEncoder.encode(bookname, "UTF-8")
                    val url = URL(RequestURL)
                    var clientId = "pQ0v12xLr1Lcz3qYOVVx"
                    var clientSecret = "aymtkJ9CUp"

                    val httpURLConnection = url.openConnection() as HttpURLConnection
                    httpURLConnection.setReadTimeout(3000)
                    httpURLConnection.setConnectTimeout(3000)
                    httpURLConnection.setDoOutput(true)
                    httpURLConnection.setDoInput(true)
                    httpURLConnection.setRequestMethod("GET")
                    httpURLConnection.setUseCaches(false)
                    httpURLConnection.setRequestProperty("X-Naver-Client-Id", clientId);
                    httpURLConnection.setRequestProperty("X-Naver-Client-Secret", clientSecret);
                    httpURLConnection.connect()
                    val responseStatusCode = httpURLConnection.getResponseCode()
                    val inputStream:InputStream
                    if (responseStatusCode == HttpURLConnection.HTTP_OK) {
                        inputStream = httpURLConnection.getInputStream()
                    }
                    else {
                        inputStream = httpURLConnection.getErrorStream()
                    }
                    val inputStreamReader = InputStreamReader(inputStream, "UTF-8")
                    val bufferedReader = BufferedReader(inputStreamReader)
                    val sb = StringBuilder()
                    var line:String
                    do{
                        line=bufferedReader.readLine()
                        sb.append(line)
                    }while(line!=null)
                    bufferedReader.close()
                    httpURLConnection.disconnect()
                    result = sb.toString().trim({ it <= ' ' })
                }
                catch (e:Exception) {
                    result = e.toString()
                }


            }

        })
        thread.start()
    }



}



