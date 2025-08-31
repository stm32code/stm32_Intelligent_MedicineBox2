package com.example.smartmedicinebox

import android.Manifest
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.blankj.utilcode.util.LogUtils
import com.example.smartmedicinebox.databinding.ActivityLoginBinding
import com.example.smartmedicinebox.db.UserDao
import com.example.smartmedicinebox.entity.Receive
import com.example.smartmedicinebox.entity.User
import com.example.smartmedicinebox.utils.Common
import com.example.smartmedicinebox.utils.MToast
import com.google.gson.Gson
import com.gyf.immersionbar.ImmersionBar
import com.itfitness.mqttlibrary.MQTTHelper
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken
import org.eclipse.paho.client.mqttv3.MqttCallback
import org.eclipse.paho.client.mqttv3.MqttMessage
import org.greenrobot.eventbus.EventBus
import pub.devrel.easypermissions.EasyPermissions

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding

    private lateinit var dao: UserDao
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        dao = UserDao(this)
        getPermission()
        initViews()
        mqttConfig()
    }

    /****
     * @brief 配置mqtt连接参数并进行连接
     */
    private fun mqttConfig() {
        // 配置mqtt参数
        Common.mqttHelper = MQTTHelper(
            this,
            Common.URL,
            Common.DRIVER_ID,
            Common.DRIVER_NAME,
            Common.DRIVER_PASSWORD,
            true,
            30,
            30
        )
        try {
            // 尝试连接mqtt服务器
            Common.mqttHelper!!.connect(Common.RECEIVE_TOPIC, 1, true, object : MqttCallback {
                override fun connectionLost(cause: Throwable?) {
                    // 连接中断或丢失时触发
                }

                //接受到消息时触发
                override fun messageArrived(topic: String?, message: MqttMessage?) {
                    LogUtils.eTag(
                        "接收到消息-未解码",
                        if (message!!.payload != null) String(message.payload) else ""
                    )

                    val receive = message.toString()
                    //数据转换
                    val data: Receive = Gson().fromJson(receive, Receive::class.java)
                    LogUtils.eTag(
                        "接收到消息-解码", if (message.payload != null) data else ""
                    )
                    EventBus.getDefault().post(data)
                }

                // 消息发送完成时触发
                override fun deliveryComplete(token: IMqttDeliveryToken?) {

                }

            })
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("mqttConfig()", e.message.toString())
            MToast.mToast(this, "连接时发生错误")
        }
    }


    private fun getPermission() {
        val perms: MutableList<String> = ArrayList()
        perms.add(Manifest.permission.ACCESS_COARSE_LOCATION)
        perms.add(Manifest.permission.ACCESS_FINE_LOCATION)
        perms.add(Manifest.permission.VIBRATE)
        perms.add(Manifest.permission.RECEIVE_BOOT_COMPLETED)
        perms.add(Manifest.permission.ACCESS_NOTIFICATION_POLICY)
        perms.add(Manifest.permission.ACCESS_NETWORK_STATE)
        perms.add(Manifest.permission.INTERNET)
        perms.add(Manifest.permission.CALL_PHONE)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) { //13
            perms.add(Manifest.permission.POST_NOTIFICATIONS)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) { //12
            perms.add(Manifest.permission.BLUETOOTH_CONNECT)
            perms.add(Manifest.permission.BLUETOOTH_SCAN)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) { //10
            perms.add(Manifest.permission.USE_FULL_SCREEN_INTENT)
        }
        if (!EasyPermissions.hasPermissions(this, *perms.toTypedArray())) {
            //请求权限
            EasyPermissions.requestPermissions(this, "这是必要的权限", 100, *perms.toTypedArray())
        }
    }

    private fun initViews() {
        setSupportActionBar(binding.toolbar)
        binding.toolbarLayout.title = "登录"
        ImmersionBar.with(this).init()

        binding.loginBtn.setOnClickListener { verifyData() }
        /***
         * 跳转注册
         */
        /***
         * 跳转注册
         */
        binding.skipRegisterBtn.setOnClickListener { view ->
            startActivity(
                Intent(
                    this@LoginActivity,
                    RegisterActivity::class.java
                )
            )
        }
    }

    private fun verifyData() {
        val name = binding.inputNameEdit.text.toString()
        val password = binding.inputPasswordEdit.text.toString()
        if (name.isEmpty()) {
            MToast.mToast(this, "用户名不能为空")
            return
        }
        if (password.isEmpty()) {
            MToast.mToast(this, "密码不能为空")
            return
        }
        val objects: List<Any>? = dao.query(name, password)
        if (objects!!.isEmpty()) {
            MToast.mToast(this, "账号或密码错误")
            return
        }
        val user: User = objects[0] as User
        if (user.uname == name && user.upassword == password) {
            when (user.per) {
                1 -> {
                    startActivity(Intent(this, AdminActivity::class.java))
                }

                0 -> { // 2普通用户
                    Common.USER = user
                    val intent = Intent(this, MainActivity::class.java)
//                    intent.putExtra("role", user.per)
                    startActivity(intent)
                }
            }
            finish()
        } else {
            MToast.mToast(this, "账号或密码错误")
        }
    }
}