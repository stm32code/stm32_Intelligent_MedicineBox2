package com.example.smartmedicinebox.db

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import com.example.smartagriculturalgreenhouses.db.BaseDao
import com.example.smartmedicinebox.entity.History
import com.example.smartmedicinebox.entity.Medicine
import com.example.smartmedicinebox.utils.MToast

class HistoryDao(private val context: Context) : BaseDao {
    private var helper = DBOpenHelper(context)
    private lateinit var db: SQLiteDatabase
    private val TAG = "HistoryDao"
    override fun insert(bean: Any): Int {
        return try {
            val box = bean as History
            db = helper.writableDatabase
            val values = ContentValues()
            values.put("hid", box.hid)
            values.put("bid", box.bid)
            values.put("name", box.name)
            values.put("createDateTime", box.createDateTime)
            db.insert("history", null, values)
            db.close()
            1
        } catch (e: Exception) {
            e.printStackTrace()
            MToast.mToast(context, "添加失败")
            -1
        }
    }

    override fun update(bean: Any, vararg data: String): Int {
        return -1
    }

    override fun query(vararg data: String): MutableList<Any>? {
        return try {
            val result: MutableList<Any> = ArrayList()
            val cursor: Cursor
            val sql: String
            db = helper.readableDatabase
            when (data.size) {
                1 -> {
                    sql = "SELECT * from history where hid = ?"
                    cursor = db.rawQuery(sql, arrayOf(data[0]))
                }

                else -> {
                    sql = "select * from history"
                    cursor = db.rawQuery(sql, null)
                }
            }
            while (cursor.moveToNext()) {
                val box = History(
                    cursor.getInt(cursor.getColumnIndexOrThrow("hid")),
                    cursor.getInt(cursor.getColumnIndexOrThrow("bid")),
                    cursor.getString(cursor.getColumnIndexOrThrow("name")),
                    cursor.getString(cursor.getColumnIndexOrThrow("createDateTime"))
                )
                result.add(box)
            }
            cursor.close()
            db.close()
            result
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            Log.e("查询", e.toString())
            MToast.mToast(context, "查询失败")
            null
        }
    }

    override fun delete(vararg data: String): Int {
        return -1
    }
}