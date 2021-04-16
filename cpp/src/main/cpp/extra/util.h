//
// Created by Rookie on 2020/7/12.
//
#ifndef CPLUSPLUSLITE_UTIL_H
#define CPLUSPLUSLITE_UTIL_H

#endif //CPLUSPLUSLITE_UTIL_H

#include "log_command.h"

void printSomeInfo(JNIEnv *env, jobject thiz) {
    // 1. 获取 thiz 的 class，也就是 java 中的 Class 信息
    jclass thisclazz = env->GetObjectClass(thiz);
    // 2. 根据 Class 获取 getClass 方法的 methodID，第三个参数是签名(params)return
    jmethodID mid_getClass = env->GetMethodID(thisclazz, "getClass", "()Ljava/lang/Class;");
    // 3. 执行 getClass 方法，获得 Class 对象
    jobject clazz_instance = env->CallObjectMethod(thiz, mid_getClass);
    // 4. 获取 Class 实例
    jclass clazz = env->GetObjectClass(clazz_instance);
    // 5. 根据 class  的 methodID
    jmethodID mid_getName = env->GetMethodID(clazz, "getName", "()Ljava/lang/String;");
    // 6. 调用 getName 方法
    jstring name = static_cast<jstring>(env->CallObjectMethod(clazz_instance, mid_getName));

    LOGE("class: %s", env->GetStringUTFChars(name, 0))
}