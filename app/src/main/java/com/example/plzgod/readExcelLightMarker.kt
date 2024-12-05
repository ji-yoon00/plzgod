package com.example.plzgod

import android.util.Log
import org.apache.poi.ss.usermodel.WorkbookFactory
import java.io.File
import java.io.FileInputStream

object ExcelLightMarkerReader {

    private const val TAG = "ExcelLMarkerReader"
    fun readLMarkersFromExcel(file: File): List<LightMarkerData> {
        val markerList = mutableListOf<LightMarkerData>()

        if (!file.exists()) {
            Log.e(TAG, "엑셀 파일이 존재하지 않습니다: ${file.absolutePath}")
            return markerList
        }

        try {
            FileInputStream(file).use { inputStream ->
                val workbook = WorkbookFactory.create(inputStream)
                val sheet = workbook.getSheetAt(0) // 첫 번째 시트 읽기

                // 첫 번째 줄은 헤더로 간주하고 두 번째 줄부터 데이터 읽기
                for (row in sheet.drop(1)) {
                    val id = row.getCell(0)?.numericCellValue?.toInt()?.toString() ?: "unknown_id"
                    val tm128Latitude = row.getCell(1)?.numericCellValue ?: 0.0
                    val tm128Longitude = row.getCell(2)?.numericCellValue ?: 0.0
                    val title = row.getCell(3)?.stringCellValue ?: "unknown_title"

                    // MarkerData 객체 생성
                    val marker = LightMarkerData(
                        id = id,
                        tm128Latitude = tm128Latitude,
                        tm128Longitude = tm128Longitude,
                        title = title,
                    )
                    markerList.add(marker)
                }
                workbook.close()
            }
        } catch (e: Exception) {
            Log.e(TAG, "엑셀 파일 읽기 오류: ${e.message}", e)
        }
        return markerList
    }
}
