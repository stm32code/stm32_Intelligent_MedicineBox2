#include "git.h"

Data_TypeDef Data_init;						  // 设备数据结构体
Threshold_Value_TypeDef threshold_value_init; // 设备阈值设置结构体
Device_Satte_Typedef device_state_init;		  // 设备状态

extern int32_t n_sp02;		 // SPO2 value
extern int8_t ch_spo2_valid; // indicator to show if the SP02 calculation is valid
extern int32_t n_heart_rate; // heart rate value
extern int8_t ch_hr_valid;	 // indicator to show if the heart rate calculation is valid
//时钟
DS1302_Time_t time = {0};					  // 日历时间
u8 sec = 0;									  // 更新秒
extern u8 alarm1_set[2] ;		   // 闹钟1
extern u8 alarm2_set[2] ;		   // 闹钟2
extern u8 alarm3_set[2] ;		   // 闹钟3
extern u8 alarm4_set[2] ;		   // 闹钟4
// 闹钟
extern char *TIME_SET;
extern char *ALARM1_SET;
extern char *ALARM2_SET;
extern u8 time_buf[8];        // 初始时间
static U8 num_on = 0;
static U8 key_old = 0;
// 获取数据参数
mySta Read_Data(Data_TypeDef *Device_Data)
{


	if(USART_RX_STA&0X8000)//接收到一次数据
	{
		Data_init.WENDU_H=USART_RX_BUF[2];		
		Data_init.WENDU_L=USART_RX_BUF[3]/10;
		Data_init.WENDU_H=Data_init.WENDU_H&0X00FF;
		USART_RX_STA=0;//启动下次接收
	}

	//发送测温指令 0XFA 0XCA 0XC4
	Usart_SendByte(USART1, 0XFA);
	Usart_SendByte(USART1, 0XCA);
	Usart_SendByte(USART1, 0XC4);
	return MY_SUCCESSFUL;
}
// 初始化
mySta Reset_Threshole_Value(Threshold_Value_TypeDef *Value, Device_Satte_Typedef *device_state)
{
	
// longitude_sum = 111.434380;
// latitude_sum = 27.197769;
//  // 写
// W_Test();
	// 读
	R_Test();
	// 状态重置
	device_state->check_device = 0;

	return MY_SUCCESSFUL;
}
// 更新OLED显示屏中内容
mySta Update_oled_massage()
{
#if OLED // 是否打开
	char str[50];
	if(Data_init.Page == 1){
		// 心率
		if (0 < n_heart_rate && n_heart_rate < 150 && ch_spo2_valid)
		{
			sprintf(str, "Heart : %03d   ", n_heart_rate);
			OLED_ShowCH(0, 0, (unsigned char *)str);
			sprintf(str, "Blood : %03d   ", n_sp02);
			OLED_ShowCH(0, 2, (unsigned char *)str);
		}
		else
		{
			sprintf(str, "Heart : %03d   ", 0);
			OLED_ShowCH(0, 0, (unsigned char *)str);
			sprintf(str, "Blood : %03d   ", 0);
			OLED_ShowCH(0, 2, (unsigned char *)str);
		}
		// 体温
		if(Data_init.WENDU_H>100){
			sprintf(str, "Temp  : Error  ");
		}else{
			sprintf(str, "Temp  : %02d.%02d ",	Data_init.WENDU_H,	Data_init.WENDU_L);
		}
		OLED_ShowCH(0, 4, (unsigned char *)str);
		
		sprintf(str, "Time: %02d:%02d:%02d ", time.hour, time.minute, time.second);
		OLED_ShowCH(0, 6, (unsigned char *)str);
		
	}
	else if(Data_init.Page == 2){
	
		sprintf(str, "Alarm1: %02x:%02x   ", alarm1_set[0], alarm1_set[1]);
		OLED_ShowCH(0, 0, (unsigned char *)str);
		sprintf(str, "Alarm2: %02x:%02x   ", alarm2_set[0], alarm2_set[1]);
		OLED_ShowCH(0, 2, (unsigned char *)str);
		sprintf(str, "Alarm3: %02x:%02x   ", alarm3_set[0], alarm3_set[1]);
		OLED_ShowCH(0, 4, (unsigned char *)str);
		
		sprintf(str, "Time: %02d:%02d:%02d ", time.hour, time.minute, time.second);
		OLED_ShowCH(0, 6, (unsigned char *)str);
	}
	else if(Data_init.Page == 0){
	
	
		sprintf(str, "BOX1: %03d   ", device_state_init.drug1);
		OLED_ShowCH(0, 0, (unsigned char *)str);
		sprintf(str, "BOX2: %03d   ", device_state_init.drug2);
		OLED_ShowCH(0, 2, (unsigned char *)str);
		sprintf(str, "BOX3: %03d   ", device_state_init.drug3);
		OLED_ShowCH(0, 4, (unsigned char *)str);
		sprintf(str, "Time: %02d:%02d:%02d ", time.hour, time.minute, time.second);
		OLED_ShowCH(0, 6, (unsigned char *)str);
	}
#endif
	return MY_SUCCESSFUL;
}

