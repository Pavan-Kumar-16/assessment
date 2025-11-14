package com.flmap

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

/**
 * MainActivity - Entry point for the application
 * Handles permissions and initializes camera + OpenGL rendering
 */
class MainActivity : AppCompatActivity() {
    
    private var cameraHandler: CameraHandler? = null
    private lateinit var glSurfaceView: MyGLSurfaceView
    private lateinit var nativeBridge: NativeBridge
    
    companion object {
        private const val CAMERA_PERMISSION_REQUEST_CODE = 100
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Initialize native bridge
        nativeBridge = NativeBridge()
        
        // Initialize OpenGL surface view
        glSurfaceView = MyGLSurfaceView(this)
        setContentView(glSurfaceView)
        
        // Request camera permissions
        if (checkCameraPermission()) {
            initializeCamera()
        } else {
            requestCameraPermission()
        }
    }
    
    /**
     * Check if camera permission is granted
     */
    private fun checkCameraPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    }
    
    /**
     * Request camera permission from user
     */
    private fun requestCameraPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.CAMERA),
            CAMERA_PERMISSION_REQUEST_CODE
        )
    }
    
    /**
     * Initialize camera handler after permission is granted
     */
    private fun initializeCamera() {
        cameraHandler = CameraHandler(this, glSurfaceView.renderer, nativeBridge)
        cameraHandler?.startCamera()
    }
    
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                initializeCamera()
            } else {
                Toast.makeText(this, "Camera permission is required", Toast.LENGTH_LONG).show()
            }
        }
    }
    
    override fun onResume() {
        super.onResume()
        glSurfaceView.onResume()
        cameraHandler?.startCamera()
    }
    
    override fun onPause() {
        super.onPause()
        glSurfaceView.onPause()
        cameraHandler?.stopCamera()
    }
}

