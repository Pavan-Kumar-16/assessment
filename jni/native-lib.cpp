#include <jni.h>
#include <string>
#include <vector>
#include "ImageProcessor.h"

/**
 * JNI function to process frame
 * Called from Kotlin NativeBridge.processFrame()
 * 
 * @param env JNI environment
 * @param thiz Java object (unused)
 * @param frameData Input frame data as jbyteArray
 * @param width Frame width
 * @param height Frame height
 * @param mode Processing mode (0 = grayscale, 1 = canny)
 * @return Processed frame data as jbyteArray
 */
extern "C" JNIEXPORT jbyteArray JNICALL
Java_com_flmap_NativeBridge_processFrame(
    JNIEnv *env,
    jobject thiz,
    jbyteArray frameData,
    jint width,
    jint height,
    jint mode
) {
    // Get input data
    jbyte* inputBytes = env->GetByteArrayElements(frameData, nullptr);
    jsize inputLength = env->GetArrayLength(frameData);
    
    // Convert jbyteArray to unsigned char*
    const unsigned char* inputData = reinterpret_cast<const unsigned char*>(inputBytes);
    
    // Process frame based on mode
    std::vector<unsigned char> processedData;
    
    if (mode == 0) {
        // Grayscale mode
        processedData = ImageProcessor::convertToGrayscale(inputData, width, height);
    } else if (mode == 1) {
        // Canny edge detection mode
        processedData = ImageProcessor::applyCannyEdgeDetection(inputData, width, height);
    } else {
        // Default: return original
        processedData.assign(inputData, inputData + inputLength);
    }
    
    // Create output jbyteArray
    jbyteArray outputArray = env->NewByteArray(processedData.size());
    env->SetByteArrayRegion(outputArray, 0, processedData.size(), 
                           reinterpret_cast<const jbyte*>(processedData.data()));
    
    // Release input array
    env->ReleaseByteArrayElements(frameData, inputBytes, JNI_ABORT);
    
    return outputArray;
}


