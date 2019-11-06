package com.example.todaybook


import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.activity_search.*
import kotlinx.android.synthetic.main.search_listview_item.*
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import com.google.gson.JsonParser
import com.google.gson.JsonObject
import com.google.gson.annotations.SerializedName
import kotlinx.android.synthetic.main.activity_main.*
import android.content.ClipData.Item
import com.google.gson.JsonArray
import android.R.array








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
        var intent = getIntent()
        var title = intent.getExtras()?.getString("BookTitle")

        AsyncTaskBook().execute(title)
    }



    inner class AsyncTaskBook: AsyncTask<String, String, String>() {

        override fun onPreExecute() {
            super.onPreExecute()
        }

        override fun doInBackground(vararg params: String): String {
            val Authorization = "KakaoAK 7cb365cb29f6b70a0778882b0487c435"
            try {

                var text = URLEncoder.encode(params[0], "UTF-8")
                val apiURL = "https://dapi.kakao.com/v3/search/book?query=" + text
                val url = URL(apiURL)
                val con = url.openConnection() as HttpURLConnection
                con.setRequestMethod("GET")
                con.setRequestProperty("Authorization", Authorization)
                val responseCode = con.responseCode
                val br: BufferedReader
                if (responseCode == 200) { // 정상 호출
                    br = BufferedReader(InputStreamReader(con.inputStream))
                    return br.readLine()
                    br.close()
                } else {  // 에러 발생
                    br = BufferedReader(InputStreamReader(con.errorStream))
                    return br.toString()
                }
                /*var inputLine: String
                val response = StringBuffer()
                do {
                    if (br.readLine() == null){
                        break
                    }
                    inputLine = br.readLine()
                    response.append(inputLine)
                } while (inputLine != null)

                br.close()
                return (response.toString())*/

            } catch (e: Exception) {
                return (e.toString())
            }
        }
        override fun onPostExecute(result: String?) {

            var json=result


            /*val jObject = JSONObject(json)
            var jsonresult = jObject.getJSONArray("documents")*///방법1

            /*val parser=JsonParser()
            val rootObj=parser.parse(json)////type=JsonElement
                .getAsJsonObject().get("documents")*////방법2

            /*textView3.setText(rootObj.toString())*/

            val gson = GsonBuilder().create()
            var gsonresult=gson.fromJson(json,FocusArea::class.java)////json을 gson으로 convert(jsonresult의 값을 book object로)


            /*var bookList=arrayListOf<book>(gsonresult)*/
            val bookAdapter = SearchListviewAdapter(this@SearchActivity, gsonresult.documentsValue)
            mainListView.adapter = bookAdapter
        }
    }
}