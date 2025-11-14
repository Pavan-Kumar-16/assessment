cmake_minimum_required(VERSION 3.18.1)

project("native-lib")

# Set C++ standard
set(CMAKE_CXX_STANDARD 17)
set(CMAKE_CXX_STANDARD_REQUIRED ON)

# Include directories
include_directories(${CMAKE_CURRENT_SOURCE_DIR}/../../../jni)

# Find OpenCV
# NOTE: Update this path to your OpenCV Android SDK location
# Example: set(OpenCV_DIR ${CMAKE_CURRENT_SOURCE_DIR}/../../../opencv/sdk/native/jni)
# Or use find_package if OpenCV is installed system-wide
# find_package(OpenCV REQUIRED)

# For now, we'll set up the structure - you need to configure OpenCV path
# Uncomment and set the path to your OpenCV installation:
# set(OpenCV_DIR "path/to/opencv/sdk/native/jni")
# include_directories(${OpenCV_INCLUDE_DIRS})

# Source files
add_library(
    native-lib
    SHARED
    ${CMAKE_CURRENT_SOURCE_DIR}/../../../jni/native-lib.cpp
    ${CMAKE_CURRENT_SOURCE_DIR}/../../../jni/ImageProcessor.cpp
)

# Link libraries
target_link_libraries(
    native-lib
    # ${OpenCV_LIBS}  # Uncomment when OpenCV is configured
    android
    log
)


