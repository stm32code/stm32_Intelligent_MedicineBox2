package com.example.smartmedicinebox.db

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import com.example.smartmedicinebox.utils.MToast
import com.example.smartagriculturalgreenhouses.db.BaseDao
import com.example.smartmedicinebox.entity.Medicine

class MedicineDao(private val context: Context) : BaseDao {
    private var helper = DBOpenHelper(context)
    private lateinit var db: SQLiteDatabase
    private val TAG = "MedicineDao"

    override fun insert(bean: Any): Int {
        return try {
            val box = bean as Medicine
            db = helper.writableDatabase
            val values = ContentValues()
            values.put("mid", box.mid)
            values.put("name", box.name)
            values.put("num", box.num)
            values.put("state", box.state)
            db.insert("medicine", null, values)
            db.close()
            1
        } catch (e: Exception) {
            e.printStackTrace()
            MToast.mToast(context, "添加失败")
            -1
        }
    }

    override fun update(bean: Any, vararg data: String): Int {
        return try {
            db = helper.writableDatabase
            val values = ContentValues()
            val box = bean as Medicine
            values.put("name", box.name)
            values.put("num", box.num)
            values.put("state", box.state)
            db.update("medicine", values, "mid=?", arrayOf(data[0]))
            1
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            MToast.mToast(context, "修改失败")
            -1
        }
    }

    override fun query(vararg data: String): MutableList<Any>? {
        return try {
            val result: MutableList<Any> = ArrayList()
            val cursor: Cursor
            val sql: String
            db = helper.readableDatabase
            when (data.size) {
                1 -> {
                    sql = "SELECT * from medicine where mid = ?"
                    cursor = db.rawQuery(sql, arrayOf(data[0]))
                }

                else -> {
                    sql = "select * from medicine"
                    cursor = db.rawQuery(sql, null)
                }
            }
            while (cursor.moveToNext()) {
                val box = Medicine(
                    cursor.getInt(cursor.getColumnIndexOrThrow("mid")),
                    cursor.getString(cursor.getColumnIndexOrThrow("name")),
                    cursor.getInt(cursor.getColumnIndexOrThrow("num")),
                    cursor.getInt(cursor.getColumnIndexOrThrow("state"))
                )
                result.add(box)
            }
            cursor.close()
            result
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            Log.e("查询", e.toString())
            MToast.mToast(context, "查询失败")
            null
        }
    }

    override fun delete(vararg data: String): Int {
        return try {
            if (data.isEmpty()) {
                return 0
            }
            db = helper.writableDatabase
            db.delete("medicine", "mid=?", arrayOf(data[0]))
            db.close()
            1
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            MToast.mToast(context, "删除失败")
            -1
        }
    }
}