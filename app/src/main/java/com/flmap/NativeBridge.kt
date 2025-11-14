package com.flmap

/**
 * NativeBridge - JNI bridge to C++ OpenCV processing
 * Provides Kotlin interface to native image processing functions
 */
class NativeBridge {
    
    init {
        System.loadLibrary("native-lib")
    }
    
    /**
     * Process frame using native OpenCV code
     * @param frameData Raw frame data as ByteArray
     * @param width Frame width
     * @param height Frame height
     * @param mode Processing mode (0 = grayscale, 1 = canny)
     * @return Processed frame data as ByteArray
     */
    external fun processFrame(
        frameData: ByteArray,
        width: Int,
        height: Int,
        mode: Int
    ): ByteArray
    
    companion object {
        init {
            System.loadLibrary("native-lib")
        }
    }
}


