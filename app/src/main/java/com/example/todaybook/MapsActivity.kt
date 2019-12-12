package com.example.todaybook

import android.Manifest.permission
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.View
import android.view.WindowManager.LayoutParams
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityCompat.OnRequestPermissionsResultCallback
import androidx.core.content.ContextCompat
import com.google.android.gms.location.*
import com.google.android.gms.location.LocationSettingsRequest.Builder
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.snackbar.Snackbar
import noman.googleplaces.Place
import noman.googleplaces.PlacesException
import noman.googleplaces.PlacesListener
import java.io.IOException

import kotlin.collections.MutableList
import com.example.todaybook.R.id
import com.example.todaybook.R.layout
import com.example.todaybook.mapsmodel.ResponseBodyBox
import com.example.todaybook.utils.NetworkHelper
import kotlinx.android.synthetic.main.activity_maps.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.net.URLDecoder

import java.util.Locale


class MapsActivity : AppCompatActivity(), OnMapReadyCallback, OnRequestPermissionsResultCallback,
    PlacesListener {
    override fun onPlacesFailure(e: PlacesException?) {}//placelistener에서 요구하는 4개의 메소드들
    override fun onPlacesStart() {}
    override fun onPlacesSuccess(places: List<Place?>?) {
        runOnUiThread {
            for (place in places!!) {
                val latLng =
                    LatLng(
                        place!!.latitude //!!을 씀으로써 null이 아니라고 선언 , 다른거 쓰면 에러,,
                        , place.longitude
                    )//latLng: 현재 좌표에서 북쪽을 기준으로 지정한 각도(시계 방향)와 거리만큼 떨어진 위치의 지도 좌표를 반환
                val markerSnippet = getCurrentAddress(latLng)
                val markerOptions =
                    MarkerOptions()
                markerOptions.position(latLng)
                markerOptions.title(place.name)
                markerOptions.snippet(markerSnippet)
                val item: Marker? =
                    mMap!!.addMarker(markerOptions)
                previous_marker!!.add(item)
            }
            //중복마커 제거
            val hashSet =
                java.util.HashSet<Marker?>()
            hashSet.addAll(previous_marker!!)
            previous_marker!!.clear()
            previous_marker!!.addAll(hashSet)
        }
    }

    //api 서비스 키 입니다.
    val serviceKey =
        "tXbnAEdBkbg%2FJDMf%2FIutPVO5IuduRgVGHbjuvYQAkCuml38A7Ms9QCDEmA1b6q%2Fdx7CUIxV9DUenLnlVw5YJHw%3D%3D"


    fun showPlaceInformation(location: LatLng?) {//지도에서 보여주는 부분
        mMap!!.clear()

        val str = URLDecoder.decode(serviceKey, "UTF-8");

        if (previous_marker != null) previous_marker!!.clear()//지역정보 마커 클리어


        //레트로핏 이용해서 통신하는 코드들
        //address가 비어있지 않으면 시도/군구 추출해서 서비스키와함께 공공api 의 도서관데이터 호출
        val address: String = "" + getCurrentAddress(currentPosition)
        if (address != "") {
            val addresses = address.split(" ")
            val cityName = addresses[1]
            val sigungu = addresses[2]

            fun searchLib(str: String, cityName: String, sigungu: String) {
                NetworkHelper.networkInstance?.getItems(str, cityName, sigungu)
                    ?.enqueue(object : Callback<ResponseBodyBox> {
                        override fun onFailure(call: Call<ResponseBodyBox>, t: Throwable) {
                            t.printStackTrace()
                            Log.e("Network error", "" + t.message) //모종의 이유로 데이터호출 실패할경우 이부분 실행

                        }

                        override fun onResponse(
                            call: Call<ResponseBodyBox>,
                            response: Response<ResponseBodyBox>
                        ) {
                            //데이터 호출은 성공하였으나 서버에서 정상적인 데이터가 오지않았을때도 있으므로
                            //성공적인 호춣이었는지 검사하고 데이터들의 리스트를 forEach를 이용하여 마커를 추가

                            if (response.isSuccessful) {
                                response.body()?.response?.body?.items?.forEach {
                                    val position =
                                        LatLng(it.latitude.toDouble(), it.hardness.toDouble())
                                    val libName = it.lbrryNm
                                    val libAddress = it.rdnmadr
                                    Log.e("asd", libName)
                                    val markerOptions = MarkerOptions()
                                    markerOptions.run {
                                        position(position)
                                        title(libName)
                                        snippet(libAddress)
                                    }
                                    mMap!!.addMarker(markerOptions)
                                }
                            }
                        }

                    })
            }
            searchLib(str, cityName, sigungu)
            searchLib(str, cityName, sigungu + " " + addresses[3])
            searchLib(str, cityName, cityName + " " + sigungu)//어떤건 시/ 어떤건 시+구 로 검색값이 달라서 인자 다른거 넣어줌
            Log.i("Address", addresses.toString())
            Log.i("cityName", cityName)
            Log.i("sigungu", sigungu)
        }
    }


    override fun onPlacesFinished() {}
    private var mMap: GoogleMap? = null
    private var currentMarker: Marker? = null
    internal var needRequest = false
    // 앱을 실행하기 위해 필요한 퍼미션을 정의
    internal var REQUIRED_PERMISSIONS: Array<String?>? = arrayOf(
        permission.ACCESS_FINE_LOCATION,
        permission.ACCESS_COARSE_LOCATION
    )  // 외부 저장소

    internal var mCurrentLocatiion: Location? = null
    internal var currentPosition: LatLng? = null
    private var mFusedLocationClient: FusedLocationProviderClient? =
        null
    private var locationRequest: LocationRequest? = null
    private var location: Location? = null
    private var mLayout
            : View? = null

    internal var previous_marker: MutableList<Marker?>? =
        null //위 placelistener에 필요한 변수

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(
            LayoutParams.FLAG_KEEP_SCREEN_ON,
            LayoutParams.FLAG_KEEP_SCREEN_ON
        )
        setContentView(layout.activity_maps)
        previous_marker = java.util.ArrayList()
        // xml 아이디로 바로 호출이 가능합니다
        //전엔 사용했지만 findViewById를 사용하지 않음
        button.setOnClickListener { showPlaceInformation(currentPosition) }
        locationRequest = LocationRequest()
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
            .setInterval(UPDATE_INTERVAL_MS.toLong())
            .setFastestInterval(FASTEST_UPDATE_INTERVAL_MS.toLong())
        val builder = Builder()
        builder.addLocationRequest(LocationRequest())
        mFusedLocationClient =
            LocationServices.getFusedLocationProviderClient(this)
        val mapFragment =
            supportFragmentManager
                .findFragmentById(id.maps) as SupportMapFragment?
        mapFragment!!.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap?) {
        Log.d(MapsActivity.TAG, "onMapReady :")
        mMap = googleMap
        mMap!!.setOnInfoWindowClickListener { marker ->
            var intent = Intent(this@MapsActivity, MapsActivity2::class.java)
            var title: String? = marker.title
            intent.putExtra("title", title)
            startActivity(intent)
        }


        setDefaultLocation() //지도를 서울로 이동


        //런타임 퍼미션 처리
        //1. 위치 퍼미션 갖고있는지 체크
        val hasFineLocationPermission =
            ContextCompat.checkSelfPermission(
                this,
                permission.ACCESS_FINE_LOCATION
            )
        val hasCoarseLocationPermission =
            ContextCompat.checkSelfPermission(
                this,
                permission.ACCESS_COARSE_LOCATION
            )
        //2. 퍼미션을 갖고 있다면
        if (hasFineLocationPermission == PackageManager.PERMISSION_GRANTED &&
            hasCoarseLocationPermission == PackageManager.PERMISSION_GRANTED
        ) {
            startLocationUpdates() //3.위치 업데이트 시작

            //2-1. 퍼미션 요청한 적 없다면 퍼미션 요청 필요
        } else {
            //첫번째 방법 : 사용자가 퍼미션 거부를 한 적이 있는 경우에는
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    REQUIRED_PERMISSIONS!![0]!!
                )
            ) {

                //요청을 진행하기 전에 사용자가에게 퍼미션이 필요한 이유를 설명해줄 필요있음
                Snackbar.make(
                    mLayout!!, "이 앱을 실행하려면 위치 접근 권한이 필요합니다.",
                    Snackbar.LENGTH_INDEFINITE
                ).setAction("확인") {

                    //사용자게에 퍼미션 요청. 요청 결과는 onRequestPermissionResult에서 수신된다
                    ActivityCompat.requestPermissions(
                        this@MapsActivity,
                        REQUIRED_PERMISSIONS!!,
                        MapsActivity.PERMISSIONS_REQUEST_CODE
                    )
                }.show()
            } else {
                //두번째 방법: 사용자가 퍼미션 거부를 한 적이 없는 경우에는 퍼미션 요청을 바로 한다

                ActivityCompat.requestPermissions(
                    this, REQUIRED_PERMISSIONS!!, MapsActivity.PERMISSIONS_REQUEST_CODE
                )
            }
        }
        mMap!!.uiSettings.isMyLocationButtonEnabled = true
        mMap!!.animateCamera(CameraUpdateFactory.zoomTo(14.5f))
        mMap!!.setOnMapClickListener { Log.d(MapsActivity.TAG, "onMapClick :") }
    }

    internal var locationCallback: LocationCallback? =
        object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) { //onlocation함수는 계속 실행
                super.onLocationResult(locationResult)
                val locationList: List<Location?> =
                    locationResult!!.locations
                if (locationList.size > 0) {
                    location = locationList[locationList.size - 1]
                    currentPosition = LatLng(
                        location!!.latitude,
                        location!!.longitude
                    )
                    val markerTitle = getCurrentAddress(currentPosition)
                    val markerSnippet =
                        "위도:" + location!!.latitude.toString() + " 경도:" + location!!.longitude.toString()
                    Log.d(MapsActivity.TAG, "onLocationResult : $markerSnippet")


                    setCurrentLocation(location, markerTitle, markerSnippet)
                    //카메라고정

                    mCurrentLocatiion = location
                }
            }
        }

    private fun startLocationUpdates() {
        if (!checkLocationServicesStatus()) {
            Log.d(
                MapsActivity.TAG,
                "startLocationUpdates : call showDialogForLocationServiceSetting"
            )
            showDialogForLocationServiceSetting()
        } else {
            val hasFineLocationPermission =
                ContextCompat.checkSelfPermission(
                    this,
                    permission.ACCESS_FINE_LOCATION
                )
            val hasCoarseLocationPermission =
                ContextCompat.checkSelfPermission(
                    this,
                    permission.ACCESS_COARSE_LOCATION
                )
            if (hasFineLocationPermission != PackageManager.PERMISSION_GRANTED ||
                hasCoarseLocationPermission != PackageManager.PERMISSION_GRANTED
            ) {
                Log.d(MapsActivity.TAG, "startLocationUpdates : 퍼미션 안가지고 있음")
                return
            }
            Log.d(
                MapsActivity.TAG,
                "startLocationUpdates : call mFusedLocationClient.requestLocationUpdates"
            )
            mFusedLocationClient!!.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.myLooper()
            )
            if (checkPermission()) mMap!!.isMyLocationEnabled = true
        }
    }

    override fun onStart() {
        super.onStart()
        Log.d(MapsActivity.TAG, "onStart")
        if (checkPermission()) {
            Log.d(
                MapsActivity.TAG,
                "onStart : call mFusedLocationClient.requestLocationUpdates"
            )
            mFusedLocationClient!!.requestLocationUpdates(locationRequest, locationCallback, null)
            if (mMap != null) mMap!!.isMyLocationEnabled = true
        }
    }

    override fun onStop() {
        super.onStop()
        if (mFusedLocationClient != null) {
            Log.d(MapsActivity.TAG, "onStop : call stopLocationUpdates")
            mFusedLocationClient!!.removeLocationUpdates(locationCallback)
        }
    }

    fun getCurrentAddress(latlng: LatLng?): String? {

        //지오코더

        val geocoder =
            Geocoder(this, Locale.getDefault())
        val addresses: List<Address?>?
        try {
            addresses = geocoder.getFromLocation(
                latlng!!.latitude,
                latlng.longitude,
                1
            )
        } catch (ioException: IOException) {
            //네트워크 문제

            Toast.makeText(this, "지오코더 서비스 사용불가", Toast.LENGTH_LONG)
                .show()
            return "지오코더 서비스 사용불가"
        } catch (illegalArgumentException: IllegalArgumentException) {
            Toast.makeText(this, "잘못된 GPS 좌표", Toast.LENGTH_LONG)
                .show()
            return "잘못된 GPS 좌표"
        }
        return if (addresses == null || addresses.size == 0) {
            Toast.makeText(this, "주소 미발견", Toast.LENGTH_LONG).show()
            "주소 미발견"
        } else {
            val address = addresses[0]
            address!!.getAddressLine(0).toString()

        }
    }

    fun checkLocationServicesStatus(): Boolean {
        val locationManager =
            getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER))
    }

    fun setCurrentLocation(
        location: Location?,
        markerTitle: String?,
        markerSnippet: String?
    ) {
        if (currentMarker != null) currentMarker!!.remove()
        val currentLatLng =
            LatLng(
                location!!.latitude,
                location.longitude
            )
        val markerOptions =
            MarkerOptions()


        markerOptions.run { // 같은객체에 대해서 함수 실해시 .run으로 반복코드 지움
            position(currentLatLng)
            title(markerTitle)
            snippet(markerSnippet)
            draggable(true)
        }
        currentMarker = mMap!!.addMarker(markerOptions)
        val cameraUpdate: CameraUpdate? =
            CameraUpdateFactory.newLatLng(currentLatLng)
        mMap!!.moveCamera(cameraUpdate)
    }

    fun setDefaultLocation() {


        //디폴트 위치, Seoul

        val DEFAULT_LOCATION =
            LatLng(37.56, 126.97)
        val markerTitle = "위치정보 가져올 수 없음"
        val markerSnippet = "위치 퍼미션과 GPS 활성 요부 확인하세요"
        if (currentMarker != null) currentMarker!!.remove()
        val markerOptions =
            MarkerOptions()
        markerOptions.run {
            position(DEFAULT_LOCATION)
            title(markerTitle)
            snippet(markerSnippet)
            draggable(true)
            icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
        }
        currentMarker = mMap!!.addMarker(markerOptions)
        val cameraUpdate: CameraUpdate? =
            CameraUpdateFactory.newLatLngZoom(DEFAULT_LOCATION, 15f)
        mMap!!.moveCamera(cameraUpdate)
    }

    //여기부터는 런타임 퍼미션 처리을 위한 메소드들
    private fun checkPermission(): Boolean {
        val hasFineLocationPermission =
            ContextCompat.checkSelfPermission(
                this,
                permission.ACCESS_FINE_LOCATION
            )
        val hasCoarseLocationPermission =
            ContextCompat.checkSelfPermission(
                this,
                permission.ACCESS_COARSE_LOCATION
            )
        return hasFineLocationPermission == PackageManager.PERMISSION_GRANTED &&
                hasCoarseLocationPermission == PackageManager.PERMISSION_GRANTED
    }


    override fun onRequestPermissionsResult(
        permsRequestCode: Int,
        permissions: Array<String?>,
        grandResults: IntArray
    ) {
        if (permsRequestCode == MapsActivity.PERMISSIONS_REQUEST_CODE && grandResults.size == REQUIRED_PERMISSIONS!!.size) {
            //요청 코드가 PERMISSIONS_REQUEST_CODE 이고, 요청한 퍼미션 개수만큼 수신되었다면
            var check_result = true

            //모든 퍼미션 허용했는지 체크

            for (result in grandResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    check_result = false
                    break
                }
            }
            if (check_result) {

                //퍼미션을 허용했다면 위치 업데이트 시작

                startLocationUpdates()
            } else {
                //거부한 퍼미션 있다면 앱 사용할 수 없는 이유 알려주고 앱 종료

                if (ActivityCompat.shouldShowRequestPermissionRationale(
                        this,
                        REQUIRED_PERMISSIONS!![0]!!
                    )
                    || ActivityCompat.shouldShowRequestPermissionRationale(
                        this,
                        REQUIRED_PERMISSIONS!![1]!!
                    )
                ) {
                    //사용자가 거부만 선택한 경우 앱을 다시실행해 허용다시 선택시 앱사용가능
                    Snackbar.make(
                        mLayout!!, "퍼미션이 거부되었습니다. 앱을 다시 실행하여 퍼미션을 허용해주세요. ",
                        Snackbar.LENGTH_INDEFINITE
                    ).setAction("확인") { finish() }.show()
                } else {
                    //"다시 묻지 않음"을 사용자가 체크하고 거부를 선택한 경우에는 설정(앱 정보)에서 퍼미션을 허용해야 앱을 사용할 수 있음
                    Snackbar.make(
                        mLayout!!, "퍼미션이 거부되었습니다. 설정(앱 정보)에서 퍼미션을 허용해야 합니다. ",
                        Snackbar.LENGTH_INDEFINITE
                    ).setAction("확인") { finish() }.show()
                }
            }
        }
    }

    //여기부터는 GPS 활성화를 위한 메소드들
    private fun showDialogForLocationServiceSetting() {
        val builder =
            AlertDialog.Builder(this@MapsActivity)
        builder.run{

            setTitle("위치 서비스 비활성화")
            setMessage(
                "앱을 사용하기 위해서는 위치 서비스가 필요합니다.\n"
                        + "위치 설정을 수정하실래요?"
            )
            setCancelable(true)
            setPositiveButton("설정") { dialog, id ->
                val callGPSSettingIntent =
                    Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivityForResult(callGPSSettingIntent, MapsActivity.GPS_ENABLE_REQUEST_CODE)
            }
            setNegativeButton("취소") { dialog, id -> dialog!!.cancel() }
            create().show()
        }
    }

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            MapsActivity.GPS_ENABLE_REQUEST_CODE -> //사용자가 GPS 활성 시켰는지 검사
                if (checkLocationServicesStatus()) {
                    if (checkLocationServicesStatus()) {
                        Log.d(MapsActivity.TAG, "onActivityResult : GPS 활성화 되있음")
                        needRequest = true
                        return
                    }
                }
        }
    }

    companion object {
        private val TAG: String? = "googlemap_example"
        const val GPS_ENABLE_REQUEST_CODE = 2001
        const val UPDATE_INTERVAL_MS = 1000  // 1초

        const val FASTEST_UPDATE_INTERVAL_MS = 500 // 0.5초


        const val PERMISSIONS_REQUEST_CODE = 100
    }
}
//  mGoogleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener()