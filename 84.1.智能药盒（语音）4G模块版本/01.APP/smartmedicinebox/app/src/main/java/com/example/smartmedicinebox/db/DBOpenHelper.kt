package com.example.smartmedicinebox.db

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.smartmedicinebox.utils.TimeCycle

class DBOpenHelper(context: Context) : SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {
    companion object {
        const val DB_NAME = "my.db"
        const val DB_VERSION = 1
    }

    // 创建数据库时触发
    override fun onCreate(p0: SQLiteDatabase?) {
        var sql = "create table `user` (" +
                "`uid` INTEGER primary key autoincrement," +
                "`uname` VARCHAR(20)," +
                "`upassword` VARCHAR(20)," +
                "`per` INTEGER," +
                "`phone` VARCHAR(20)," +
                "`createDateTime` VARCHAR(255))"
        p0?.execSQL(sql) //执行sql语句，
        var values = ContentValues()
        values.put("uname", "admin")
        values.put("upassword", "123456")
        values.put("per", 1)
        values.put("createDateTime", TimeCycle.getDateTime())
        p0?.insert("user", null, values)
        sql = "create table `medicine` (" +
                "`mid` INTEGER primary key autoincrement," +
                "`name` VARCHAR(255)," +
                "`num` INTEGER," +
                "`state` INTEGER" +
                ")"
        p0?.execSQL(sql) //执行sql语句
        values = ContentValues()
        values.put("mid", 0)
        values.put("name", "盒1")
        values.put("num", 0)
        p0?.insert("medicine", null, values)
        values = ContentValues()
        values.put("mid", 1)
        values.put("name", "盒2")
        values.put("num", 0)
        p0?.insert("medicine", null, values)
        values = ContentValues()
        values.put("mid", 2)
        values.put("name", "盒3")
        values.put("num", 0)
        p0?.insert("medicine", null, values)
        sql = "create table `history` (" +
                "`hid` INTEGER primary key autoincrement," +
                "`bid` INTEGER," +
                "`name` VARCHAR(255)," +
                "`createDateTime`  VARCHAR(255)" +
                ")"
        p0?.execSQL(sql) //执行sql语句，
    }

    // 更新数据库时触发
    override fun onUpgrade(p0: SQLiteDatabase?, p1: Int, p2: Int) {

    }
}