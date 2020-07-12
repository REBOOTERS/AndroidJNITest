#include <jni.h>
#include <string>


extern "C"
JNIEXPORT jstring JNICALL
Java_com_engineer_android_cpp_ExampleActivity_stringFromJNI(JNIEnv *env, jobject thiz) {
    std::string hello = "Hello from JNI";
    return env-> NewStringUTF(hello.c_str());
}


extern "C"
JNIEXPORT jstring JNICALL
Java_com_engineer_android_cpp_ExampleActivity_mapStringInJNI(JNIEnv *env, jobject thiz,
                                                             jstring input) {
    std::string hello = "Hello from JNI";
    return env-> NewStringUTF(hello.c_str());
}