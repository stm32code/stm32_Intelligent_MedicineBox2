package com.example.smartmedicinebox

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.smartmedicinebox.databinding.ActivitySetBinding
import com.example.smartmedicinebox.db.UserDao
import com.example.smartmedicinebox.utils.Common
import com.example.smartmedicinebox.utils.MToast
import com.gyf.immersionbar.BarHide
import com.gyf.immersionbar.ImmersionBar

class SetActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySetBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySetBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initView()
    }

    private fun initView() {
        //设置共同沉浸式样式
        ImmersionBar.with(this).hideBar(BarHide.FLAG_HIDE_STATUS_BAR).init()
        binding.backBtn.setOnClickListener { finish() }
        setSupportActionBar(binding.toolbar)
        binding.toolbarLayout.title = "紧急联系人"
        binding.submitBtn.setOnClickListener {
            val phone = binding.phoneText.text.toString()
            if (phone.isEmpty()) {
                MToast.mToast(this, "紧急联系人不能为空")
            } else {
                if (Common.USER != null) {
                    Common.USER?.phone = phone
                    UserDao(this).update(Common.USER!!, Common.USER!!.uid.toString())
                    MToast.mToast(this, "修改成功")
                } else {
                    MToast.mToast(this, "当前用户错误")
                }
            }
        }
    }
}