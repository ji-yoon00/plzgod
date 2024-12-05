package com.example.plzgod

import android.util.Log
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.content.Context
import com.skt.tmap.TMapView
import com.skt.tmap.overlay.TMapMarkerItem
import com.skt.tmap.TMapPoint

class WasteMarkerManager(private val context: Context, private val tMapView: TMapView) {
    private val markers = mutableMapOf<String, TMapMarkerItem>()

    // 마커 생성 및 설정
    fun addMarker(id: String, tm128Latitude: Double, tm128Longitude: Double, title: String, subtitle: String) {
        val tm128Point = TMapPoint(tm128Latitude, tm128Longitude)

        // TMapMarkerItem 생성
        val markerItem = TMapMarkerItem().apply {
            tMapPoint = tm128Point
            name = title
            canShowCallout = true
            calloutTitle = title
            calloutSubTitle = subtitle
            this.id = id
            // 원본 Bitmap 로드
            val originalBitmap = BitmapFactory.decodeResource(context.resources, R.drawable.wastebasketicon)

            // 크기 조정 (100x100으로 예시)
            val scaledBitmap = Bitmap.createScaledBitmap(originalBitmap, 50, 50, true)
            icon = scaledBitmap // 크기 조정된 Bitmap을 마커 아이콘으로 설정
        }

        markers[id] = markerItem

        // 지도에 마커 추가
        try {
            tMapView.addTMapMarkerItem(markerItem)
        } catch (e: Exception) {
            Log.e("MarkerManager", "마커 추가 중 오류 발생: ${e.message}")
        }
    }

    // 모든 마커 제거
    fun clearAllMarkers() {
        for (id in markers.keys) {
            tMapView.removeTMapMarkerItem(id)
        }
        markers.clear()
    }
}