// 更新设备状态
mySta Update_device_massage()
{

	// 实现1s
	char str[4],str1[4];
	if(device_state_init.Alarm_time<6){
		if(device_state_init.Alarm_time==0){
			// 读取闹钟数据
			sprintf(str,"%02x%02x",device_state_init.Alarm1_hour,device_state_init.Alarm1_min);
			ALARM1_SET = str;
			DS1302_AlarmTime(1);  // 闹钟
		}
		else if(device_state_init.Alarm_time==1){
			sprintf(str1,"%02x%02x",device_state_init.Alarm2_hour,device_state_init.Alarm2_min);
			ALARM2_SET = str1;
			DS1302_AlarmTime(1);  // 闹钟
		}
		else if(device_state_init.Alarm_time==2){
			// 读取闹钟数据
			sprintf(str,"%02x%02x",device_state_init.Alarm3_hour,device_state_init.Alarm3_min);
			ALARM1_SET = str;
			DS1302_AlarmTime(2);  // 闹钟
		}
		else if(device_state_init.Alarm_time==3){
			sprintf(str1,"%02x%02x",device_state_init.Alarm4_hour,device_state_init.Alarm4_min);
			ALARM2_SET = str1;
			DS1302_AlarmTime(2);  // 闹钟
		}

		device_state_init.Alarm_time++;
	}
	DS1302_GetTime(&time); // 读取此时时刻
	// 舵机
	if(device_state_init.door1 ){
		  TIM3->CCR1 = 15;
	}else{
			TIM3->CCR1 = 25;
	}
	if(device_state_init.door2 ){
		 TIM3->CCR2 = 15;
	}else{
		 TIM3->CCR2 = 25;
	}
	if(device_state_init.door3 ){
		 TIM3->CCR4 = 15;
	}else{
		 TIM3->CCR4 = 25;
	}
	
	return MY_SUCCESSFUL;
}

