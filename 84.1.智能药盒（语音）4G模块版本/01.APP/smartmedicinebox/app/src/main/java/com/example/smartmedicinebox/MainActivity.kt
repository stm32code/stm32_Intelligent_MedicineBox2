package com.example.smartmedicinebox

import android.app.TimePickerDialog
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract.RawContacts
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.TimePicker
import androidx.appcompat.app.AppCompatActivity
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.LogUtils
import com.example.smartmedicinebox.adapter.MedicineListViewAdapter
import com.example.smartmedicinebox.databinding.ActivityMainBinding
import com.example.smartmedicinebox.db.HistoryDao
import com.example.smartmedicinebox.db.MedicineDao
import com.example.smartmedicinebox.entity.EquipmentData.EquipmentBean
import com.example.smartmedicinebox.entity.History
import com.example.smartmedicinebox.entity.Medicine
import com.example.smartmedicinebox.entity.Receive
import com.example.smartmedicinebox.utils.ChartUtil
import com.example.smartmedicinebox.utils.Common
import com.example.smartmedicinebox.utils.Common.sendMessage
import com.example.smartmedicinebox.utils.CustomBottomSheetDialogFragment
import com.example.smartmedicinebox.utils.HttpAPI
import com.example.smartmedicinebox.utils.MToast
import com.example.smartmedicinebox.utils.TimeCycle
import com.google.gson.Gson
import com.gyf.immersionbar.ImmersionBar
import com.itfitness.mqttlibrary.MQTTHelper
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken
import org.eclipse.paho.client.mqttv3.MqttCallback
import org.eclipse.paho.client.mqttv3.MqttMessage
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.Calendar

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var isDebugView = false //是否显示debug界面

    private val arrayList = mutableListOf<String>() // debug消息数据

    private var adapter: ArrayAdapter<String>? = null // debug消息适配器
    private lateinit var sharedPreferences: SharedPreferences // 临时存储
    private lateinit var editor: SharedPreferences.Editor // 修改提交
    private var medicines = mutableListOf<Any>()
    private lateinit var dao: MedicineDao
    private lateinit var hdao: HistoryDao
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        EventBus.getDefault().register(this)
        dao = MedicineDao(this)
        hdao = HistoryDao(this)
        sharedPreferences = getSharedPreferences("local", MODE_PRIVATE)
        editor = sharedPreferences.edit()
        initView()
        medicines = dao.query()!!
