/* DO NOT EDIT THIS FILE - it is machine generated */
#include <jni.h>
/* Header for class com_example_gangzhang_myapplication_VideoPlayerActivity */

#ifndef _Included_com_example_gangzhang_myapplication_VideoPlayerActivity
#define _Included_com_example_gangzhang_myapplication_VideoPlayerActivity
#ifdef __cplusplus
extern "C" {
#endif
#undef com_example_gangzhang_myapplication_VideoPlayerActivity_LOCAL_AUDIO
#define com_example_gangzhang_myapplication_VideoPlayerActivity_LOCAL_AUDIO 1L
#undef com_example_gangzhang_myapplication_VideoPlayerActivity_STREAM_AUDIO
#define com_example_gangzhang_myapplication_VideoPlayerActivity_STREAM_AUDIO 2L
#undef com_example_gangzhang_myapplication_VideoPlayerActivity_RESOURCES_AUDIO
#define com_example_gangzhang_myapplication_VideoPlayerActivity_RESOURCES_AUDIO 3L
#undef com_example_gangzhang_myapplication_VideoPlayerActivity_LOCAL_VIDEO
#define com_example_gangzhang_myapplication_VideoPlayerActivity_LOCAL_VIDEO 4L
#undef com_example_gangzhang_myapplication_VideoPlayerActivity_STREAM_VIDEO
#define com_example_gangzhang_myapplication_VideoPlayerActivity_STREAM_VIDEO 5L
/*
 * Class:     com_example_gangzhang_myapplication_VideoPlayerActivity
 * Method:    startH264
 * Signature: ([B)Ljava/lang/String;
 */
JNIEXPORT jint JNICALL Java_com_example_gangzhang_myapplication_VideoPlayerActivity_startH264
  (JNIEnv *, jobject, jbyteArray);

JNIEXPORT jint JNICALL Java_com_example_gangzhang_myapplication_VideoPlayerActivity_initH264
        (JNIEnv * env, jclass obj,jbyteArray filename);
#ifdef __cplusplus
}
#endif
#endif
