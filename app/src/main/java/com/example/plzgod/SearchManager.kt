package com.example.plzgod

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.skt.tmap.TMapData
import com.skt.tmap.TMapView
import com.skt.tmap.overlay.TMapMarkerItem
import com.skt.tmap.poi.TMapPOIItem
import java.util.UUID

class SearchManager(private val context: Context, private val tMapView: TMapView) {
    var firstPOI: TMapPOIItem? = null
    // 장소 검색 메서드
    fun performSearch(keyword: String) {
        val tMapData = TMapData()

        // TMapData의 POI 검색 기능 사용
        tMapData.findAllPOI(keyword, object : TMapData.OnFindAllPOIListener {
            override fun onFindAllPOI(poiItemList: ArrayList<TMapPOIItem>?) {
                if (poiItemList == null || poiItemList.isEmpty()) {
                    Log.e("SearchManager", "검색 결과가 없습니다.")
                    showToast("검색 결과가 없습니다.")
                    return
                }
                // 첫 번째 POI 저장
                firstPOI = poiItemList[0]
                val latitude = firstPOI!!.poiPoint.latitude
                val longitude = firstPOI!!.poiPoint.longitude
                Log.d("SearchManager", "첫 번째 검색 결과: ${firstPOI!!.poiName} (${latitude}, ${longitude})")

                // 지도 중심 이동
                tMapView.post {
                    tMapView.setCenterPoint(longitude, latitude, true)
                    Log.d("SearchManager", "지도 중심 이동 완료")
                }
                // 지도에 마커 표시
                updateMapWithPOI(poiItemList)
            }
        })
    }

    // 지도에 검색 결과 표시
    private fun updateMapWithPOI(poiItemList: ArrayList<TMapPOIItem>) {
        tMapView.removeAllTMapMarkerItem() // 캐시 초기화
        tMapView.removeAllTMapPOIItem() // 기존 마커 제거

        for (poiItem in poiItemList) {
            val marker = TMapMarkerItem().apply {
                tMapPoint = poiItem.poiPoint
                name = poiItem.poiName
                canShowCallout = true
                calloutTitle = poiItem.poiName
                id = poiItem.poiName ?: UUID.randomUUID().toString() // id 기본값 설정
            }
            tMapView.addTMapMarkerItem(marker)
        }

        showToast("${poiItemList.size}개의 결과를 찾았습니다.")
    }

    // Toast 메시지 표시
    private fun showToast(message: String) {
        (context as? AppCompatActivity)?.runOnUiThread {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }
}
