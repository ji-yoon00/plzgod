package com.example.plzgod

import android.os.Bundle
import android.util.Log
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.skt.tmap.TMapView
import java.io.File
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import android.content.Context
import android.widget.Button
import android.widget.LinearLayout

class MainActivity : AppCompatActivity() {
    private var isWMarkerVisible = false // 쓰레기통 마커 상태
    private var isLMarkerVisible = false // 쓰레기통 마커 상태
    private lateinit var wmarkerManager: WasteMarkerManager
    private lateinit var lmarkerManager: LightMarkerManager
    private lateinit var tMapView: TMapView
    private var isLightIconChanged = false // 가로등 아이콘 상태
    private var isWasteIconChanged = false // 쓰레기통 아이콘 상태
    private lateinit var searchManager: SearchManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val frameLayout = findViewById<FrameLayout>(R.id.tmap_view_container)
        val wasteBasket = findViewById<ImageView>(R.id.foregroundImage4)
        val light = findViewById<ImageView>(R.id.foregroundImage6)

        //파일복사
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
        // SearchManager 초기화
        searchManager = SearchManager(this, tMapView)

        // 검색창 및 버튼 초기화
        val searchEditText = findViewById<EditText>(R.id.searchEditText)
        val searchIcon = findViewById<ImageView>(R.id.searchIcon)
        val searchRectangle = findViewById<ImageView>(R.id.foregroundImage3)

        searchRectangle.setOnClickListener {
            searchEditText.visibility = View.VISIBLE
            searchEditText.requestFocus() // 검색창에 포커스 설정
            // 키보드 표시
            val imm = applicationContext.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            searchEditText.post {
                imm.showSoftInput(searchEditText, InputMethodManager.SHOW_IMPLICIT)
            }
        }

        searchEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == android.view.inputmethod.EditorInfo.IME_ACTION_SEARCH) {
                val keyword = searchEditText.text.toString().trim()
                if (keyword.isNotEmpty()) {
                    try {
                        searchManager.performSearch(keyword)
                        // 검색 후 동작
                        searchEditText.clearFocus()
                        searchEditText.visibility = View.GONE
                        searchIcon.visibility = View.VISIBLE

                        // 키보드 숨기기
                        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                        imm.hideSoftInputFromWindow(searchEditText.windowToken, 0)
                    } catch (e: Exception) {
                        Log.e("SearchError", "검색 중 예외 발생: ${e.message}")
                        Toast.makeText(this, "검색 중 문제가 발생했습니다.", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@MainActivity, "검색어를 입력하세요", Toast.LENGTH_SHORT).show()
                }
                true // 이벤트 처리 완료
            } else {
                false // 다른 이벤트 처리
            }
        }


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
                        if (isWasteIconChanged) {
                            wasteBasket.setImageResource(R.drawable.wastebasket) // 원래 아이콘
                        } else {
                            wasteBasket.setImageResource(R.drawable.wastebasket_alternate) // 다른 아이콘
                        }
                        isWasteIconChanged = !isWasteIconChanged // 상태 변경
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
                        if (isLightIconChanged) {
                            light.setImageResource(R.drawable.light) // 원래 아이콘
                        } else {
                            light.setImageResource(R.drawable.light_alternate) // 다른 아이콘
                        }
                        isLightIconChanged = !isLightIconChanged // 상태 변경
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
        val peoPle = findViewById<ImageView>(R.id.foregroundImage5)

        // UI 요소 순서 설정
        mainRectangle.bringToFront()
        menuImage.bringToFront()
        searchRectangle.bringToFront()
        wasteBasket.bringToFront()
        peoPle.bringToFront()
        light.bringToFront()
        searchRectangle.bringToFront()
        searchIcon.bringToFront()
        searchEditText.bringToFront()
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