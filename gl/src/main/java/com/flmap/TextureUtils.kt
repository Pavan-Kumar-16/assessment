package com.flmap

import android.opengl.GLES20
import android.util.Log

/**
 * TextureUtils - Utility functions for OpenGL shader compilation
 */
object TextureUtils {
    
    private const val TAG = "TextureUtils"
    
    /**
     * Vertex shader code - handles vertex positions and texture coordinates
     */
    val vertexShaderCode = """
        attribute vec4 aPosition;
        attribute vec2 aTexCoord;
        varying vec2 vTexCoord;
        
        void main() {
            gl_Position = aPosition;
            vTexCoord = aTexCoord;
        }
    """.trimIndent()
    
    /**
     * Fragment shader code - samples texture and outputs color
     */
    val fragmentShaderCode = """
        precision mediump float;
        uniform sampler2D uTexture;
        varying vec2 vTexCoord;
        
        void main() {
            gl_FragColor = texture2D(uTexture, vTexCoord);
        }
    """.trimIndent()
    
    /**
     * Load and compile shader
     * @param type Shader type (GL_VERTEX_SHADER or GL_FRAGMENT_SHADER)
     * @param shaderCode Shader source code
     * @return Shader handle
     */
    fun loadShader(type: Int, shaderCode: String): Int {
        // Create shader
        val shader = GLES20.glCreateShader(type)
        
        // Compile shader
        GLES20.glShaderSource(shader, shaderCode)
        GLES20.glCompileShader(shader)
        
        // Check compilation status
        val compileStatus = IntArray(1)
        GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, compileStatus, 0)
        
        if (compileStatus[0] == 0) {
            val error = GLES20.glGetShaderInfoLog(shader)
            Log.e(TAG, "Shader compilation error: $error")
            GLES20.glDeleteShader(shader)
            return 0
        }
        
        return shader
    }
}


