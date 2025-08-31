package com.example.smartmedicinebox.utils

import com.example.smartmedicinebox.entity.EquipmentData.Equipment0nline
import com.example.smartmedicinebox.entity.EquipmentData.EquipmentBean
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Headers

interface HttpAPI {
    // OneNET 数据
    @Headers("api-key:${Common.API_KEY}")
    @GET("${Common.DRIVER_ID_HARDWARE}/datapoints?limit=100")
    fun getEquipmentData(): Call<EquipmentBean>

    // OneNET 设备状态
    @Headers("api-key:${Common.API_KEY}")
    @GET("${Common.DRIVER_ID_HARDWARE}")
    fun getEquipmentonline(): Call<Equipment0nline>
}