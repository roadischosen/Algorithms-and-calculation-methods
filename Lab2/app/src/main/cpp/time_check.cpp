#include <jni.h>
#include <chrono>

extern "C" void ShakerSort(int* arr, int size);

extern "C" JNIEXPORT jlong JNICALL
Java_org_example_roadischosen_lab2_MainActivity_ArraySort(JNIEnv *env, jobject instance, jintArray arr) {
    jboolean copied;
    jint* array = (*env).GetIntArrayElements(arr, &copied);

    using clock = std::chrono::high_resolution_clock;
    using unit = std::chrono::microseconds;
    std::chrono::time_point<clock> startTime = clock::now(); //get starting time

    ShakerSort(array, (*env).GetArrayLength(arr));
    (*env).ReleaseIntArrayElements(arr, array, 0);

    return std::chrono::duration_cast<unit>(clock::now() - startTime).count();
}