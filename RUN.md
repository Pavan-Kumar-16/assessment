# How to Run FLMap Project

## Running the Web Viewer

### Option 1: Using Python (Recommended)
```bash
cd web
python server.py
```
Then open http://localhost:8000 in your browser.

### Option 2: Using Node.js
```bash
cd web
npx http-server -p 8000
```

### Option 3: Direct File Open
Simply open `web/index.html` in your web browser (some features may be limited).

## Running the Android App

### Prerequisites
1. **Android Studio** installed
2. **Android SDK** (API 24+)
3. **NDK** (Native Development Kit)
4. **OpenCV for Android** - Download from https://opencv.org/releases/
5. **Java JDK 8+**

### Setup Steps

1. **Configure OpenCV in CMakeLists.txt**
   - Open `app/src/main/cpp/CMakeLists.txt`
   - Uncomment and set the OpenCV path:
   ```cmake
   set(OpenCV_DIR "path/to/opencv/sdk/native/jni")
   include_directories(${OpenCV_INCLUDE_DIRS})
   ```
   - Uncomment OpenCV linking:
   ```cmake
   target_link_libraries(
       native-lib
       ${OpenCV_LIBS}
       android
       log
   )
   ```

2. **Open Project in Android Studio**
   - Open Android Studio
   - Select "Open an Existing Project"
   - Navigate to the `flmap` directory
   - Wait for Gradle sync to complete

3. **Sync Gradle**
   - Android Studio should automatically sync
   - If not, click "Sync Now" or File → Sync Project with Gradle Files

4. **Build the Project**
   ```bash
   # Using command line
   ./gradlew build
   
   # Or in Android Studio: Build → Make Project
   ```

5. **Run on Device/Emulator**
   - Connect an Android device via USB (with USB debugging enabled)
   - Or start an Android emulator
   - Click "Run" in Android Studio, or:
   ```bash
   ./gradlew installDebug
   ```

### Troubleshooting

**OpenCV Not Found:**
- Ensure OpenCV Android SDK is downloaded
- Update the path in `app/src/main/cpp/CMakeLists.txt`
- Make sure OpenCV libraries are in the correct ABI folders

**Build Errors:**
- Ensure NDK is installed: Tools → SDK Manager → SDK Tools → NDK
- Check that CMake is installed: Tools → SDK Manager → SDK Tools → CMake
- Verify Java JDK is set correctly: File → Project Structure → SDK Location

**Camera Permission:**
- The app will request camera permission on first launch
- Grant permission to use the camera

## Project Structure

```
flmap/
├── app/                    # Android application
│   ├── src/main/
│   │   ├── java/com/flmap/ # Kotlin source files
│   │   ├── cpp/            # CMakeLists.txt for native build
│   │   └── AndroidManifest.xml
│   └── build.gradle        # App build configuration
├── jni/                    # Native C++ code
│   ├── CMakeLists.txt
│   ├── native-lib.cpp
│   └── ImageProcessor.cpp/h
├── web/                    # Web viewer
│   ├── index.html
│   ├── style.css
│   ├── main.js
│   └── server.py
└── build.gradle            # Root build file
```

## Testing

### Web Viewer
- Open the viewer in a browser
- FPS counter should start at 0
- Resolution should show 0x0 initially
- You can test by calling: `window.updateImage(base64Data, width, height)`

### Android App
- Launch the app
- Grant camera permission
- Camera should start and display processed frames
- Frames are processed through OpenCV (grayscale or canny based on mode)