// 定时器
void Automation_Close(void)
{

	
	if(device_state_init.Alarm_time== 7){
		// 保存闹钟
		device_state_init.Alarm1_hour =  alarm1_set[0];
		device_state_init.Alarm1_min =   alarm1_set[1];
		device_state_init.Alarm2_hour =  alarm2_set[0];
		device_state_init.Alarm2_min =   alarm2_set[1];
		W_Alarm1_Time(1);
		device_state_init.Alarm_time += 3;
	}
	if(device_state_init.Alarm_time== 8){
		// 保存闹钟
		device_state_init.Alarm3_hour =  alarm3_set[0];
		device_state_init.Alarm3_min =   alarm3_set[1];
		device_state_init.Alarm4_hour =  alarm4_set[0];
		device_state_init.Alarm4_min =   alarm4_set[1];
		W_Alarm1_Time(2);
		device_state_init.Alarm_time += 3;
	}
	// 检测时间是否达到
	if(time_buf[4]== alarm1_set[0] && time_buf[5] == alarm1_set[1] && time_buf[6] == 1 && device_state_init.door3 == 0)
	{
		// 拿药
		if(device_state_init.drug1 && device_state_init.door1 == 0){
			device_state_init.drug1--;
			device_state_init.door1 = 1;
			device_state_init.drug = 1;
			Mqtt_Pub(2);
			W_Test();
			u2_printf("cmd:1");
		}
	}
	// 检测时间是否达到
	if(time_buf[4]== alarm2_set[0] && time_buf[5] == alarm2_set[1] && time_buf[6] == 1 && device_state_init.door3 == 0)
	{
		// 拿药
		if(device_state_init.drug2 && device_state_init.door2 == 0){
			device_state_init.drug2--;
			device_state_init.door2 = 1;
			device_state_init.drug = 2;
			Mqtt_Pub(2);
			W_Test();
			u2_printf("cmd:1");
		}
	}
	// 检测时间是否达到
	if(time_buf[4]== alarm3_set[0] && time_buf[5] == alarm3_set[1] && time_buf[6] == 1 && device_state_init.door3 == 0)
	{
		// 拿药
		if(device_state_init.drug3 && device_state_init.door3 == 0){
			device_state_init.drug3--;
			device_state_init.door3 = 1;
			device_state_init.drug = 3;
			Mqtt_Pub(2);
			W_Test();
			u2_printf("cmd:1");
		}
	}

	// 执行语音模块发来消息
	if (Data_init.cmd)
	{
		switch (Data_init.cmd)
		{
		case 1:
			// 拿药
			if(device_state_init.drug1 && device_state_init.door1 == 0){
				device_state_init.drug1--;
				device_state_init.door1 = 1;
				device_state_init.drug = 1;
				Mqtt_Pub(2);
				W_Test();
			}
			break;
		case 2:
			// 拿药
			if(device_state_init.drug2 && device_state_init.door2 == 0){
				device_state_init.drug2--;
				device_state_init.door2 = 1;
				device_state_init.drug = 2;
				Mqtt_Pub(2);
				W_Test();
			}
			break;
		case 3:
			// 拿药
			if(device_state_init.drug3 && device_state_init.door3 == 0){
				device_state_init.drug3--;
				device_state_init.door3 = 1;
				device_state_init.drug = 3;
				Mqtt_Pub(2);
				W_Test();
			}
			break;
		case 4:
			if(device_state_init.door1 == 1)
			{
				device_state_init.door1 = 0;
			}
			break;
		case 5:
			if(device_state_init.door2 == 1)
			{
				device_state_init.door2 = 0;
			}
			break;
		case 6:
			if(device_state_init.door3 == 1)
			{
				device_state_init.door3 = 0;
			}
			break;
	  case 7:
			device_state_init.door1 = 0;
			device_state_init.door2 = 0;
			device_state_init.door3 = 0;
			break;

		}
		Data_init.cmd=0;
	}

	
	if (Data_init.App && num_on ==0)
	{
		switch (Data_init.App)
		{
		case 1:
			// 发布消息
			Mqtt_Pub(1);
			break;
		case 2:
			Mqtt_Pub(2);
			break;
		}
		Data_init.App = 0;
	}	
}
// 检测按键是否按下

