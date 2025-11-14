#ifndef IMAGEPROCESSOR_H
#define IMAGEPROCESSOR_H

#include <opencv2/opencv.hpp>
#include <vector>

/**
 * ImageProcessor - OpenCV image processing functions
 * All OpenCV logic is implemented in C++
 */
class ImageProcessor {
public:
    /**
     * Convert RGB frame to grayscale
     * @param input Input frame data (RGB)
     * @param width Frame width
     * @param height Frame height
     * @return Grayscale frame data
     */
    static std::vector<unsigned char> convertToGrayscale(
        const unsigned char* input,
        int width,
        int height
    );
    
    /**
     * Apply Canny edge detection
     * @param input Input frame data (RGB)
     * @param width Frame width
     * @param height Frame height
     * @return Edge-detected frame data
     */
    static std::vector<unsigned char> applyCannyEdgeDetection(
        const unsigned char* input,
        int width,
        int height
    );
};

#endif // IMAGEPROCESSOR_H


