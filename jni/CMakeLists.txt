cmake_minimum_required(VERSION 3.18.1)

project("native-lib")

# Set C++ standard
set(CMAKE_CXX_STANDARD 17)
set(CMAKE_CXX_STANDARD_REQUIRED ON)

# Find OpenCV
find_package(OpenCV REQUIRED)

# Include directories
include_directories(${OpenCV_INCLUDE_DIRS})

# Source files
add_library(
    native-lib
    SHARED
    native-lib.cpp
    ImageProcessor.cpp
)

# Link OpenCV libraries
target_link_libraries(
    native-lib
    ${OpenCV_LIBS}
    android
    log
)


