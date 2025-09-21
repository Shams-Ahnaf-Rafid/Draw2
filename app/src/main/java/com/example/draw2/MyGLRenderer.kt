//package com.example.draw2
//
//import android.content.Context
//import android.graphics.BitmapFactory
//import android.opengl.GLES20.*
//import android.opengl.GLUtils
//import android.opengl.GLSurfaceView
//import java.nio.ByteBuffer
//import java.nio.ByteOrder
//import java.nio.FloatBuffer
//import javax.microedition.khronos.egl.EGLConfig
//import javax.microedition.khronos.opengles.GL10
//
//class MyGLRenderer(private val context: Context) : GLSurfaceView.Renderer {
//
//    private var fboId = IntArray(1)
//    private var fboTextureId = IntArray(1)
//    private var fboWidth = 1
//    private var fboHeight = 1
//    private var screenWidth = 1
//    private var screenHeight = 1
//
//    private lateinit var brushShader: ShaderProgram
//    private lateinit var textureShader: ShaderProgram
//
//    private val quadVertices = floatArrayOf(
//        -1f, 1f, 0f, 0f,
//        -1f, -1f, 0f, 1f,
//        1f,  1f, 1f, 0f,
//        1f,  -1f, 1f, 1f
//    )
//
//    private val vertexBuffer: FloatBuffer = ByteBuffer
//        .allocateDirect(quadVertices.size * 4)
//        .order(ByteOrder.nativeOrder())
//        .asFloatBuffer()
//        .put(quadVertices)
//        .apply { position(0) }
//
//    private var positionHandle = 0
//    private var texCoordHandle = 0
//    private var colorHandle = 0
//    private var thicknessHandle = 0
//    private var pointsHandle = 0
//    private var resolutionHandle = 0
//    private var textureHandle = 0
//
//    private var bgTextureId = 0
//    private var fgTextureId = 0
//
//    private var pointA = floatArrayOf(-2f, -2f)
//    private var pointB = floatArrayOf(-2f, -2f)
//
//    fun setPoints(x0: Float, y0: Float, x1: Float, y1: Float) {
//        pointA[0] = x0
//        pointA[1] = y0
//        pointB[0] = x1
//        pointB[1] = y1
//    }
//
//    override fun onSurfaceCreated(unused: GL10?, config: EGLConfig?) {
//        glClearColor(1f, 1f, 1f, 1f)
//
//        brushShader = ShaderProgram(context, R.raw.vertex_shader, R.raw.fragment_shader)
//        textureShader = ShaderProgram(context, R.raw.vertex_shader, R.raw.texture_fragment_shader)
//
//        brushShader.useProgram()
//        positionHandle = glGetAttribLocation(brushShader.program, "a_Position")
//        texCoordHandle = glGetAttribLocation(brushShader.program, "a_TexCoord")
//        colorHandle = glGetUniformLocation(brushShader.program, "u_Color")
//        thicknessHandle = glGetUniformLocation(brushShader.program, "u_Thickness")
//        pointsHandle = glGetUniformLocation(brushShader.program, "u_Points")
//        resolutionHandle = glGetUniformLocation(brushShader.program, "u_resolution")
//        textureHandle = glGetUniformLocation(brushShader.program, "u_Texture")
//
//        bgTextureId = loadTextureFromRes(R.drawable.background)
//        fgTextureId = loadTextureFromRes(R.drawable.foreground)
//    }
//
//    override fun onSurfaceChanged(unused: GL10?, width: Int, height: Int) {
//        screenWidth = width
//        screenHeight = height
//        glViewport(0, 0, width, height)
//
//        fboWidth = width
//        fboHeight = height
//        setupFBO(fboWidth, fboHeight)
//    }
//
//    private fun setupFBO(width: Int, height: Int) {
//        glGenFramebuffers(1, fboId, 0)
//        glGenTextures(1, fboTextureId, 0)
//
//        glBindTexture(GL_TEXTURE_2D, fboTextureId[0])
//        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, null)
//        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR)
//        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR)
//
//        glBindFramebuffer(GL_FRAMEBUFFER, fboId[0])
//        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, fboTextureId[0], 0)
//        glBindFramebuffer(GL_FRAMEBUFFER, 0)
//    }
//
//    override fun onDrawFrame(unused: GL10?) {
//        glEnable(GL_BLEND)
//        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA)
//
//        // Draw brush stroke to FBO
//        glBindFramebuffer(GL_FRAMEBUFFER, fboId[0])
//        glViewport(0, 0, fboWidth, fboHeight)
//
//        brushShader.useProgram()
//        glEnableVertexAttribArray(positionHandle)
//        glEnableVertexAttribArray(texCoordHandle)
//        vertexBuffer.position(0)
//        glVertexAttribPointer(positionHandle, 2, GL_FLOAT, false, 16, vertexBuffer)
//        vertexBuffer.position(2)
//        glVertexAttribPointer(texCoordHandle, 2, GL_FLOAT, false, 16, vertexBuffer)
//
//        glActiveTexture(GL_TEXTURE0)
//        glBindTexture(GL_TEXTURE_2D, fboTextureId[0])
//        glUniform1i(textureHandle, 0)
//
//        glUniform4f(colorHandle, 0f, 0f, 0f, 1f)
//        glUniform1f(thicknessHandle, 50f)
//        glUniform2f(resolutionHandle, fboWidth.toFloat(), fboHeight.toFloat())
//        glUniform2fv(pointsHandle, 2, floatArrayOf(pointA[0], pointA[1], pointB[0], pointB[1]), 0)
//
//        glDrawArrays(GL_TRIANGLE_STRIP, 0, 4)
//        glDisableVertexAttribArray(positionHandle)
//        glDisableVertexAttribArray(texCoordHandle)
//        glBindFramebuffer(GL_FRAMEBUFFER, 0)
//
//        // Draw background + FBO texture to screen
//        glViewport(0, 0, screenWidth, screenHeight)
//        textureShader.useProgram()
//        val pos = glGetAttribLocation(textureShader.program, "a_Position")
//        val tex = glGetAttribLocation(textureShader.program, "a_TexCoord")
//        val bgUniform = glGetUniformLocation(textureShader.program, "u_Background")
//        val fgUniform = glGetUniformLocation(textureShader.program, "u_Texture")
//
//        vertexBuffer.position(0)
//        glEnableVertexAttribArray(pos)
//        glVertexAttribPointer(pos, 2, GL_FLOAT, false, 16, vertexBuffer)
//        vertexBuffer.position(2)
//        glEnableVertexAttribArray(tex)
//        glVertexAttribPointer(tex, 2, GL_FLOAT, false, 16, vertexBuffer)
//
//        glActiveTexture(GL_TEXTURE0)
//        glBindTexture(GL_TEXTURE_2D, bgTextureId)
//        glUniform1i(bgUniform, 0)
//
//        glActiveTexture(GL_TEXTURE1)
//        glBindTexture(GL_TEXTURE_2D, fboTextureId[0])
//        glUniform1i(fgUniform, 1)
//
//        glDrawArrays(GL_TRIANGLE_STRIP, 0, 4)
//        glDisableVertexAttribArray(pos)
//        glDisableVertexAttribArray(tex)
//    }
//
//    private fun loadTextureFromRes(resId: Int): Int {
//        val textures = IntArray(1)
//        glGenTextures(1, textures, 0)
//        val options = BitmapFactory.Options().apply { inScaled = false }
//        val bitmap = BitmapFactory.decodeResource(context.resources, resId, options)
//        glBindTexture(GL_TEXTURE_2D, textures[0])
//        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR)
//        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR)
//        GLUtils.texImage2D(GL_TEXTURE_2D, 0, bitmap, 0)
//        bitmap.recycle()
//        return textures[0]
//    }
//}

