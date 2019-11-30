package com.example.todaybook

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import android.widget.TextView
import com.example.todaybook.R.id
import com.example.todaybook.R.layout
import com.example.todaybook.mapsmodel.Library
import com.example.todaybook.mapsmodel.ResponseBodyBox
import com.example.todaybook.utils.NetworkHelper
import kotlinx.android.synthetic.main.activity_maps2.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.net.URLDecoder


class NewActivity : AppCompatActivity() {
    val serviceKey =
        "QgXDwOsi6tiCBiPIVhuyQCouyE%2BDemjMgKb3rf8BVXuVbJur6V%2BH%2BvuvKc7QCJqGE72AmxY5K2LbWrvXANV3hw%3D%3D"

    lateinit var lib: Library
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layout.activity_maps2)

        var libName: String = intent.getStringExtra("title")
        val str = URLDecoder.decode(serviceKey, "UTF-8")
        Log.e("asd", libName)

        //도서관 이름을 이용하여 retrofit 통신을 하는 코드입니다.
        //다른 부분들은 아까와 같습니다.
        NetworkHelper.networkInstance?.getItem(str, libName)?.enqueue(object :
            Callback<ResponseBodyBox> {
            override fun onFailure(call: Call<ResponseBodyBox>, t: Throwable) {
                t.printStackTrace()
                Log.e("Network error", t.message)
            }

            override fun onResponse(
                call: Call<ResponseBodyBox>,
                response: Response<ResponseBodyBox>
            ) {
                if (response.isSuccessful) {
                    Log.e("asdad", response.body()?.response?.header?.resultMsg)
                    response.body()?.response?.body?.items?.get(0)?.let {
                        //Kotlin에서 null 체크를 할 때 이용하는 방식으로 ?.let 일시에 앞에 있는 객체가 null이 아니면
                        //해당 스코프를 실행합니다.
                        lib = it
                    }
                    setInfo()
                }
            }
        })
    }

    //정보를 뷰에 올리는 작업을 하는 함수입니다.
    fun setInfo() {
        libName.text = lib.lbrryNm
        holiday.text = lib.closeDay
        weekdayOpenTime.text = lib.weekdayOperOpenHhmm
        weekdayCloseTime.text = lib.weekdayOperColseHhmm
        satOpenTime.text = lib.satOperOperOpenHhmm
        satCloseTime.text = lib.satOperCloseHhmm
        holidayCloseTime.text = lib.holidayCloseOpenHhmm
        holidayOpenTime.text = lib.holidayOperOpenHhmm
        bookCo.text = "${lib.bookCo}권"
        homepage.text = lib.homepageUrl
        operInstitution.text = lib.operInstitutionNm
        phoneNum.text = lib.phoneNumber
        address.text = lib.rdnmadr
        noneBookCo.text = "${lib.noneBookCo}권"
        lonCo.text = "${lib.lonCo}권"
    }
}