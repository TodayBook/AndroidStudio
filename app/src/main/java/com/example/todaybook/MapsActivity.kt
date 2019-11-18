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
import android.view.View.OnClickListener
import android.view.WindowManager.LayoutParams
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityCompat.OnRequestPermissionsResultCallback
import androidx.core.content.ContextCompat
import com.example.todaybook.R.id
import com.example.todaybook.R.layout
import com.google.android.gms.location.*
import com.google.android.gms.location.LocationSettingsRequest.Builder
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.snackbar.Snackbar
import noman.googleplaces.Place
import noman.googleplaces.PlaceType
import noman.googleplaces.PlacesException
import noman.googleplaces.PlacesListener
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashSet
import kotlin.collections.MutableList
import org.jetbrains.anko.noButton
import org.jetbrains.anko.yesButton

class MapsActivity : AppCompatActivity(),
    OnMapReadyCallback,
    OnRequestPermissionsResultCallback,
    PlacesListener {
    override fun onPlacesFailure(e: PlacesException) {}
    override fun onPlacesStart() {}
    override fun onPlacesSuccess(places: List<Place>) {
        runOnUiThread {
            for (place in places) {
                val latLng =
                    LatLng(
                        place.latitude
                        , place.longitude
                    )
                val markerSnippet = getCurrentAddress(latLng)
                val markerOptions =
                    MarkerOptions()
                markerOptions.position(latLng)
                markerOptions.title(place.name)
                markerOptions.snippet(markerSnippet)
                val item: Marker =
                    mMap!!.addMarker(markerOptions)
                previous_marker!!.add(item)
            }

            //중복 마커 제거


            val hashSet =
                HashSet<Marker?>()
            hashSet.addAll(previous_marker!!)
            previous_marker!!.clear()
            previous_marker!!.addAll(hashSet)
        }
    }

    fun showPlaceInformation(location: LatLng?) {
        mMap!!.clear()//지도 클리어

        if (previous_marker != null) previous_marker!!.clear()//지역정보 마커 클리어

        Builder()
            .listener(this@MapsActivity)
            .key("AIzaSyBYDSuPmcdcMPd-Q0Bea3U6JMbGpPwO2Ag") //PLACE 키값
            .latlng(location!!.latitude, location.longitude)//현재 위치
            .radius(500) //500 미터 내에서 검색
            .type(PlaceType.LIBRARY) //도서관으로 설정
            .build()
            .execute()
    }

    override fun onPlacesFinished() {}
    private var mMap: GoogleMap? = null
    private var currentMarker: Marker? = null
    internal var needRequest = false
    // 앱을 실행하기 위해 필요한 퍼미션을 정의합니다.
    internal var REQUIRED_PERMISSIONS = arrayOf(
        permission.ACCESS_FINE_LOCATION,
        permission.ACCESS_COARSE_LOCATION
    )  // 외부 저장소

    internal var mCurrentLocatiion: Location? = null
    internal var currentPosition: LatLng? = null
    private var mFusedLocationClient: FusedLocationProviderClient? =
        null
    private var locationRequest: LocationRequest? = null
    private var location: Location? = null
    private var mLayout  // Snackbar 사용하기 위해서는 View가 필요합니다.
            : View? = null
    // (참고로 Toast에서는 Context가 필요했습니다.)
    internal var previous_marker: MutableList<Marker>? =
        null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(
            LayoutParams.FLAG_KEEP_SCREEN_ON,
            LayoutParams.FLAG_KEEP_SCREEN_ON
        )
        setContentView(layout.activity_maps)
        previous_marker = ArrayList<Marker?>()
        val button: Button =
            findViewById<View>(id.button) as Button
        button.setOnClickListener(OnClickListener { showPlaceInformation(currentPosition) })
        mLayout = findViewById<View?>(id.layout_maps)
        locationRequest = LocationRequest()
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
            .setInterval(MapsActivity.UPDATE_INTERVAL_MS.toLong())
            .setFastestInterval(MapsActivity.FASTEST_UPDATE_INTERVAL_MS.toLong())
        val builder =
            Builder()
        builder.addLocationRequest(LocationRequest())
        mFusedLocationClient =
            LocationServices.getFusedLocationProviderClient(this)
        val mapFragment =
            supportFragmentManager
                .findFragmentById(id.map) as SupportMapFragment?
        mapFragment!!.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        Log.d(MapsActivity.TAG, "onMapReady :")
        mMap = googleMap
//런타임 퍼미션 요청 대화상자나 GPS 활성 요청 대화상자 보이기전에
        //지도의 초기위치를 서울로 이동
        setDefaultLocation()


        //런타임 퍼미션 처리
        // 1. 위치 퍼미션을 가지고 있는지 체크합니다.


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
        if (hasFineLocationPermission == PackageManager.PERMISSION_GRANTED &&
            hasCoarseLocationPermission == PackageManager.PERMISSION_GRANTED
        ) {

            // 2. 이미 퍼미션을 가지고 있다면
            // ( 안드로이드 6.0 이하 버전은 런타임 퍼미션이 필요없기 때문에 이미 허용된 걸로 인식합니다.)


            startLocationUpdates() // 3. 위치 업데이트 시작
        } else {  //2. 퍼미션 요청을 허용한 적이 없다면 퍼미션 요청이 필요합니다. 2가지 경우(3-1, 4-1)가 있습니다.

            // 3-1. 사용자가 퍼미션 거부를 한 적이 있는 경우에는

            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    REQUIRED_PERMISSIONS[0]
                )
            ) {

                // 3-2. 요청을 진행하기 전에 사용자가에게 퍼미션이 필요한 이유를 설명해줄 필요가 있습니다.

                Snackbar.make(
                    mLayout!!, "이 앱을 실행하려면 위치 접근 권한이 필요합니다.",
                    Snackbar.LENGTH_INDEFINITE
                ).setAction("확인") {
                    // 3-3. 사용자게에 퍼미션 요청을 합니다. 요청 결과는 onRequestPermissionResult에서 수신됩니다.

                    ActivityCompat.requestPermissions(
                        this@MapsActivity,
                        REQUIRED_PERMISSIONS,
                        MapsActivity.PERMISSIONS_REQUEST_CODE
                    )
                }.show()
            } else {
                // 4-1. 사용자가 퍼미션 거부를 한 적이 없는 경우에는 퍼미션 요청을 바로 합니다.
                // 요청 결과는 onRequestPermissionResult에서 수신됩니다.

                ActivityCompat.requestPermissions(
                    this, REQUIRED_PERMISSIONS, MapsActivity.PERMISSIONS_REQUEST_CODE
                )
            }
        }
        mMap!!.uiSettings.isMyLocationButtonEnabled = true
        mMap!!.animateCamera(CameraUpdateFactory.zoomTo(15f))
        mMap!!.setOnMapClickListener { Log.d(MapsActivity.TAG, "onMapClick :") }
    }

    internal var locationCallback: LocationCallback =
        object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)
                val locationList: List<Location> =
                    locationResult.locations
                if (locationList.size > 0) {
                    location = locationList[locationList.size - 1]
                    currentPosition = LatLng(
                        location!!.latitude,
                        location!!.longitude
                    )
                    val markerTitle = getCurrentAddress(currentPosition!!)
                    val markerSnippet =
                        "위도:" + location!!.latitude.toString() + " 경도:" + location!!.longitude.toString()
                    Log.d(MapsActivity.TAG, "onLocationResult : $markerSnippet")


                    //현재 위치에 마커 생성하고 이동


                    setCurrentLocation(location, markerTitle, markerSnippet)
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

    fun getCurrentAddress(latlng: LatLng): String {

        //지오코더... GPS를 주소로 변환

        val geocoder =
            Geocoder(this, Locale.getDefault())
        val addresses: List<Address>?
        try {
            addresses = geocoder.getFromLocation(
                latlng.latitude,
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
            address.getAddressLine(0).toString()
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
        markerTitle: String,
        markerSnippet: String
    ) {
        if (currentMarker != null) currentMarker!!.remove()
        val currentLatLng =
            LatLng(
                location!!.latitude,
                location.longitude
            )
        val markerOptions =
            MarkerOptions()
        markerOptions.position(currentLatLng)
        markerOptions.title(markerTitle)
        markerOptions.snippet(markerSnippet)
        markerOptions.draggable(true)
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
        markerOptions.position(DEFAULT_LOCATION)
        markerOptions.title(markerTitle)
        markerOptions.snippet(markerSnippet)
        markerOptions.draggable(true)
        markerOptions.icon(
            BitmapDescriptorFactory.defaultMarker(
                BitmapDescriptorFactory.HUE_RED
            )
        )
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
        return if (hasFineLocationPermission == PackageManager.PERMISSION_GRANTED &&
            hasCoarseLocationPermission == PackageManager.PERMISSION_GRANTED
        ) {
            true
        } else false
    }

    /*
     * ActivityCompat.requestPermissions를 사용한 퍼미션 요청의 결과를 리턴받는 메소드입니다.
     */
    override fun onRequestPermissionsResult(
        permsRequestCode: Int,
        permissions: Array<String>,
        grandResults: IntArray
    ) {
        if (permsRequestCode == MapsActivity.PERMISSIONS_REQUEST_CODE && grandResults.size == REQUIRED_PERMISSIONS.size) {

            // 요청 코드가 PERMISSIONS_REQUEST_CODE 이고, 요청한 퍼미션 개수만큼 수신되었다면


            var check_result = true


            // 모든 퍼미션을 허용했는지 체크합니다.


            for (result in grandResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    check_result = false
                    break
                }
            }
            if (check_result) {

                // 퍼미션을 허용했다면 위치 업데이트를 시작합니다.

                startLocationUpdates()
            } else {
                // 거부한 퍼미션이 있다면 앱을 사용할 수 없는 이유를 설명해주고 앱을 종료합니다.2 가지 경우가 있습니다.


                if (ActivityCompat.shouldShowRequestPermissionRationale(
                        this,
                        REQUIRED_PERMISSIONS[0]
                    )
                    || ActivityCompat.shouldShowRequestPermissionRationale(
                        this,
                        REQUIRED_PERMISSIONS[1]
                    )
                ) {


                    // 사용자가 거부만 선택한 경우에는 앱을 다시 실행하여 허용을 선택하면 앱을 사용할 수 있습니다.

                    Snackbar.make(
                        mLayout!!, "퍼미션이 거부되었습니다. 앱을 다시 실행하여 퍼미션을 허용해주세요. ",
                        Snackbar.LENGTH_INDEFINITE
                    ).setAction("확인") { finish() }.show()
                } else {


                    // "다시 묻지 않음"을 사용자가 체크하고 거부를 선택한 경우에는 설정(앱 정보)에서 퍼미션을 허용해야 앱을 사용할 수 있습니다.

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
        builder.setTitle("위치 서비스 비활성화")
        builder.setMessage(
            "앱을 사용하기 위해서는 위치 서비스가 필요합니다.\n"
                    + "위치 설정을 수정하실래요?"
        )
        builder.setCancelable(true)
        builder.setPositiveButton("설정") { dialog, id ->
            val callGPSSettingIntent =
                Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            startActivityForResult(callGPSSettingIntent, MapsActivity.GPS_ENABLE_REQUEST_CODE)
        }
        builder.setNegativeButton("취소") { dialog, id -> dialog.cancel() }
        builder.create().show()
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
        private const val TAG = "googlemap_example"
        private const val GPS_ENABLE_REQUEST_CODE = 2001
        private const val UPDATE_INTERVAL_MS = 1000  // 1초

        private const val FASTEST_UPDATE_INTERVAL_MS = 500 // 0.5초

        // onRequestPermissionsResult에서 수신된 결과에서 ActivityCompat.requestPermissions를 사용한 퍼미션 요청을 구별하기 위해 사용됩니다.
        private const val PERMISSIONS_REQUEST_CODE = 100
    }
}