package com.example.smartmedicinebox

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import com.example.smartmedicinebox.databinding.ActivityRegisterBinding
import com.example.smartmedicinebox.db.UserDao
import com.example.smartmedicinebox.entity.Receive
import com.example.smartmedicinebox.entity.User
import com.example.smartmedicinebox.utils.MToast
import com.gyf.immersionbar.ImmersionBar
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.Objects

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private var isReceive = false
    private lateinit var dao: UserDao
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        dao = UserDao(this)
        initViews()
    }

    private fun initViews() {
        setSupportActionBar(binding.toolbar)
        binding.toolbarLayout.title = "注册用户"
        ImmersionBar.with(this).init()
        Objects.requireNonNull(supportActionBar)?.setDisplayHomeAsUpEnabled(true) //添加默认的返回图标
        supportActionBar!!.setHomeButtonEnabled(true) //设置返回键可用
        binding.registerBtn.setOnClickListener { verifyData() }
    }

    /***
     * 数据验证
     */
    private fun verifyData() {
        val name = binding.inputNameEdit.text.toString()
        val password = binding.inputPasswordEdit.text.toString()
//        Log.e(
//            "ceshi",
//            "item:${binding.inputRole.selectedItem} \n " +
//                    "itemID:${binding.inputRole.selectedItemId} \n" +
//                    "itemPosition:${binding.inputRole.selectedItemPosition}"
//        )
        val phone: String = binding.inputPhoneEdit.text.toString()
        if (name.isEmpty()) {
            MToast.mToast(this, "用户名不能为空")
            return
        }
        if (password.isEmpty()) {
            MToast.mToast(this, "密码不能为空")
            return
        }
        if (phone.isEmpty()) {
            MToast.mToast(this, "紧急联系人不能为空")
            return
        }

        val objects: List<Any>? = dao.query(name, "name")
        if (objects!!.isNotEmpty()) {
            MToast.mToast(this, "已有该用户，请直接登录")
            return
        }
        val user = User(
            uname = name,
            upassword = password,
            phone = phone,
            per = 0
        )
        dao.insert(user)
        MToast.mToast(this, "添加成功")

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        finish()
        return super.onOptionsItemSelected(item)
    }
}