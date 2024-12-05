package com.example.plzgod

import android.os.Bundle
import android.util.Log
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.skt.tmap.TMapView
import java.io.File

class MainActivity : AppCompatActivity() {
    private var isWMarkerVisible = false // 쓰레기통 마커 상태
    private var isLMarkerVisible = false // 쓰레기통 마커 상태
    private lateinit var wmarkerManager: WasteMarkerManager
    private lateinit var lmarkerManager: LightMarkerManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val frameLayout = findViewById<FrameLayout>(R.id.tmap_view_container)
        val wasteBasket = findViewById<ImageView>(R.id.foregroundImage4)
        val light = findViewById<ImageView>(R.id.foregroundImage6)

        // 파일 복사
        copyFileToInternalStorage("wastebasketlocation.xlsx")
        copyFileToInternalStorage("lightlocation.xlsx")

        // TMapView 초기화
        val tMapView = TMapView(this).apply {
            setSKTMapApiKey("F9wKkfk1Vo7fe83G574bx7OOE2MVgpHY4Km4uZ0V") // API Key 설정
        }
        frameLayout.addView(tMapView)

        // WMarkerManager 초기화
        wmarkerManager = WasteMarkerManager(this, tMapView)
        // LMarkerManager 초기화
        lmarkerManager = LightMarkerManager(this, tMapView)

        // 콜백 설정
        tMapView.setOnApiKeyListenerCallback(object : TMapView.OnApiKeyListenerCallback {
            override fun onSKTMapApikeySucceed() {
                Log.d("MainActivity", "TMapView 렌더링 및 API Key 인증 성공")
                // 지도 렌더링 완료 확인
                tMapView.setOnMapReadyListener {
                    Log.d("MainActivity", "TMapView 지도 렌더링 완료")
                    // 지도 중심 좌표 설정
                    tMapView.setCenterPoint(127.0, 37.0, true) // 기본 TM128 좌표

                    // wasteBasket 클릭 이벤트 설정
                    wasteBasket.setOnClickListener {
                        try {
                            if (isWMarkerVisible) {
                                wmarkerManager.clearAllMarkers() // 모든 마커 제거
                            } else {
                                val excelData = wreadExcelFile("wastebasketlocation.xlsx") // 엑셀 데이터 읽기
                                for (marker in excelData) {
                                    if (marker.id.isNullOrEmpty()) {
                                        Log.e("MainActivity", "마커 ID가 비어 있습니다: $marker")
                                    } else {
                                        wmarkerManager.addMarker(
                                            id = marker.id,
                                            tm128Latitude = marker.tm128Latitude,
                                            tm128Longitude = marker.tm128Longitude,
                                            title = marker.title,
                                            subtitle = marker.subtitle
                                        )
                                    }
                                }
                            }
                            isWMarkerVisible = !isWMarkerVisible // 상태 반전
                        } catch (e: Exception) {
                            Log.e("MainActivity", "쓰레기통 아이콘 클릭 처리 중 오류 발생: ${e.message}", e)
                        }
                    }

                    // light 클릭 이벤트 설정
                    light.setOnClickListener {
                        try {
                            if (isLMarkerVisible) {
                                lmarkerManager.clearAllMarkers() // 모든 마커 제거
                            } else {
                                val excelData = lreadExcelFile("lightlocation.xlsx") // 엑셀 데이터 읽기
                                for (marker in excelData) {
                                    if (marker.id.isNullOrEmpty()) {
                                        Log.e("MainActivity", "마커 ID가 비어 있습니다: $marker")
                                    } else {
                                        // 좌표 유효성 검사
                                        val tm128Longitude = marker.tm128Longitude
                                        val tm128Latitude = marker.tm128Latitude
                                                lmarkerManager.addMarker(
                                                id = marker.id,
                                                tm128Latitude = marker.tm128Latitude,
                                                tm128Longitude = marker.tm128Longitude,
                                                title = marker.title,
                                                )
                                    }
                                }
                            }
                            isLMarkerVisible = !isLMarkerVisible // 상태 반전
                        } catch (e: Exception) {
                            Log.e("MainActivity", "가로등 아이콘 클릭 처리 중 오류 발생: ${e.message}", e)
                        }
                    }
                }
            }

            override fun onSKTMapApikeyFailed(errorMsg: String?) {
                Log.e("TMapView", "API Key 인증 실패: $errorMsg")
            }
        })

        // UI 요소들 가져오기
        val mainRectangle = findViewById<ImageView>(R.id.foregroundImage1)
        val menuImage = findViewById<ImageView>(R.id.foregroundImage2)
        val searchRectangle = findViewById<ImageView>(R.id.foregroundImage3)
        val peoPle = findViewById<ImageView>(R.id.foregroundImage5)

        // UI 요소 순서 설정
        mainRectangle.bringToFront()
        menuImage.bringToFront()
        searchRectangle.bringToFront()
        wasteBasket.bringToFront()
        peoPle.bringToFront()
        light.bringToFront()
    }

    // 파일 복사 함수
    private fun copyFileToInternalStorage(fileName: String) {
        try {
            val inputStream = assets.open(fileName)
            val outputFile = File(filesDir, fileName)
            inputStream.use { input ->
                outputFile.outputStream().use { output ->
                    input.copyTo(output)
                }
            }
            Log.d("MainActivity", "파일 복사 성공: ${outputFile.absolutePath}")
        } catch (e: Exception) {
            Log.e("MainActivity", "파일 복사 실패: ${e.message}", e)
        }
    }

    // 쓰레기통 엑셀 파일 읽기
    private fun wreadExcelFile(fileName: String): List<WasteMarkerData> {
        return try {
            val file = File(filesDir, fileName)
            val data = ExcelWasteMarkerReader.readWMarkersFromExcel(file)

            // id 값 검증 및 기본값 설정
            data.mapIndexed { index, marker ->
                if (marker.id.isNullOrEmpty()) {
                    marker.copy(id = "marker_$index") // 기본값으로 고유한 id 설정
                } else {
                    marker
                }
            }
        } catch (e: Exception) {
            Log.e("MainActivity", "엑셀 파일 읽기 중 오류 발생: ${e.message}", e)
            emptyList()
        }
    }
    // 가로등 엑셀 파일 읽기
    private fun lreadExcelFile(fileName: String): List<LightMarkerData> {
        return try {
            val file = File(filesDir, fileName)
            val data = ExcelLightMarkerReader.readLMarkersFromExcel(file)

            // id 값 검증 및 기본값 설정
            data.mapIndexed { index, marker ->
                if (marker.id.isNullOrEmpty()) {
                    marker.copy(id = "marker_$index") // 기본값으로 고유한 id 설정
                } else {
                    marker
                }
            }
        } catch (e: Exception) {
            Log.e("MainActivity", "엑셀 파일 읽기 중 오류 발생: ${e.message}", e)
            emptyList()
        }
    }
}
