package com.example.plzgod

import android.os.Bundle
import android.util.Log
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.skt.tmap.TMapView
import com.skt.tmap.TMapPoint
import com.skt.tmap.overlay.TMapMarkerItem
import android.graphics.BitmapFactory

class MainActivity : AppCompatActivity() {
    private var isMarkerVisible = false // 마커 표시 여부 상태
    private lateinit var markerManager: MarkerManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val frameLayout = findViewById<FrameLayout>(R.id.tmap_view_container)
        val wasteBasket = findViewById<ImageView>(R.id.foregroundImage4)
        // TMapView 초기화
        val tMapView = TMapView(this).apply {
            setSKTMapApiKey("F9wKkfk1Vo7fe83G574bx7OOE2MVgpHY4Km4uZ0V") // API Key 설정
        }
        frameLayout.addView(tMapView)

        // MarkerManager 초기화
        markerManager = MarkerManager(this, tMapView)

        // 콜백 설정
        tMapView.setOnApiKeyListenerCallback(object : TMapView.OnApiKeyListenerCallback {
            override fun onSKTMapApikeySucceed() {
                Log.d("MainActivity", "TMapView 렌더링 및 API Key 인증 성공")
                // 지도 렌더링 완료 확인
                tMapView.setOnMapReadyListener {
                    Log.d("MainActivity", "TMapView 지도 렌더링 완료")
                    // 지도 중심 이동
                    tMapView.setCenterPoint(126.985302, 37.570841, true)
                }
            }
            override fun onSKTMapApikeyFailed(errorMsg: String?) {
                Log.e("TMapView", "API Key 인증 실패: $errorMsg")
            }
        })

        // wasteBasket 클릭 이벤트 설정
        wasteBasket.setOnClickListener {
            if (isMarkerVisible) {
                // 마커 제거
                tMapView.removeTMapMarkerItem("marker1")
            } else {
                // 마커 추가
                markerManager.addMarker(
                    id = "marker1",
                    latitude = 37.570841,
                    longitude = 126.985302,
                    title = "서울시청",
                    subtitle = "서울특별시 중구 세종대로 110"
                )
            }
            isMarkerVisible = !isMarkerVisible // 상태 반전
        }

        // UI 요소들 가져오기
        val mainRectangle = findViewById<ImageView>(R.id.foregroundImage1)
        val menuImage = findViewById<ImageView>(R.id.foregroundImage2)
        val searchRectangle = findViewById<ImageView>(R.id.foregroundImage3)
        val peoPle = findViewById<ImageView>(R.id.foregroundImage5)
        val liGht = findViewById<ImageView>(R.id.foregroundImage6)

        // UI 요소 순서 설정
        mainRectangle.bringToFront()
        menuImage.bringToFront()
        searchRectangle.bringToFront()
        wasteBasket.bringToFront()
        peoPle.bringToFront()
        liGht.bringToFront()
    }
}
