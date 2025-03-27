// 网络设备
#include "usart3.h"
#include "led.h"
// 协议文件
#include "GA10.h"
#include "mqttkit.h"
#include "delay.h"
// 硬件驱动
#include "usart.h"
#include "flash.h"
//  C库
#include <string.h>
#include <stdio.h>
extern Device_Satte_Typedef device_state_init;		 // 设备状态
extern Threshold_Value_TypeDef threshold_value_init; // 设备阈值设置结构体
extern unsigned char esp8266_buf[256];
//==========================================================
//	函数功能：	使用GA10发消息
//==========================================================
void GA10_SendData(unsigned char *data, unsigned short len)
{
	ESP8266_Clear();			  // 清空接收缓存
	Usart3_SendString(data, len); // 发送设备连接请求数据
}
//==========================================================
//	函数功能：	给GA6发指令
//==========================================================
_Bool GA10_SendCmd(char *cmd, char *res, u16 time)
{
	char str[256];
	sprintf(str, "%s\r\n", cmd);
	// printf("%s",str);
	Usart3_SendString((unsigned char *)str, strlen((const char *)str));

	while (time--)
	{
		if (ESP8266_WaitRecive() == REV_OK) // 如果收到数据
		{
			//printf("%s\n", esp8266_buf);
			if (strstr((const char *)esp8266_buf, res) != NULL) // 如果检索到关键词
			{
				ESP8266_Clear(); // 清空缓存
				return 0;
			}
		}
		delay_ms(10);
	}
	return 1;
}
//==========================================================
//	函数功能：	初始化GA10
//==========================================================
void GA10_Init(void)
{
	char str[125];
	delay_ms(250);

	ESP8266_Clear();

	printf("测试通信是否成功... ...\r\n"); // 串口输出信息

	while (GA10_SendCmd("AT", "OK", 500)) 
		delay_ms(300);

	printf("设置数据类型.. ...\r\n"); // 串口输出信息

	memset(str, 0, sizeof(str));
	sprintf(str, "AT+MTCFG=\"cloud\",0,2,1");  //AT+MTCFG="cloud",0,2,1
	while (GA10_SendCmd(str, "OK", 500))
		delay_ms(300);

	printf("设置链接参数... ...\r\n"); // 串口输出信息

	memset(str, 0, sizeof(str));	//AT+MTCFG="aliauth",0,"k11q5RB034G","smartdevice","f449d680827e727ffdaf5cd285b83163"
	sprintf(str, "AT+MTCFG=\"aliauth\",0,\"%s\",\"%s\",\"%s\"", PROID, DEVID,AUTH_INFO);
	sprintf(str, "AT+MTCFG=\"aliauth\",0,\"k11q5RB034G\",\"smartdevice\",\"f449d680827e727ffdaf5cd285b83163\"");
	
	while (GA10_SendCmd(str, "OK", 500))
		delay_ms(300);

	printf("连接服务器\r\n");

	memset(str, 0, sizeof(str));		//AT+MTOPEN=0,"iot-06z00bhlj16tvj8.mqtt.iothub.aliyuncs.com",1883
	sprintf(str, "AT+MTOPEN=0,\"%s\",%d", ServerIP, ServerPort);
	sprintf(str, "AT+MTOPEN=0,\"iot-06z00bhlj16tvj8.mqtt.iothub.aliyuncs.com\",1883");
	while (GA10_SendCmd(str, "OK", 500))
		delay_ms(300);
	
	printf("连接设备\r\n");

	memset(str, 0, sizeof(str));
	sprintf(str, "AT+MTCONN=0,\"aliauth\" "); //AT+MTCONN=0,"aliauth"  
	while (GA10_SendCmd(str, "OK", 500))
		delay_ms(300);
	
	printf("定义主题\r\n");

	memset(str, 0, sizeof(str));  //AT+MTSUB=0,1,"/broadcast/k11q5RB034G/test1",0
	sprintf(str, "AT+MTSUB=0,1,\"%s\",0",S_TOPIC_NAME);
	sprintf(str, "AT+MTSUB=0,1,\"/broadcast/k11q5RB034G/test1\",0");
	while (GA10_SendCmd(str, "OK", 500))
		delay_ms(300);

	printf("GA10 OK\r\n");
	// Sys_Restart();//软件复位
}
//==========================================================
//	函数功能：	发布消息
//==========================================================

