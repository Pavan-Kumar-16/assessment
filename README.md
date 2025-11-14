# FLMap - Camera Processing Pipeline

A minimal but fully structured Android application that captures camera frames, processes them with OpenCV (C++), and renders them using OpenGL ES 2.0.

## Project Structure

```
flmap/
├── app/                    # Android application (Kotlin)
│   └── src/main/
│       ├── java/com/flmap/
│       │   ├── MainActivity.kt
│       │   ├── CameraHandler.kt
│       │   └── NativeBridge.kt
│       └── AndroidManifest.xml
│
├── jni/                    # Native C++ code with OpenCV
│   ├── CMakeLists.txt
│   ├── native-lib.cpp
│   ├── ImageProcessor.cpp
│   └── ImageProcessor.h
│
├── gl/                     # OpenGL ES 2.0 renderer
│   └── src/main/java/com/flmap/
│       ├── GLRenderer.kt
│       ├── MyGLSurfaceView.kt
│       ├── TextureUtils.kt
│       └── shaders/
│           ├── vertex_shader.glsl
│           └── fragment_shader.glsl
│
└── web/                    # TypeScript web viewer
    ├── index.html
    ├── style.css
    └── main.ts
```

## Features

### Android Module (`/app`)
- **Camera2 API** integration for frame capture
- **TextureView** for camera preview
- YUV to ByteArray conversion
- JNI bridge to native processing

### Native Module (`/jni`)
- **OpenCV C++** image processing
- **Grayscale conversion**
- **Canny edge detection**
- JNI function: `processFrame(frameData, width, height, mode)`
  - `mode = 0` → Grayscale
  - `mode = 1` → Canny

### OpenGL ES 2.0 Module (`/gl`)
- **GLSurfaceView** for rendering
- **Vertex and Fragment shaders**
- Texture upload and rendering pipeline
- Full-screen quad rendering

### Web Module (`/web`)
- **TypeScript** viewer
- **HTML/CSS** interface
- FPS and resolution overlay
- Base64 image display

## Build Requirements

### Android
- Android Studio
- Android NDK
- CMake
- OpenCV for Android (configure in `CMakeLists.txt`)

### Web
- TypeScript compiler (`tsc`) or bundler
- Modern web browser

## Setup Instructions

1. **OpenCV Setup**: Configure OpenCV path in `jni/CMakeLists.txt`
2. **Android Build**: Add CMake configuration to `app/build.gradle`
3. **Web Build**: Compile TypeScript: `tsc web/main.ts --outDir web/dist`

## Usage

### Android
- Launch the app
- Grant camera permission
- Frames are automatically captured and processed
- Switch processing mode via `CameraHandler.setProcessingMode(mode)`

### Web
- Open `web/index.html` in a browser
- View FPS and resolution overlay
- Update image via `window.updateImage(base64Data, width, height)`

## Technical Notes

- All OpenCV processing is done in C++ (no Java/Kotlin OpenCV calls)
- JNI signatures match Kotlin `NativeBridge` class
- OpenGL ES 2.0 shaders use standard texture sampling
- Web viewer is framework-free (vanilla TypeScript)



