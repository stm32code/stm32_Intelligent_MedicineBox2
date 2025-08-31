package com.example.smartmedicinebox.utils

import android.content.Context
import android.util.Log
import com.example.smartmedicinebox.entity.Send
import com.example.smartmedicinebox.entity.User
import com.google.gson.Gson
import com.itfitness.mqttlibrary.MQTTHelper

object Common {

//    const val PORT = "6002" // mqtt服务器端口号
//    const val SERVER_ADDRESS = "183.230.40.39"// mqtt 服务器地址


    const val PORT = "1883" // mqtt服务器端口号
    const val SERVER_ADDRESS = "iot-06z00bhlj16tvj8.mqtt.iothub.aliyuncs.com"// mqtt 服务器地址
    const val URL = "tcp://$SERVER_ADDRESS:$PORT" // mqtt连接地址
    const val RECEIVE_TOPIC = "/broadcast/k11q5RB034G/test2" // 接收消息订阅的主题 - 下位机发送消息的主题
    const val PUSH_TOPIC = "/broadcast/k11q5RB034G/test1" // 推送消息的主题 - 下位机接收消息的主题
    const val DRIVER_ID =
        "k11q5RB034G.smartdevice_app|securemode=2,signmethod=hmacsha256,timestamp=1736051418563|" // mqtt id
    const val DRIVER_NAME = "smartdevice_app&k11q5RB034G" // mqtt 用户名 （oneNET中为产品ID）
    const val DRIVER_PASSWORD =
        "e6cb8bee60dd40145acfd5f563acd34d7fea3c03166df5b5720f9d0b80a2db0b" // mqtt 鉴权或者密码
    const val DRIVER_ID_HARDWARE = "smartdevice&k11q5RB034G" // mqtt 硬件id
    const val API_KEY = "29170b7bbc0756de8ba79316f85de6ca38ce11d1acdf555a917ebd2eb55bef51" // （oneNET） APIkey
    var HARDWARE_ONLINE = false // 硬件在线标志位
    var mqttHelper: MQTTHelper? = null // mqtt 连接服务函数
    var USER: User? = null


    /***
     * @brief 包装发送函数，只有建立了连接才发送消息
     */
    fun sendMessage(context: Context, cmd: Int, vararg data: String): String {
        return if (mqttHelper == null || !mqttHelper!!.connected) {
            MToast.mToast(context, "未建立连接")
            ""
        } else {
            try {
                val send = Send(cmd = cmd)
                when (cmd) {
                    1 -> {
                        send.time = data[0]
                    }

                    2 -> {
                        send.time1 = data[0]
                        send.time2 = data[1]
                    }

                    3 -> {
                        send.time1 = data[0]
                        send.time2 = data[0]
                    }

                    4 -> {
                        send.drug1 = data[0].toInt()
                        send.drug2 = data[1].toInt()
                        send.drug3 = data[2].toInt()
                    }

                }
                val result = Gson().toJson(send)
                mqttHelper!!.publish(PUSH_TOPIC, result, 1)
                result
            } catch (e: Exception) {
                e.printStackTrace()
                Log.e("发送错误", e.message.toString())
                MToast.mToast(context, "数据发送失败")
                ""
            }
        }
    }

}