void Check_Key_ON_OFF()
{
	U8 key;
	key = KEY_Scan(1);
	// 与上一次的键值比较 如果不相等，表明有键值的变化，开始计时
	if (key != 0 && num_on == 0)
	{
		key_old = key;
		num_on = 1;
	}
	if (key != 0 && num_on >= 1 && num_on <= Key_Scan_Time) // 25*10ms
	{
		num_on++; // 时间记录器
	}
	if (key == 0 && num_on > 0 && num_on < Key_Scan_Time) // 短按
	{
		switch (key_old)
		{
		 case KEY1_PRES:
			printf("Key1_Short\n");
			if(Data_init.Page == 0){
				if(device_state_init.door1 == 1)
				{
					device_state_init.door1 = 0;
				}
				else if(device_state_init.door1 == 0){
					// 拿药
					if(device_state_init.drug1){
						device_state_init.drug1--;
						device_state_init.door1 = 1;
						device_state_init.drug = 1;
						Mqtt_Pub(2);
						W_Test();
					}
				}
			}
			break;
		case KEY2_PRES:
			printf("Key2_Short\n");
			if(Data_init.Page == 0){
				if(device_state_init.door2 == 1){
					device_state_init.door2 = 0;
				}
				else if(device_state_init.door2 == 0){
					// 拿药
					if(device_state_init.drug2){
						device_state_init.drug2--;
						device_state_init.door2 = 1;
						device_state_init.drug = 2;
						Mqtt_Pub(2);
						W_Test();
					}
				}
			}
			
			break;
		case KEY3_PRES:
			printf("Key3_Short\n");
			if(Data_init.Page == 0){
				if(device_state_init.door3 == 1){
					device_state_init.door3 = 0;
				}
				else if(device_state_init.door3 == 0){
					// 拿药
					if(device_state_init.drug3){
						device_state_init.drug3--;
						device_state_init.door3 = 1;
						device_state_init.drug = 3;
						Mqtt_Pub(2);
						W_Test();
					}
				}
			}
		
			break;

		default:
			break;
		}
		num_on = 0;
	}
	else if (key == 0 && num_on >= Key_Scan_Time) // 长按
	{
		switch (key_old)
		{
		case KEY1_PRES:
			printf("Key1_Long\n");
			OLED_Clear();
			if(Data_init.Page != 1){
				Data_init.Page = 1;
			}else{
				Data_init.Page = 0;
				n_heart_rate=0;
        n_sp02=0;
			}
	
			break;
		case KEY2_PRES:
			printf("Key1_Long\n");
			OLED_Clear();
			if(Data_init.Page!= 2){
				Data_init.Page = 2;
			}else{
				Data_init.Page = 0;
			  n_heart_rate=0;
        n_sp02=0;
			}
		
			break;
	case KEY3_PRES:
			printf("Key3_Long\n");
			if (device_state_init.Key_State == 1)
			{
				device_state_init.Key_State = 0;
			}
			else
			{
				device_state_init.Key_State = 1;
			}
			Data_init.App = 1;
			break;
		default:
			break;
		}
		num_on = 0;
	}
}
// 解析json数据
mySta massage_parse_json(char *message)
{

	cJSON *cjson_test = NULL; // 检测json格式
	//cJSON *cjson_data = NULL; // 数据
	const char *massage;
	// 定义数据类型
	u8 cjson_cmd; // 指令,方向

	/* 解析整段JSO数据 */
	cjson_test = cJSON_Parse(message);
	if (cjson_test == NULL)
	{
		// 解析失败
		printf("parse fail.\n");
		return MY_FAIL;
	}

	/* 依次根据名称提取JSON数据（键值对） */
	cjson_cmd = cJSON_GetObjectItem(cjson_test, "cmd")->valueint;
	/* 解析嵌套json数据 */
	//cjson_data = cJSON_GetObjectItem(cjson_test, "data");

	switch (cjson_cmd)
	{
	case 0x01: // 消息包
		TIME_SET = cJSON_GetObjectItem(cjson_test, "time")->valuestring;
		DS1302_WriteTime(); 		//  设置时间
		Connect_Net = 60;
		break;
	case 0x02: // 消息包
		ALARM1_SET = cJSON_GetObjectItem(cjson_test, "time1")->valuestring;
		ALARM2_SET = cJSON_GetObjectItem(cjson_test, "time2")->valuestring;
		DS1302_AlarmTime(1);  
	  device_state_init.Alarm_time = 7;
		Connect_Net = 60;
		break;
	case 0x03: // 数据包
		ALARM1_SET = cJSON_GetObjectItem(cjson_test, "time1")->valuestring;
		ALARM2_SET = cJSON_GetObjectItem(cjson_test, "time2")->valuestring;
		DS1302_AlarmTime(2);  
	  device_state_init.Alarm_time = 8;
		Connect_Net = 60;
		break;
	case 0x04: // 数据包
		device_state_init.drug1 = cJSON_GetObjectItem(cjson_test, "drug1")->valueint;
		device_state_init.drug2 = cJSON_GetObjectItem(cjson_test, "drug2")->valueint;
		device_state_init.drug3 = cJSON_GetObjectItem(cjson_test, "drug3")->valueint;
		W_Test();
		Data_init.App = 1;
		Connect_Net = 60;
		break;
	default:
		break;
	}

	/* 清空JSON对象(整条链表)的所有数据 */
	cJSON_Delete(cjson_test);

	return MY_SUCCESSFUL;
}
// 解析数据
mySta massage_speak(char *message)
{

	char *dataPtr = NULL;

	char numBuf[10];
	int num = 0;

	dataPtr = strchr(message, ':'); // 搜索':'

	if (dataPtr != NULL) // 如果找到了
	{
		dataPtr++;
		while (*dataPtr >= '0' && *dataPtr <= '9') // 判断是否是下发的命令控制数据
		{
			numBuf[num++] = *dataPtr++;
		}
		numBuf[num] = 0;
		num = atoi((const char *)numBuf); // 转为数值形式
		if (strstr((char *)message, "cmd")) // 搜索"redled"
		{
			Data_init.cmd = num;
		}
	}
	return MY_SUCCESSFUL;
}