extern int32_t n_sp02;		 // SPO2 value
extern int8_t ch_spo2_valid; // indicator to show if the SP02 calculation is valid
extern int32_t n_heart_rate; // heart rate value
extern int8_t ch_hr_valid;	 // indicator to show if the heart rate calculation is valid
unsigned char FillBuf(char *buf)
{
	char text[256];
	memset(text, 0, sizeof(text));

	strcpy(buf, "{");
	if(20 < n_heart_rate && n_heart_rate < 150 && ch_spo2_valid){
		memset(text, 0, sizeof(text));
		sprintf(text, "\"heart\":\"%d\",", n_heart_rate); // Temp是数据流的一个名称，temper是温度值
		strcat(buf, text);
		memset(text, 0, sizeof(text));
		sprintf(text, "\"blood\":\"%d\",", n_sp02); // Temp是数据流的一个名称，temper是温度值
		strcat(buf, text);
	}
	if( Data_init.WENDU_H < 50){
		memset(text, 0, sizeof(text));
		sprintf(text, "\"temp\":\"%02d.%02d\",", Data_init.WENDU_H,	Data_init.WENDU_L); // Temp是数据流的一个名称，temper是温度值
		strcat(buf, text);
	}
	memset(text, 0, sizeof(text));
	sprintf(text, "\"drug1_s\":\"%d\",", device_state_init.door1); // Temp是数据流的一个名称，temper是温度值
	strcat(buf, text);

	memset(text, 0, sizeof(text));
	sprintf(text, "\"drug2_s\":\"%d\",", device_state_init.door2); // Temp是数据流的一个名称，temper是温度值
	strcat(buf, text);
	
	memset(text, 0, sizeof(text));
	sprintf(text, "\"drug3_s\":\"%d\",", device_state_init.door3); // Temp是数据流的一个名称，temper是温度值
	strcat(buf, text);
	
	memset(text, 0, sizeof(text));
	sprintf(text, "\"drug1\":\"%d\",", device_state_init.drug1); // Temp是数据流的一个名称，temper是温度值
	strcat(buf, text);

	memset(text, 0, sizeof(text));
	sprintf(text, "\"drug2\":\"%d\",", device_state_init.drug2); // Temp是数据流的一个名称，temper是温度值
	strcat(buf, text);
	
	memset(text, 0, sizeof(text));
	sprintf(text, "\"drug3\":\"%d\",", device_state_init.drug3); // Temp是数据流的一个名称，temper是温度值
	strcat(buf, text);
	
	memset(text, 0, sizeof(text));
	sprintf(text, "\"waning\":\"%d\"", device_state_init.Key_State); // Temp是数据流的一个名称，temper是温度值
	strcat(buf, text);

	memset(text, 0, sizeof(text));
	sprintf(text, "}");
	strcat(buf, text);

	return strlen(buf);
}
unsigned char drugBuf(char *buf)
{
	char text[256];
	memset(text, 0, sizeof(text));

	strcpy(buf, "{");


	memset(text, 0, sizeof(text));
	sprintf(text, "\"drug\":\"%d\"", device_state_init.drug); // Temp是数据流的一个名称，temper是温度值
	strcat(buf, text);

	memset(text, 0, sizeof(text));
	sprintf(text, "}");
	strcat(buf, text);

	return strlen(buf);
}
void Mqtt_Pub(u8 cmd)
{

	char buf[256];
	char str[256];
	short body_len = 0;
	memset(buf, 0, sizeof(buf));
	switch (cmd)
	{
	case 1:
		body_len = FillBuf(buf); // 数据流
		break;
	case 2:
		body_len = drugBuf(buf); // 数据流
		break;
	default:
		break;
	}


	if (body_len)
	{
		sprintf(str, "AT+MTPUB=0,0,0,0,\"%s\",\"%s\"\r\n",P_TOPIC_NAME,buf);
		//GA10_SendCmd(str, "OK", 500);
		Usart3_SendString((unsigned char *)str,strlen(str)); // 发送设备连接请求数据
		delay_ms(100);
	}
	
	
}

