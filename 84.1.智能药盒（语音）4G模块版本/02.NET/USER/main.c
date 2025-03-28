#include "git.h"

// 心率参数初始化
uint32_t aun_ir_buffer[500];  // IR LED sensor data
int32_t n_ir_buffer_length;   // data length
uint32_t aun_red_buffer[500]; // Red LED sensor data
int32_t n_sp02;               // SPO2 value
int8_t ch_spo2_valid;         // indicator to show if the SP02 calculation is valid
int32_t n_heart_rate;         // heart rate value
int8_t ch_hr_valid;           // indicator to show if the heart rate calculation is valid
uint8_t uch_dummy;
// variables to calculate the on-board LED brightness that reflects the heartbeats
uint32_t un_min, un_max, un_prev_data;
int i;
int32_t n_brightness;
float f_temp;
u8 temp_num = 0;
u8 temp[6];
char str[50];
u8 dis_hr = 0, dis_spo2 = 0;
static void Heart_num(void);
#define MAX_BRIGHTNESS 255
// 软件定时器设定
static Timer task1_id;
static Timer task2_id;
static Timer task3_id;
extern u8 time25ms;
u8 Connect_Net;
// 获取全局变量
const char *topics[] = {S_TOPIC_NAME};

// 硬件初始化
void Hardware_Init(void)
{
    NVIC_PriorityGroupConfig(NVIC_PriorityGroup_2); // 设置中断优先级分组为组2：2位抢占优先级，2位响应优先级
    HZ = GB16_NUM();                                // 字数
    delay_init();                                   // 延时函数初始化
    GENERAL_TIM_Init(TIM_4, 0, 1);
    Usart1_Init(9600); // 串口1初始化为9600
		Usart2_Init(9600,1,2);   // 语音
    Usart3_Init(9600); // 串口3，驱动GA10用
    System_PB34_setIO();
    LED_Init();
    Key_GPIO_Config(); // key
		DS1302_Init();      // 初始化ds1302端口
		TIM3_PWM_Init(20);   // PWM
	
#if OLED // OLED文件存在
    OLED_Init();
    OLED_ColorTurn(0);   // 0正常显示，1 反色显示
    OLED_DisplayTurn(0); // 0正常显示 1 屏幕翻转显示
#endif
	 

		

#if OLED // OLED文件存在
    OLED_Clear();
#endif
}
// 网络初始化
void Net_Init()
{

#if OLED // OLED文件存在
    char str[50];
    OLED_Clear();
    // 写OLED内容
		sprintf(str, "---------------");
		OLED_ShowCH(0, 0, (unsigned char *)str);
		sprintf(str, "---Net Break---");
		OLED_ShowCH(0, 2, (unsigned char *)str);
		sprintf(str, "---------------");
		OLED_ShowCH(0, 4, (unsigned char *)str);
#endif
    GA10_Init(); // 初始化GPRS模块

    Connect_Net = 60; // 入网成功
#if OLED              // OLED文件存在
    OLED_Clear();
#endif
}

