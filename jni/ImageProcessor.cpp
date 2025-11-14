#include "ImageProcessor.h"
#include <opencv2/imgproc.hpp>
#include <opencv2/imgcodecs.hpp>

/**
 * Convert RGB frame to grayscale using OpenCV
 */
std::vector<unsigned char> ImageProcessor::convertToGrayscale(
    const unsigned char* input,
    int width,
    int height
) {
    // Create OpenCV Mat from input data (RGB format)
    cv::Mat rgbMat(height, width, CV_8UC3, (void*)input);
    
    // Convert RGB to grayscale
    cv::Mat grayMat;
    cv::cvtColor(rgbMat, grayMat, cv::COLOR_RGB2GRAY);
    
    // Convert grayscale to RGB for output (3 channels)
    cv::Mat outputMat;
    cv::cvtColor(grayMat, outputMat, cv::COLOR_GRAY2RGB);
    
    // Convert to vector
    std::vector<unsigned char> output;
    output.assign(outputMat.data, outputMat.data + outputMat.total() * outputMat.channels());
    
    return output;
}

/**
 * Apply Canny edge detection using OpenCV
 */
std::vector<unsigned char> ImageProcessor::applyCannyEdgeDetection(
    const unsigned char* input,
    int width,
    int height
) {
    // Create OpenCV Mat from input data (RGB format)
    cv::Mat rgbMat(height, width, CV_8UC3, (void*)input);
    
    // Convert to grayscale first
    cv::Mat grayMat;
    cv::cvtColor(rgbMat, grayMat, cv::COLOR_RGB2GRAY);
    
    // Apply Canny edge detection
    cv::Mat edgesMat;
    cv::Canny(grayMat, edgesMat, 50, 150);
    
    // Convert edges to RGB for output (3 channels)
    cv::Mat outputMat;
    cv::cvtColor(edgesMat, outputMat, cv::COLOR_GRAY2RGB);
    
    // Convert to vector
    std::vector<unsigned char> output;
    output.assign(outputMat.data, outputMat.data + outputMat.total() * outputMat.channels());
    
    return output;
}


