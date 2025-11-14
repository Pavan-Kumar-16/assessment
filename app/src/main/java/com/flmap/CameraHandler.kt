package com.flmap

import android.content.Context
import android.graphics.SurfaceTexture
import android.hardware.camera2.*
import android.hardware.camera2.CameraCaptureSession.CaptureCallback
import android.util.Log
import android.view.Surface
import android.view.TextureView
import java.util.*

/**
 * CameraHandler - Manages Camera2 API for frame capture
 * Captures YUV frames and sends them to JNI for processing
 */
class CameraHandler(
    private val context: Context,
    private val renderer: GLRenderer,
    private val nativeBridge: NativeBridge
) {
    
    private var cameraDevice: CameraDevice? = null
    private var captureSession: CameraCaptureSession? = null
    private var textureView: TextureView? = null
    private val cameraManager: CameraManager = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
    
    private var processingMode: Int = 0 // 0 = grayscale, 1 = canny
    
    companion object {
        private const val TAG = "CameraHandler"
    }
    
    /**
     * Start camera capture
     */
    fun startCamera() {
        try {
            val cameraId = cameraManager.cameraIdList[0]
            val characteristics = cameraManager.getCameraCharacteristics(cameraId)
            val streamConfigMap = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)
            
            // Get preview size
            val previewSize = streamConfigMap?.getOutputSizes(SurfaceTexture::class.java)?.get(0)
            
            textureView = TextureView(context).apply {
                surfaceTextureListener = object : TextureView.SurfaceTextureListener {
                    override fun onSurfaceTextureAvailable(surface: SurfaceTexture, width: Int, height: Int) {
                        openCamera(cameraId)
                    }
                    
                    override fun onSurfaceTextureSizeChanged(surface: SurfaceTexture, width: Int, height: Int) {}
                    override fun onSurfaceTextureDestroyed(surface: SurfaceTexture): Boolean = true
                    override fun onSurfaceTextureUpdated(surface: SurfaceTexture) {
                        // Capture frame from TextureView
                        processFrame()
                    }
                }
            }
            
        } catch (e: Exception) {
            Log.e(TAG, "Error starting camera", e)
        }
    }
    
    /**
     * Open camera device
     */
    private fun openCamera(cameraId: String) {
        try {
            val stateCallback = object : CameraDevice.StateCallback() {
                override fun onOpened(camera: CameraDevice) {
                    cameraDevice = camera
                    createCaptureSession()
                }
                
                override fun onDisconnected(camera: CameraDevice) {
                    camera.close()
                    cameraDevice = null
                }
                
                override fun onError(camera: CameraDevice, error: Int) {
                    camera.close()
                    cameraDevice = null
                    Log.e(TAG, "Camera error: $error")
                }
            }
            
            cameraManager.openCamera(cameraId, stateCallback, null)
        } catch (e: Exception) {
            Log.e(TAG, "Error opening camera", e)
        }
    }
    
    /**
     * Create camera capture session
     */
    private fun createCaptureSession() {
        try {
            val surface = Surface(textureView?.surfaceTexture)
            val captureRequestBuilder = cameraDevice?.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW)
            captureRequestBuilder?.addTarget(surface)
            
            cameraDevice?.createCaptureSession(
                listOf(surface),
                object : CameraCaptureSession.StateCallback() {
                    override fun onConfigured(session: CameraCaptureSession) {
                        captureSession = session
                        captureRequestBuilder?.let {
                            it.set(CaptureRequest.CONTROL_MODE, CaptureRequest.CONTROL_MODE_AUTO)
                            session.setRepeatingRequest(it.build(), null, null)
                        }
                    }
                    
                    override fun onConfigureFailed(session: CameraCaptureSession) {
                        Log.e(TAG, "Capture session configuration failed")
                    }
                },
                null
            )
        } catch (e: Exception) {
            Log.e(TAG, "Error creating capture session", e)
        }
    }
    
    /**
     * Process frame from TextureView
     * Converts to ByteArray and sends to JNI
     */
    private fun processFrame() {
        textureView?.let { view ->
            val bitmap = view.bitmap
            if (bitmap != null) {
                val width = bitmap.width
                val height = bitmap.height
                
                // Convert bitmap to byte array (simplified - in production, extract YUV properly)
                val pixels = IntArray(width * height)
                bitmap.getPixels(pixels, 0, width, 0, 0, width, height)
                val frameData = ByteArray(width * height * 3)
                
                // Convert ARGB to RGB bytes
                for (i in pixels.indices) {
                    val pixel = pixels[i]
                    frameData[i * 3] = ((pixel shr 16) and 0xFF).toByte()     // R
                    frameData[i * 3 + 1] = ((pixel shr 8) and 0xFF).toByte()  // G
                    frameData[i * 3 + 2] = (pixel and 0xFF).toByte()          // B
                }
                
                // Process frame through JNI
                val processedFrame = nativeBridge.processFrame(frameData, width, height, processingMode)
                
                // Update OpenGL renderer with processed frame
                renderer.updateTexture(processedFrame, width, height)
            }
        }
    }
    
    /**
     * Stop camera capture
     */
    fun stopCamera() {
        captureSession?.close()
        cameraDevice?.close()
        cameraDevice = null
        captureSession = null
    }
    
    /**
     * Set processing mode (0 = grayscale, 1 = canny)
     */
    fun setProcessingMode(mode: Int) {
        processingMode = mode
    }
}