package com.example.draw2

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.opengl.GLES20.*
import android.opengl.GLUtils
import android.opengl.GLSurfaceView
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class MyGLRenderer(private val context: Context) : GLSurfaceView.Renderer {

    private var fboId = IntArray(1)
    private var fboTextureId = IntArray(1)
    private var screenWidth = 1
    private var screenHeight = 1

    private lateinit var brushShader: ShaderProgram
    private lateinit var fgShader: ShaderProgram
    private lateinit var bgShader: ShaderProgram

    private val quadVertices = floatArrayOf(
        1f,  1f, 0f, 1f,   // top-left
        1f, -1f, 0f, 0f,   // bottom-left
        -1f,  1f, 1f, 1f,   // top-right
        -1f, -1f, 1f, 0f    // bottom-right
    )



    private val vertexBuffer: FloatBuffer = ByteBuffer
        .allocateDirect(quadVertices.size * 4)
        .order(ByteOrder.nativeOrder())
        .asFloatBuffer()
        .put(quadVertices)
        .apply { position(0) }

    // Brush shader handles
    private var brushPosHandle = 0
    private var brushPointsHandle = 0
    private var brushThicknessHandle = 0
    private var brushResolutionHandle = 0

    // Foreground shader handles
    private var fgPosHandle = 0
    private var fgTexHandle = 0
    private var fgMaskHandle = 0

    // Background shader handles
    private var bgPosHandle = 0
    private var bgTexHandle = 0

    private var bgTextureId = 0
    private var fgTextureId = 0

    private var pointA = floatArrayOf(-2f, -2f)
    private var pointB = floatArrayOf(-2f, -2f)

    fun setPoints(x0: Float, y0: Float, x1: Float, y1: Float) {
        pointA[0] = x0
        pointA[1] = y0
        pointB[0] = x1
        pointB[1] = y1
    }

    override fun onSurfaceCreated(unused: GL10?, config: EGLConfig?) {
        glClearColor(1f, 1f, 1f, 1f)

        // Load shaders
        brushShader = ShaderProgram(context, R.raw.vertex_shader, R.raw.fragment_shader)
        fgShader = ShaderProgram(context, R.raw.vertex_shader, R.raw.foreground_fragment)
        bgShader = ShaderProgram(context, R.raw.vertex_shader, R.raw.texture_fragment_shader)

        // Brush shader handles
        brushShader.useProgram()
        brushPosHandle = glGetAttribLocation(brushShader.program, "a_Position")
        brushPointsHandle = glGetUniformLocation(brushShader.program, "u_Points")
        brushThicknessHandle = glGetUniformLocation(brushShader.program, "u_Thickness")
        brushResolutionHandle = glGetUniformLocation(brushShader.program, "u_resolution")

        // Foreground shader handles
        fgShader.useProgram()
        fgPosHandle = glGetAttribLocation(fgShader.program, "a_Position")
        fgTexHandle = glGetUniformLocation(fgShader.program, "u_Foreground")
        fgMaskHandle = glGetUniformLocation(fgShader.program, "u_Mask")

        // Background shader handles
        bgShader.useProgram()
        bgPosHandle = glGetAttribLocation(bgShader.program, "a_Position")
        bgTexHandle = glGetUniformLocation(bgShader.program, "u_Texture")

        // Load textures
        bgTextureId = loadTextureFromRes(R.drawable.background)
        fgTextureId = loadTextureFromRes(R.drawable.foreground)
    }

    override fun onSurfaceChanged(unused: GL10?, width: Int, height: Int) {
        screenWidth = width
        screenHeight = height
        glViewport(0, 0, width, height)

        setupFBO(width, height)
        clearFBOToWhite()
    }

    private fun setupFBO(width: Int, height: Int) {
        glGenFramebuffers(1, fboId, 0)
        glGenTextures(1, fboTextureId, 0)

        glBindTexture(GL_TEXTURE_2D, fboTextureId[0])
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, null)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE)

        glBindFramebuffer(GL_FRAMEBUFFER, fboId[0])
        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, fboTextureId[0], 0)
        glBindFramebuffer(GL_FRAMEBUFFER, 0)
    }

    private fun clearFBOToWhite() {
        glBindFramebuffer(GL_FRAMEBUFFER, fboId[0])
        glClearColor(1f, 1f, 1f, 1f)
        glClear(GL_COLOR_BUFFER_BIT)
        glBindFramebuffer(GL_FRAMEBUFFER, 0)
    }

    override fun onDrawFrame(unused: GL10?) {
        // Draw brush to FBO (mask)
        glBindFramebuffer(GL_FRAMEBUFFER, fboId[0])
        glViewport(0, 0, screenWidth, screenHeight)
        glEnable(GL_BLEND)
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA)

        brushShader.useProgram()
        vertexBuffer.position(0)
        glEnableVertexAttribArray(brushPosHandle)
        glVertexAttribPointer(brushPosHandle, 2, GL_FLOAT, false, 16, vertexBuffer)

        glUniform2fv(brushPointsHandle, 2, floatArrayOf(pointA[0], pointA[1], pointB[0], pointB[1]), 0)
        glUniform1f(brushThicknessHandle, 100f)
        glUniform2f(brushResolutionHandle, screenWidth.toFloat(), screenHeight.toFloat())

        glDrawArrays(GL_TRIANGLE_STRIP, 0, 4)
        glDisableVertexAttribArray(brushPosHandle)
        glBindFramebuffer(GL_FRAMEBUFFER, 0)

        // Draw background
        glViewport(0, 0, screenWidth, screenHeight)
        bgShader.useProgram()
        vertexBuffer.position(0)
        glEnableVertexAttribArray(bgPosHandle)
        glVertexAttribPointer(bgPosHandle, 2, GL_FLOAT, false, 16, vertexBuffer)
        glActiveTexture(GL_TEXTURE0)
        glBindTexture(GL_TEXTURE_2D, bgTextureId)
        glUniform1i(bgTexHandle, 0)
        glDrawArrays(GL_TRIANGLE_STRIP, 0, 4)
        glDisableVertexAttribArray(bgPosHandle)

        // Draw foreground with mask
        fgShader.useProgram()
        vertexBuffer.position(0)
        glEnableVertexAttribArray(fgPosHandle)
        glVertexAttribPointer(fgPosHandle, 2, GL_FLOAT, false, 16, vertexBuffer)

        glActiveTexture(GL_TEXTURE0)
        glBindTexture(GL_TEXTURE_2D, fgTextureId)
        glUniform1i(fgTexHandle, 0)

        glActiveTexture(GL_TEXTURE1)
        glBindTexture(GL_TEXTURE_2D, fboTextureId[0])
        glUniform1i(fgMaskHandle, 1)

        glDrawArrays(GL_TRIANGLE_STRIP, 0, 4)
        glDisableVertexAttribArray(fgPosHandle)
        glDisable(GL_BLEND)
    }

    private fun loadTextureFromRes(resId: Int): Int {
        val textures = IntArray(1)
        glGenTextures(1, textures, 0)
        val options = BitmapFactory.Options().apply { inScaled = false }
        val bitmap = BitmapFactory.decodeResource(context.resources, resId, options)
        val matrix = android.graphics.Matrix().apply { preScale(1f, -1f) }
        val flipped = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, false)
        glBindTexture(GL_TEXTURE_2D, textures[0])
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE)
        GLUtils.texImage2D(GL_TEXTURE_2D, 0, flipped, 0)
        bitmap.recycle()
        flipped.recycle()
        return textures[0]
    }
}
