package com.flmap

import android.content.Context
import android.opengl.GLSurfaceView
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

/**
 * MyGLSurfaceView - OpenGL ES 2.0 surface view
 * Manages OpenGL rendering context and renderer
 */
class MyGLSurfaceView(context: Context) : GLSurfaceView(context) {
    
    val renderer: GLRenderer
    
    init {
        // Set OpenGL ES 2.0 context
        setEGLContextClientVersion(2)
        
        // Create renderer
        renderer = GLRenderer()
        setRenderer(renderer)
        
        // Set render mode to continuous
        renderMode = RENDERMODE_CONTINUOUSLY
    }
}


