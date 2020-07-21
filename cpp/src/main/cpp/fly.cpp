#include <jni.h>
#include <string>
#include "extra/util.h"
#include "extra/log_command.h"

extern "C"
JNIEXPORT jstring JNICALL
Java_com_engineer_android_cpp_ExampleActivity_stringFromJNI(JNIEnv *env, jobject thiz) {
    std::string hello = "Hello from JNI";
    printSomeInfo(env,thiz);
    return env-> NewStringUTF(hello.c_str());
}

extern "C"
JNIEXPORT jstring JNICALL
Java_com_engineer_android_cpp_ExampleActivity_mapStringWithJNI(JNIEnv *env, jobject thiz,
                                                             jstring input) {
    std::string hello = "Hello from JNI";
    const char *c_str = env->GetStringUTFChars(input,NULL);
    if (c_str == NULL) {
        return env-> NewStringUTF("null");
    }
    jint len = env->GetStringLength(input);
    for(int i=0;i<len;i++) {
        LOGE("c_str: %c",*(c_str+i));
    }
    env->ReleaseStringUTFChars(input,c_str);
    return env-> NewStringUTF(hello.c_str());
}