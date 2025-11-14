package com.flmap

import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.opengl.GLUtils
import android.graphics.BitmapFactory
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

/**
 * GLRenderer - OpenGL ES 2.0 renderer
 * Handles shader compilation, texture upload, and rendering
 */
class GLRenderer : GLSurfaceView.Renderer {
    
    private var programHandle: Int = 0
    private var textureHandle: Int = 0
    private var vertexBuffer: FloatBuffer? = null
    private var texCoordBuffer: FloatBuffer? = null
    
    private var currentFrameData: ByteArray? = null
    private var currentWidth: Int = 0
    private var currentHeight: Int = 0
    private val frameLock = Any()
    
    // Full-screen quad vertices (x, y)
    private val vertices = floatArrayOf(
        -1.0f, -1.0f,  // Bottom-left
         1.0f, -1.0f,  // Bottom-right
        -1.0f,  1.0f,  // Top-left
         1.0f,  1.0f   // Top-right
    )
    
    // Texture coordinates (u, v)
    private val texCoords = floatArrayOf(
        0.0f, 1.0f,  // Bottom-left
        1.0f, 1.0f,  // Bottom-right
        0.0f, 0.0f,  // Top-left
        1.0f, 0.0f   // Top-right
    )
    
    /**
     * Called when surface is created
     */
    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        // Set clear color to black
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f)
        
        // Load and compile shaders
        val vertexShader = TextureUtils.loadShader(GLES20.GL_VERTEX_SHADER, TextureUtils.vertexShaderCode)
        val fragmentShader = TextureUtils.loadShader(GLES20.GL_FRAGMENT_SHADER, TextureUtils.fragmentShaderCode)
        
        // Create shader program
        programHandle = GLES20.glCreateProgram()
        GLES20.glAttachShader(programHandle, vertexShader)
        GLES20.glAttachShader(programHandle, fragmentShader)
        GLES20.glLinkProgram(programHandle)
        
        // Setup vertex buffers
        setupBuffers()
        
        // Generate texture
        val textures = IntArray(1)
        GLES20.glGenTextures(1, textures, 0)
        textureHandle = textures[0]
        
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureHandle)
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR)
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR)
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE)
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE)
    }
    
    /**
     * Called when surface size changes
     */
    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        GLES20.glViewport(0, 0, width, height)
    }
    
    /**
     * Called every frame to render
     */
    override fun onDrawFrame(gl: GL10?) {
        // Clear screen
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)
        
        // Update texture if new frame available
        synchronized(frameLock) {
            currentFrameData?.let { frameData ->
                if (currentWidth > 0 && currentHeight > 0) {
                    uploadTexture(frameData, currentWidth, currentHeight)
                    currentFrameData = null
                }
            }
        }
        
        // Use shader program
        GLES20.glUseProgram(programHandle)
        
        // Bind texture
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureHandle)
        
        // Set vertex attributes
        val positionHandle = GLES20.glGetAttribLocation(programHandle, "aPosition")
        GLES20.glEnableVertexAttribArray(positionHandle)
        vertexBuffer?.let {
            GLES20.glVertexAttribPointer(positionHandle, 2, GLES20.GL_FLOAT, false, 0, it)
        }
        
        val texCoordHandle = GLES20.glGetAttribLocation(programHandle, "aTexCoord")
        GLES20.glEnableVertexAttribArray(texCoordHandle)
        texCoordBuffer?.let {
            GLES20.glVertexAttribPointer(texCoordHandle, 2, GLES20.GL_FLOAT, false, 0, it)
        }
        
        // Set texture uniform
        val textureHandle = GLES20.glGetUniformLocation(programHandle, "uTexture")
        GLES20.glUniform1i(textureHandle, 0)
        
        // Draw quad
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4)
        
        // Disable vertex arrays
        GLES20.glDisableVertexAttribArray(positionHandle)
        GLES20.glDisableVertexAttribArray(texCoordHandle)
    }
    
    /**
     * Setup vertex buffers
     */
    private fun setupBuffers() {
        // Vertex buffer
        val vertexBuffer = ByteBuffer.allocateDirect(vertices.size * 4)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()
        vertexBuffer.put(vertices)
        vertexBuffer.position(0)
        this.vertexBuffer = vertexBuffer
        
        // Texture coordinate buffer
        val texCoordBuffer = ByteBuffer.allocateDirect(texCoords.size * 4)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()
        texCoordBuffer.put(texCoords)
        texCoordBuffer.position(0)
        this.texCoordBuffer = texCoordBuffer
    }
    
    /**
     * Update texture with new frame data
     * Called from CameraHandler when new processed frame is available
     * Stores frame data to be uploaded on GL thread in onDrawFrame
     */
    fun updateTexture(frameData: ByteArray, width: Int, height: Int) {
        synchronized(frameLock) {
            // Store frame data to be processed on GL thread
            currentFrameData = frameData.copyOf()
            currentWidth = width
            currentHeight = height
        }
    }
    
    /**
     * Upload texture data to GPU (called on GL thread)
     */
    private fun uploadTexture(frameData: ByteArray, width: Int, height: Int) {
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureHandle)
        
        // Upload texture data (RGB format)
        val buffer = ByteBuffer.wrap(frameData)
        GLES20.glTexImage2D(
            GLES20.GL_TEXTURE_2D,
            0,
            GLES20.GL_RGB,
            width,
            height,
            0,
            GLES20.GL_RGB,
            GLES20.GL_UNSIGNED_BYTE,
            buffer
        )
    }
}

