package com.example.smartmedicinebox.entity

data class Send(
    var cmd: Int,
    var time: String? = null, // 时间 yyyy-mm-dd HH:mm:ss
    var time1: String? = null, // HH:mm
    var time2: String? = null,
    var drug1: Int? = null, // 盒子里数量
    var drug2: Int? = null,
    var drug3: Int? = null
)
