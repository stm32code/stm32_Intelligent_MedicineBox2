#ifndef __GA6_H_
#define __GA6_H_

// 单片机头文件
#include "stm32f10x.h"
#include "git.h"
#include "cjson.h"
#include "string.h"
#include <stdio.h>
// 初始化
void GA10_Init(void);
// 发消息
void GA10_SendData(unsigned char *data, unsigned short len);
// 发命令
_Bool GA10_SendCmd(char *cmd, char *res, u16 time);
// 解析json数据
void massage_ga10_json(char *message);
// 发布消息
void Mqtt_Pub(u8 cmd);

#endif


