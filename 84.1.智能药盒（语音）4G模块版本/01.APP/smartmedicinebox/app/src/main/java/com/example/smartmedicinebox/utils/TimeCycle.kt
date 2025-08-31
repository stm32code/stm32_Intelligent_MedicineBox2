package com.example.smartmedicinebox.utils

import java.text.DateFormat
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Date
import java.util.TimeZone

object TimeCycle {
    /***
     * 获取时间以yyyyMMddHHmmssWeek格式
     * @return 示例2023121613145006表示时间2023年12月16日13点14分50秒周6
     */
    fun getDateTimeAndWeek(): String {
        // 转换为中国时区
        TimeZone.setDefault(TimeZone.getTimeZone("Etc/GMT-8"))
        val calendar = Calendar.getInstance()

        // 获取当前日期和时间
        val year = calendar[Calendar.YEAR]
        val month = calendar[Calendar.MONTH] + 1
        val day = calendar[Calendar.DAY_OF_MONTH]
        val hour = calendar[Calendar.HOUR_OF_DAY]
        val minute = calendar[Calendar.MINUTE]
        val second = calendar[Calendar.SECOND]
        val dayOfWeek = calendar[Calendar.DAY_OF_WEEK]
        return String.format(
            "%04d%02d%02d%02d%02d%02d%02d",
            year,
            month,
            day,
            hour,
            minute,
            second,
            if (dayOfWeek == Calendar.SATURDAY) 6 else if (dayOfWeek == Calendar.SUNDAY) 7 else 1
        )
    }
    /**
     * 比较两个"yyyy-MM-dd HH:mm:ss"字符串大小
     *
     * @param dateTime1 String
     * @param dateTime2 String
     * @return 如果返回值小于 0，则表示 dateTime1 较小；如果返回值大于 0，则表示 dateTime1 较大；如果返回值等于 0，则表示两个日期时间相等
     */
    fun compareDateTime(dateTime1: String, dateTime2: String): Int {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        val dt1 = LocalDateTime.parse(dateTime1, formatter)
        val dt2 = LocalDateTime.parse(dateTime2, formatter)

        return dt1.compareTo(dt2)
    }

    /**
     * 获取当前时间
     * return yyyy-MM-dd HH:mm:ss
     */
    fun getDateTime(): String {
        // 转换为中国时区
        TimeZone.setDefault(TimeZone.getTimeZone("Etc/GMT-8"))
        return transToString(System.currentTimeMillis())
    }

    /**
     * 时间戳转字符串
     *
     * @param time
     * @return String
     */
    fun transToString(time: Long): String {
        val date = Date(time)
        val format: DateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        return format.format(date)
    }

    /**
     * 字符串转时间戳
     *
     * @param date
     * @return Long
     */
    fun transToTimeStamp(date: String): Long {
        return SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(date).time
    }
}