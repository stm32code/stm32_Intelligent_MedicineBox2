package com.example.smartmedicinebox

import android.R
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import com.example.smartmedicinebox.databinding.ActivityAdminBinding
import com.example.smartmedicinebox.utils.CustomBottomSheetDialogFragment
import com.gyf.immersionbar.ImmersionBar

class AdminActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAdminBinding
    private val titles = arrayOf("用户管理", "添加用户", "退出登录")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initViews()
    }


    private fun initViews() {
        ImmersionBar.with(this).init()

        binding.adminListView.adapter = ArrayAdapter(this, R.layout.simple_list_item_1, titles)
        binding.adminListView.setOnItemClickListener { _, _, i, _ ->
            when (i) {
                0 -> {
                    //用户管理
                    val customBottomSheetDialogFragment =
                        CustomBottomSheetDialogFragment(0)
                    customBottomSheetDialogFragment.show(
                        supportFragmentManager,
                        customBottomSheetDialogFragment.tag
                    )
                }

                1 -> {
                    //添加用户
                    startActivity(Intent(this, RegisterActivity::class.java))
                }

                2 -> {
                    //退出登录
                    startActivity(Intent(this, LoginActivity::class.java))
                    finish()
                }
            }
        }
    }
}