// 任务1
void task1(void)
{
	//1秒计算器
 	Automation_Close();
	
	
	Update_oled_massage();   // 更新OLED
  Read_Data(&Data_init);   // 更新传感器数据
	if(Connect_Net == 0){
#if OLED // OLED文件存在
    OLED_Clear();
    // 写OLED内容
		sprintf(str, "---------------");
		OLED_ShowCH(0, 0, (unsigned char *)str);
		sprintf(str, "---Net Break---");
		OLED_ShowCH(0, 2, (unsigned char *)str);
		sprintf(str, "---------------");
		OLED_ShowCH(0, 4, (unsigned char *)str);
#endif
    GA10_Init(); // 初始化GPRS模块
		
		sprintf(str, "4GOK");
		OLED_ShowCH(0, 4, (unsigned char *)str);
		

    Connect_Net = 180; // 入网成功
#if OLED              // OLED文件存在
    OLED_Clear();
#endif	 
	 }
}
// 任务2
void task2(void)
{
	 char str[50];


   State = ~State;
	 Update_device_massage(); // 更新设备
	

}
// 任务3
void task3(void)
{
	  Data_init.Time++;
		// 9S 发送数据到平台
		if (Data_init.Time % 2 == 0 && Data_init.App == 0) {
				Data_init.App = 1;
		}
		if(Data_init.Page == 1){
				//获取心率
				Heart_num();
				Data_init.App = 1;
		}	

}
// 软件初始化
void SoftWare_Init(void)
{
 

    timer_start(&task1_id);
    timer_start(&task2_id);
    timer_start(&task3_id);
	
	
}
// 主函数
int main(void)
{

    char *dataPtr = NULL;
    SoftWare_Init(); // 软件初始化
    Hardware_Init(); // 硬件初始化
   
    TIM_Cmd(TIM4, ENABLE); // 使能计数器
    while (1) {

        // 线程
        timer_loop(); // 定时器执行
        // 串口接收判断
        dataPtr = (char*)ESP8266_GetIPD(0);
        if (dataPtr != NULL) {
            massage_parse_json(dataPtr); // 接收命令
        }
				// 语音控制
				Send_Usart2();
#if KEY_OPEN
				// 按键监测
				if(time25ms == MY_TRUE){
						Check_Key_ON_OFF();
						time25ms = MY_FALSE;
				}
#endif
    }
}
/**********************************************************************
 * @ 函数名  ：获取心率数据
 ********************************************************************/
void Heart_num(void)
{
    i = 0;
    un_min = 0x3FFFF;
    un_max = 0;

    // dumping the first 100 sets of samples in the memory and shift the last 400 sets of samples to the top
    for (i = 100; i < 500; i++) {
        aun_red_buffer[i - 100] = aun_red_buffer[i];
        aun_ir_buffer[i - 100] = aun_ir_buffer[i];

        // update the signal min and max
        if (un_min > aun_red_buffer[i])
            un_min = aun_red_buffer[i];
        if (un_max < aun_red_buffer[i])
            un_max = aun_red_buffer[i];
    }
    // take 100 sets of samples before calculating the heart rate.
    for (i = 400; i < 500; i++) {
        un_prev_data = aun_red_buffer[i - 1];
        while (MAX30102_INT == 1)
            ;
        max30102_FIFO_ReadBytes(REG_FIFO_DATA, temp);
        aun_red_buffer[i] = (long)((long)((long)temp[0] & 0x03) << 16) | (long)temp[1] << 8 | (long)temp[2]; // Combine values to get the actual number
        aun_ir_buffer[i] = (long)((long)((long)temp[3] & 0x03) << 16) | (long)temp[4] << 8 | (long)temp[5];  // Combine values to get the actual number

        if (aun_red_buffer[i] > un_prev_data) {
            f_temp = aun_red_buffer[i] - un_prev_data;
            f_temp /= (un_max - un_min);
            f_temp *= MAX_BRIGHTNESS;
            n_brightness -= (int)f_temp;
            if (n_brightness < 0)
                n_brightness = 0;
        } else {
            f_temp = un_prev_data - aun_red_buffer[i];
            f_temp /= (un_max - un_min);
            f_temp *= MAX_BRIGHTNESS;
            n_brightness += (int)f_temp;
            if (n_brightness > MAX_BRIGHTNESS)
                n_brightness = MAX_BRIGHTNESS;
        }
        // send samples and calculation result to terminal program through UART
        if (ch_hr_valid == 1 && n_heart_rate < 120) //**/ ch_hr_valid == 1 && ch_spo2_valid ==1 && n_heart_rate<120 && n_sp02<101
        {
            dis_hr = n_heart_rate;
            dis_spo2 = n_sp02;

        } else {
            dis_hr = 0;
            dis_spo2 = 0;
        }
				
    }
   

}