//        getHTTPData()
    }

    private fun initView() {
        setSupportActionBar(findViewById(R.id.toolbar))
        binding.toolbarLayout.title = title
        ImmersionBar.with(this).init()

        val time1 = sharedPreferences.getString("time1", "wait")
        val time2 = sharedPreferences.getString("time2", "wait")
        val time3 = sharedPreferences.getString("time3", "wait")
        if (time1 != "wait") {
            binding.time1Text.text = time1
        }
        if (time2 != "wait") {
            binding.time2Text.text = time2
        }
        if (time3 != "wait") {
            binding.time3Text.text = time3
        }

        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, arrayList)
        binding.debugView.adapter = adapter

        binding.medicineList.adapter = MedicineListViewAdapter(this, dao.query()!!)
        binding.warringLayout.setOnClickListener {
            warringLayout(false, "")
        }
        binding.callBtn.setOnClickListener {
            call(binding.phone.text.toString())
        }
        ChartUtil.initChart(binding.mainChart)
        //时间校准
        binding.calibrationBtn.setOnClickListener {
            if (Common.mqttHelper != null && Common.mqttHelper!!.connected) {
                sendMessage(this, 1, TimeCycle.getDateTimeAndWeek())
            } else {
                MToast.mToast(this, "请先建立连接")
            }
        }
        binding.time1Text.setOnClickListener {
            if (Common.mqttHelper != null && Common.mqttHelper!!.connected) {
                setTime(binding.time1Text)
            } else {
                MToast.mToast(this, "请先建立连接")
            }
        }
        binding.time2Text.setOnClickListener {
            if (Common.mqttHelper != null && Common.mqttHelper!!.connected) {
                setTime(binding.time2Text)
            } else {
                MToast.mToast(this, "请先建立连接")
            }
        }

        binding.time3Text.setOnClickListener {
            if (Common.mqttHelper != null && Common.mqttHelper!!.connected) {
                setTime(binding.time3Text)
            } else {
                MToast.mToast(this, "请先建立连接")
            }
        }
    }

    /***
     * 设置定时器控件时间
     * @param view
     */
    private fun setTime(view: TextView) {
        // 获取当前时间
        val currentTime = Calendar.getInstance()
        // 创建 TimePickerDialog 对话框
        val timePickerDialog = TimePickerDialog(
            this,
            { _: TimePicker?, hourOfDay: Int, minute: Int ->
                view.text = String.format("%02d:%02d", hourOfDay, minute)
                val times = arrayOf("000000", "000000", "000000")
                when (view) {
                    binding.time1Text -> {
                        editor.putString("time1", view.text.toString())
                        editor.commit()
                        times[0] = String.format("%02d%02d", hourOfDay, minute)
                        if (binding.time2Text.text.toString() != "HH:mm:ss") {
                            val temp =
                                binding.time2Text.text.toString().split(":".toRegex())
                                    .dropLastWhile { it.isEmpty() }
                                    .toTypedArray()
                            times[1] =
                                String.format("%s%s", temp[0], temp[1])
                        } else {
                            times[1] = "0000"
                        }
                        if (binding.time3Text.text.toString() != "HH:mm:ss") {
                            val temp =
                                binding.time3Text.text.toString().split(":".toRegex())
                                    .dropLastWhile { it.isEmpty() }
                                    .toTypedArray()
                            times[2] =
                                String.format("%s%s", temp[0], temp[1])
                        } else {
                            times[2] = "0000"
                        }
                    }

                    binding.time2Text -> {
                        editor.putString("time2", view.text.toString())
                        editor.commit()
                        times[1] = String.format("%02d%02d", hourOfDay, minute)
                        if (binding.time1Text.text.toString() != "HH:mm:ss") {
                            val temp =
                                binding.time1Text.text.toString().split(":".toRegex())
                                    .dropLastWhile { it.isEmpty() }
                                    .toTypedArray()
                            times[0] =
                                String.format("%s%s", temp[0], temp[1])
                        } else {
                            times[0] = "0000"
                        }
                        if (binding.time3Text.text.toString() != "HH:mm:ss") {
                            val temp =
                                binding.time3Text.text.toString().split(":".toRegex())
                                    .dropLastWhile { it.isEmpty() }
                                    .toTypedArray()
                            times[2] =
                                String.format("%s%s", temp[0], temp[1])
                        } else {
                            times[2] = "0000"
                        }
                    }

                    else -> {
                        editor.putString("time3", view.text.toString())
                        editor.commit()
                        times[2] = String.format("%02d%02d", hourOfDay, minute)
                        if (binding.time2Text.text.toString() != "HH:mm:ss") {
                            val temp =
                                binding.time2Text.text.toString().split(":".toRegex())
                                    .dropLastWhile { it.isEmpty() }
                                    .toTypedArray()
                            times[1] =
                                String.format("%s%s", temp[0], temp[1])
                        } else {
                            times[1] = "0000"
                        }
                        if (binding.time1Text.text.toString() != "HH:mm:ss") {
                            val temp =
                                binding.time1Text.text.toString().split(":".toRegex())
                                    .dropLastWhile { it.isEmpty() }
                                    .toTypedArray()
                            times[0] =
                                String.format("%s%s", temp[0], temp[1])
                        } else {
                            times[0] = "0000"
                        }
                    }
                }

                if (view != binding.time3Text) {
                    Log.e("测试", "${times[0]}, ${times[1]}")
                    sendMessage(this, 2, times[0], times[1])
                } else { // 定时器3
                    sendMessage(this, 3, times[2], times[2])
                }

            },
            currentTime[Calendar.HOUR_OF_DAY], currentTime[Calendar.MINUTE], true
        )
        // 显示 TimePickerDialog 对话框
        timePickerDialog.show()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun analysisData(data: Receive) {
        try {
            var isChangeFlag = false
            if (data.blood != null && data.heart != null) {
//                Log.e("测试","hhhh")
                val dataFloat = Array(2) { 0.0f }
                dataFloat[0] = data.blood!!.toFloat()
                dataFloat[1] = data.heart!!.toFloat()
                ChartUtil.addEntry(binding.mainChart, dataFloat)
            }

            if (data.drug != null) {
                when (data.drug) {
                    "1" -> {
                        val d = medicines[0] as Medicine
                        hdao.insert(
                            History(
                                bid = 1,
                                name = d.name,
                                createDateTime = TimeCycle.getDateTime()
                            )
                        )
                    }

                    "2" -> {
                        val d = medicines[1] as Medicine
                        hdao.insert(
                            History(
                                bid = 2,
                                name = d.name,
                                createDateTime = TimeCycle.getDateTime()
                            )
                        )
                    }

                    else -> {
                        val d = medicines[2] as Medicine
                        hdao.insert(
                            History(
                                bid = 3,
                                name = d.name,
                                createDateTime = TimeCycle.getDateTime()
                            )
                        )
                    }
                }

            }
            if (data.temp != null) {
                binding.tempText.text = data.temp
            }

//            Log.e("测试","$medicines")
            if (data.drug1 != null) {
                val d = medicines[0] as Medicine
                if (data.drug1 != d.num.toString()) {
                    d.num = data.drug1!!.toInt()
                    dao.update(d, d.mid.toString())
                    isChangeFlag = true
                }
            }
            if (data.drug2 != null) {
                val d = medicines[1] as Medicine
                if (data.drug2 != d.num.toString()) {
                    d.num = data.drug2!!.toInt()
                    dao.update(d, d.mid.toString())
                    isChangeFlag = true
                }
            }
            if (data.drug3 != null) {
                val d = medicines[2] as Medicine
                if (data.drug3 != d.num.toString()) {
                    d.num = data.drug3!!.toInt()
                    dao.update(d, d.mid.toString())
                    isChangeFlag = true
                }
            }
            if (data.drug1_s != null) {
                val d = medicines[0] as Medicine
                if (data.drug1_s != d.state.toString()) {
                    d.state = data.drug1_s!!.toInt()
                    dao.update(d, d.mid.toString())
                    isChangeFlag = true
                }
            }
            if (data.drug2_s != null) {
                val d = medicines[1] as Medicine
                if (data.drug2_s != d.state.toString()) {
                    d.state = data.drug2_s!!.toInt()
                    dao.update(d, d.mid.toString())
                    isChangeFlag = true
                }
            }
            if (data.drug3_s != null) {
                val d = medicines[2] as Medicine
                if (data.drug3_s != d.state.toString()) {
                    d.state = data.drug3_s!!.toInt()
                    dao.update(d, d.mid.toString())
                    isChangeFlag = true
                }
            }

            if (data.waning != null) {
                if (data.waning == "1") warringLayout(true, "警告！") else warringLayout(
                    false,
                    ""
                )
            } else {
                warringLayout(false, "")
            }
            if (isChangeFlag) {
                Log.e("发送修改", "修改")
                medicines = dao.query()!!
                binding.medicineList.adapter = MedicineListViewAdapter(this, medicines)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("数据解析", e.message.toString())
            MToast.mToast(this, "数据解析失败")
        }
    }

    /**
     * @param visibility 是否显示
     * @param str        显示内容
     * @brief 显示警告弹窗和设置弹窗内容
     */
    private fun warringLayout(visibility: Boolean, str: String) {
        if (visibility) {
            binding.warringLayout.visibility = View.VISIBLE
            binding.warringText.text = str
        } else {
            binding.warringLayout.visibility = View.GONE
        }
    }


    /**
     * @param str  如果为 1 添加发送数据到界面   为 2 添加接受消息到界面
     * @param data 数据字符串
     * @brief debug界面数据添加
     */
    private fun debugViewData(str: Int, data: String) {
        if (arrayList.size >= 255) {
            arrayList.clear()
        }
        runOnUiThread {
            when (str) {
                1 -> arrayList.add(
                    """
                    ${
                        """目标主题:${Common.RECEIVE_TOPIC} 
时间:${TimeCycle.getDateTime()}"""
                    }
                    发送消息:$data
                    """.trimIndent()
                )

                2 -> arrayList.add(
                    """
                    ${
                        """来自主题:${Common.RECEIVE_TOPIC} 
时间:${TimeCycle.getDateTime()}"""
                    }
                    接到消息:$data
                    """.trimIndent()
                )
            }
            // 在添加新数据之后调用以下方法，滚动到列表底部
            binding.debugView.post { binding.debugView.setSelection(if (adapter != null) adapter!!.count - 1 else 0) }
            adapter?.notifyDataSetChanged()
        }
    }

    private fun getHTTPData() {
        Thread {
            while (true) {
                // 构建Retrofit实例
                val retrofit = Retrofit.Builder() // 设置网络请求BaseUrl地址
                    .baseUrl("https://api.heclouds.com/devices/")
                    .addConverterFactory(GsonConverterFactory.create()).build()
                // 创建网络请求接口对象实例
                val api: HttpAPI = retrofit.create(HttpAPI::class.java)
                val dataEquipmentCall: Call<EquipmentBean> = api.getEquipmentData()
                dataEquipmentCall.enqueue(object : Callback<EquipmentBean?> {
                    override fun onResponse(
                        call: Call<EquipmentBean?>,
                        response: Response<EquipmentBean?>
                    ) {
                        val data: EquipmentBean? = response.body()
                        if (data != null && data.errno == 0) {
                            Log.e("测试", data.toString())
                            if (data.data.datastreams.size > 0) {
                                runOnUiThread {
//                                binding!!.tempText.text =
//                                    data.getData().getDatastreams().get(0).getDatapoints().get(0)
//                                        .getValue()
//                                binding.humiText.setText(
//                                    data.getData().getDatastreams().get(1).getDatapoints().get(0)
//                                        .getValue()
//                                )
                                }
                            }
                        }
                    }

                    override fun onFailure(call: Call<EquipmentBean?>, t: Throwable) {
                        // 处理请求失败的情况
                    }
                })
                try {
                    Thread.sleep(3000)
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }
            }
        }.start()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_scrolling, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.debugView -> { // 调试窗口
                isDebugView = !isDebugView
                binding.debugView.visibility = if (isDebugView) View.VISIBLE else View.GONE
            }

            R.id.registerView -> { // 历史记录
                val customBottomSheetDialogFragment = CustomBottomSheetDialogFragment(1)
                customBottomSheetDialogFragment.show(
                    supportFragmentManager,
                    customBottomSheetDialogFragment.tag
                )
            }

            else -> { // 设置界面
                startActivity(Intent(this, SetActivity::class.java))
            }
        }

        return super.onOptionsItemSelected(item)
    }

    /**
     * 拨打电话（直接拨打）
     *
     * @param telPhone 电话
     */
    private fun call(telPhone: String) {
        Log.e("电话", telPhone)


        val intent = Intent()
        intent.action = Intent.ACTION_DIAL
        intent.data = (Uri.parse("tel:$telPhone"))
        startActivity(intent)
    }

    override fun onResume() {
        super.onResume()
        binding.phone.text = Common.USER?.phone
    }

    override fun onDestroy() {
        super.onDestroy()
        Common.USER = null
        Log.e("我被调用了", "DEBUG")
        EventBus.getDefault().unregister(this)
    }
}