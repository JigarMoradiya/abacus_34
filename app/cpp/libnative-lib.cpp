#include <jni.h>
#include <string>

extern "C" jstring
Java_com_jigar_me_utils_CommonUtils_getOneSignalKey(JNIEnv *env,jobject) {
    std::string app_secret = "046803b8-7161-4f5a-8ccf-034c7347cc7c";
    return env->NewStringUTF(app_secret.c_str());
}
extern "C" jstring
Java_com_jigar_me_utils_CommonUtils_getOrganizerId(JNIEnv *env,jobject) {
    std::string app_secret = "77609163-2e8a-4bfa-91ad-a1fa3cdc6a5b";
    return env->NewStringUTF(app_secret.c_str());
}
extern "C" jstring
Java_com_jigar_me_utils_CommonUtils_getDatabaseKey(JNIEnv *env,jobject) {
    std::string app_secret = "YzI5dFpYZG9aWEpsWTNKaFkydHdhV2R3WVhKaFozSmhjR2hqYUdGdVoyVjZaWEp2WW1WdVpXRjBhR2hoWW1sMGIzYz0=";
    return env->NewStringUTF(app_secret.c_str());
}
extern "C" jstring
Java_com_jigar_me_utils_CommonUtils_getApiBaseUrl(JNIEnv *env,jobject) {
    std::string app_secret = "https://v2.abacuspro.in/apis/";
    return env->NewStringUTF(app_secret.c_str());
}