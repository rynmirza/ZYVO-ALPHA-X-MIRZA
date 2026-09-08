package com.example.zyvo.rtc

import android.graphics.PointF
import android.opengl.GLES11Ext
import android.opengl.GLES20
import android.util.Log
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer

/**
 * NinetiesVignetteBeautyFilter
 *
 * A high-performance, GPU-accelerated real-time video effect filter.
 * Applies a beautiful 90s-inspired warm/nostalgic color grade, a smooth dark vignette,
 * and a parametric face-landmark skin beauty, smoothing, and brightness enhancement.
 * Runs entirely on the OpenGL thread using GLES 2.0 with zero memory copies.
 */
class NinetiesVignetteBeautyFilter {
    private val tag = "NinetiesBeautyFilter"

    private var program = 0
    private var vertexShader = 0
    private var fragmentShader = 0

    // Framebuffer for rendering
    private var fboWidth = 0
    private var fboHeight = 0
    private var fboId = 0
    private var outputTextureId = 0

    // Uniform locations
    private var texMatrixLoc = -1
    private var widthLoc = -1
    private var heightLoc = -1
    private var hasFaceLoc = -1
    private var faceCenterLoc = -1
    private var faceBoundLoc = -1
    private var leftEyeLoc = -1
    private var rightEyeLoc = -1
    private var mouthCenterLoc = -1
    private var leftCheekLoc = -1
    private var rightCheekLoc = -1

    // Adjustable aesthetic thresholds
    private val smoothStrength = 0.35f      // Default 35% natural skin smoothing
    private val beautyBrightness = 0.15f     // Subtle brightness lift (EV +0.15)
    private val rosyCheekIntensity = 0.08f   // Subtle healthy rosy blush (8%)
    private val lipWarmthIntensity = 0.08f   // Natural warm rosy lip tint (8%)
    private val vignetteStrength = 0.30f     // Smooth 90s vignetting (30%)

    // Quad geometry data
    private val vertexCoords = floatArrayOf(
        -1.0f, -1.0f, 0.0f, 0.0f, // Bottom-Left
         1.0f, -1.0f, 1.0f, 0.0f, // Bottom-Right
        -1.0f,  1.0f, 0.0f, 1.0f, // Top-Left
         1.0f,  1.0f, 1.0f, 1.0f  // Top-Right
    )
    private val vertexBuffer: FloatBuffer

    init {
        vertexBuffer = ByteBuffer.allocateDirect(vertexCoords.size * 4)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()
            .put(vertexCoords)
        vertexBuffer.position(0)
    }

    private val vertexShaderSource = """
        attribute vec4 in_position;
        attribute vec4 in_tc;
        varying vec2 var_tc;
        uniform mat4 tex_matrix;
        void main() {
            gl_Position = in_position;
            var_tc = (tex_matrix * in_tc).xy;
        }
    """.trimIndent()

    private val fragmentShaderSource = """
        #extension GL_OES_EGL_image_external : require
        precision mediump float;
        varying vec2 var_tc;
        uniform samplerExternalOES u_texture;
        
        uniform float u_width;
        uniform float u_height;
        
        // Face landmarks
        uniform float u_has_face;
        uniform vec2 u_face_center;
        uniform vec4 u_face_bound; // left, top, right, bottom
        uniform vec2 u_left_eye;
        uniform vec2 u_right_eye;
        uniform vec2 u_mouth_center;
        uniform vec2 u_left_cheek;
        uniform vec2 u_right_cheek;
        
        void main() {
            vec3 color = texture2D(u_texture, var_tc).rgb;
            float aspect = u_width / u_height;
            
            // 1. SMART PARAMETRIC FACE MASK & BEAUTIFICATION
            float skinMask = 0.0;
            if (u_has_face > 0.5) {
                // Determine elliptical face bounds
                vec2 faceRadius = vec2(
                    abs(u_face_bound.z - u_face_bound.x) * 0.5,
                    abs(u_face_bound.w - u_face_bound.y) * 0.5
                );
                
                vec2 faceDiff = (var_tc - u_face_center) / max(faceRadius, vec2(0.01));
                float inFace = smoothstep(1.1, 0.7, length(faceDiff));
                
                // Exclude eyes
                float nearLeftEye = smoothstep(0.035, 0.065, length((var_tc - u_left_eye) * vec2(aspect, 1.0)));
                float nearRightEye = smoothstep(0.035, 0.065, length((var_tc - u_right_eye) * vec2(aspect, 1.0)));
                
                // Exclude mouth / lips
                float nearMouth = smoothstep(0.035, 0.075, length((var_tc - u_mouth_center) * vec2(aspect, 1.0)));
                
                skinMask = inFace * nearLeftEye * nearRightEye * nearMouth;
            }
            
            // 2. SKIN SMOOTHING (GPU edge-preserving bilateral blur approximation)
            if (skinMask > 0.01) {
                float dx = 1.0 / u_width;
                float dy = 1.0 / u_height;
                
                vec3 blur = color * 0.36;
                blur += texture2D(u_texture, var_tc + vec2(-dx, 0.0)).rgb * 0.16;
                blur += texture2D(u_texture, var_tc + vec2(dx, 0.0)).rgb * 0.16;
                blur += texture2D(u_texture, var_tc + vec2(0.0, -dy)).rgb * 0.16;
                blur += texture2D(u_texture, var_tc + vec2(0.0, dy)).rgb * 0.16;
                
                // Blend smoothed skin
                color = mix(color, blur, skinMask * $smoothStrength);
                
                // Lift face exposure
                color = mix(color, color * 1.14, skinMask * $beautyBrightness);
            }
            
            // 3. ROSY CHEEK EFFECT
            if (u_has_face > 0.5) {
                float cheekRadius = 0.065;
                float leftCheekDist = length((var_tc - u_left_cheek) * vec2(aspect, 1.0));
                float rightCheekDist = length((var_tc - u_right_cheek) * vec2(aspect, 1.0));
                
                float leftCheekBlush = smoothstep(cheekRadius, cheekRadius * 0.3, leftCheekDist);
                float rightCheekBlush = smoothstep(cheekRadius, cheekRadius * 0.3, rightCheekDist);
                
                vec3 blushColor = vec3(0.97, 0.58, 0.58); // Flattering peach-rose
                color = mix(color, blushColor, (leftCheekBlush + rightCheekBlush) * $rosyCheekIntensity);
                
                // Lip tint enhancement
                float lipDist = length((var_tc - u_mouth_center) * vec2(aspect, 1.0));
                float lipMask = smoothstep(0.045, 0.015, lipDist);
                vec3 lipTint = vec3(0.94, 0.44, 0.44); // Healthy natural crimson
                color = mix(color, color * vec3(1.12, 1.02, 1.02) + lipTint * 0.05, lipMask * $lipWarmthIntensity);
            }
            
            // 4. 90s CINEMATIC COLOR GRADE (Dreamy, soft-vintage, premium film)
            // Desaturate slightly
            float gray = dot(color, vec3(0.299, 0.587, 0.114));
            color = mix(vec3(gray), color, 0.94);
            
            // Contrast curve
            color = smoothstep(0.0, 1.0, color);
            
            // Lift black levels slightly for classic film shadow depth
            color = color * 0.94 + vec3(0.035, 0.035, 0.035);
            
            // Highlight warming & Shadow cooling
            color.rg += vec3(0.012, 0.005) * smoothstep(0.35, 0.9, gray);
            color.b += 0.008 * smoothstep(0.65, 0.1, gray);
            
            // 5. FLATTERING DIFFUSION (Subtle lens bloom approximation)
            vec3 bloom = vec3(gray * 0.12);
            color += bloom * 0.45;
            
            // 6. ASPECT-RATIO-AWARE SOFT DARK VIGNETTE
            vec2 vignetteUV = var_tc - vec2(0.5);
            vignetteUV.x *= aspect;
            float distFromCenter = length(vignetteUV);
            float vignette = smoothstep(0.38, 0.88, distFromCenter);
            color *= (1.0 - vignette * $vignetteStrength);
            
            gl_FragColor = vec4(color, 1.0);
        }
    """.trimIndent()

    /**
     * Compiles GLES shaders and programs on the GL thread.
     */
    fun compile() {
        if (program != 0) return // Already compiled

        vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderSource)
        fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderSource)

        program = GLES20.glCreateProgram()
        GLES20.glAttachShader(program, vertexShader)
        GLES20.glAttachShader(program, fragmentShader)
        GLES20.glLinkProgram(program)

        val linkStatus = IntArray(1)
        GLES20.glGetProgramiv(program, GLES20.GL_LINK_STATUS, linkStatus, 0)
        if (linkStatus[0] == 0) {
            val log = GLES20.glGetProgramInfoLog(program)
            Log.e(tag, "Error linking program: $log")
            GLES20.glDeleteProgram(program)
            program = 0
            return
        }

        // Cache uniform positions
        texMatrixLoc = GLES20.glGetUniformLocation(program, "tex_matrix")
        widthLoc = GLES20.glGetUniformLocation(program, "u_width")
        heightLoc = GLES20.glGetUniformLocation(program, "u_height")
        hasFaceLoc = GLES20.glGetUniformLocation(program, "u_has_face")
        faceCenterLoc = GLES20.glGetUniformLocation(program, "u_face_center")
        faceBoundLoc = GLES20.glGetUniformLocation(program, "u_face_bound")
        leftEyeLoc = GLES20.glGetUniformLocation(program, "u_left_eye")
        rightEyeLoc = GLES20.glGetUniformLocation(program, "u_right_eye")
        mouthCenterLoc = GLES20.glGetUniformLocation(program, "u_mouth_center")
        leftCheekLoc = GLES20.glGetUniformLocation(program, "u_left_cheek")
        rightCheekLoc = GLES20.glGetUniformLocation(program, "u_right_cheek")

        Log.d(tag, "GL Program successfully linked with cached uniforms.")
    }

    /**
     * Initializes FBO output texture
     */
    private fun setupFBO(width: Int, height: Int) {
        if (width == fboWidth && height == fboHeight && fboId != 0) {
            return
        }
        releaseFBO()

        fboWidth = width
        fboHeight = height

        val textures = IntArray(1)
        GLES20.glGenTextures(1, textures, 0)
        outputTextureId = textures[0]
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, outputTextureId)
        GLES20.glTexImage2D(
            GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA,
            fboWidth, fboHeight, 0, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, null
        )
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR)
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR)
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE)
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE)

        val fbos = IntArray(1)
        GLES20.glGenFramebuffers(1, fbos, 0)
        fboId = fbos[0]
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, fboId)
        GLES20.glFramebufferTexture2D(
            GLES20.GL_FRAMEBUFFER, GLES20.GL_COLOR_ATTACHMENT0,
            GLES20.GL_TEXTURE_2D, outputTextureId, 0
        )

        val status = GLES20.glCheckFramebufferStatus(GLES20.GL_FRAMEBUFFER)
        if (status != GLES20.GL_FRAMEBUFFER_COMPLETE) {
            Log.e(tag, "Framebuffer incomplete status: $status")
        }

        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0)
        Log.d(tag, "FBO recreated: size=${fboWidth}x${fboHeight}")
    }

    /**
     * Executes the shader pipeline on the input texture and outputs the final processed texture ID.
     */
    fun process(inputTextureId: Int, width: Int, height: Int, transformMatrix: FloatArray): Int {
        compile()
        if (program == 0) return inputTextureId

        setupFBO(width, height)

        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, fboId)
        GLES20.glViewport(0, 0, width, height)

        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f)
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)

        GLES20.glUseProgram(program)

        // Set dimensions
        GLES20.glUniform1f(widthLoc, width.toFloat())
        GLES20.glUniform1f(heightLoc, height.toFloat())

        // Pass transform matrix
        GLES20.glUniformMatrix4fv(texMatrixLoc, 1, false, transformMatrix, 0)

        // Pass face tracker landmarks from volatile FaceTrackerState
        if (FaceTrackerState.hasFace) {
            GLES20.glUniform1f(hasFaceLoc, 1.0f)
            GLES20.glUniform2f(faceCenterLoc, FaceTrackerState.faceCenter.x, FaceTrackerState.faceCenter.y)
            GLES20.glUniform4f(
                faceBoundLoc,
                FaceTrackerState.faceBoundingBox.left,
                FaceTrackerState.faceBoundingBox.top,
                FaceTrackerState.faceBoundingBox.right,
                FaceTrackerState.faceBoundingBox.bottom
            )
            GLES20.glUniform2f(leftEyeLoc, FaceTrackerState.leftEye.x, FaceTrackerState.leftEye.y)
            GLES20.glUniform2f(rightEyeLoc, FaceTrackerState.rightEye.x, FaceTrackerState.rightEye.y)
            GLES20.glUniform2f(mouthCenterLoc, FaceTrackerState.mouthCenter.x, FaceTrackerState.mouthCenter.y)
            GLES20.glUniform2f(leftCheekLoc, FaceTrackerState.leftCheek.x, FaceTrackerState.leftCheek.y)
            GLES20.glUniform2f(rightCheekLoc, FaceTrackerState.rightCheek.x, FaceTrackerState.rightCheek.y)
        } else {
            GLES20.glUniform1f(hasFaceLoc, 0.0f)
        }

        // Draw quad geometry
        val positionHandle = GLES20.glGetAttribLocation(program, "in_position")
        GLES20.glEnableVertexAttribArray(positionHandle)
        vertexBuffer.position(0)
        GLES20.glVertexAttribPointer(positionHandle, 2, GLES20.GL_FLOAT, false, 16, vertexBuffer)

        val tcHandle = GLES20.glGetAttribLocation(program, "in_tc")
        GLES20.glEnableVertexAttribArray(tcHandle)
        vertexBuffer.position(2)
        GLES20.glVertexAttribPointer(tcHandle, 2, GLES20.GL_FLOAT, false, 16, vertexBuffer)

        // Bind input camera OES texture
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0)
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, inputTextureId)
        val texUniform = GLES20.glGetUniformLocation(program, "u_texture")
        GLES20.glUniform1i(texUniform, 0)

        // Render full screen quad
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4)

        // Cleanup bindings
        GLES20.glDisableVertexAttribArray(positionHandle)
        GLES20.glDisableVertexAttribArray(tcHandle)
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, 0)
        GLES20.glUseProgram(0)
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0)

        return outputTextureId
    }

    private fun loadShader(type: Int, shaderCode: String): Int {
        val shader = GLES20.glCreateShader(type)
        GLES20.glShaderSource(shader, shaderCode)
        GLES20.glCompileShader(shader)

        val compileStatus = IntArray(1)
        GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, compileStatus, 0)
        if (compileStatus[0] == 0) {
            val log = GLES20.glGetShaderInfoLog(shader)
            Log.e(tag, "Error compiling shader (type=$type): $log")
            GLES20.glDeleteShader(shader)
            return 0
        }
        return shader
    }

    private fun releaseFBO() {
        if (fboId != 0) {
            val fbos = intArrayOf(fboId)
            GLES20.glDeleteFramebuffers(1, fbos, 0)
            fboId = 0
        }
        if (outputTextureId != 0) {
            val textures = intArrayOf(outputTextureId)
            GLES20.glDeleteTextures(1, textures, 0)
            outputTextureId = 0
        }
    }

    /**
     * Deletes OpenGL handles to prevent graphics memory leaks.
     */
    fun release() {
        releaseFBO()
        if (program != 0) {
            GLES20.glDeleteProgram(program)
            program = 0
        }
        if (vertexShader != 0) {
            GLES20.glDeleteShader(vertexShader)
            vertexShader = 0
        }
        if (fragmentShader != 0) {
            GLES20.glDeleteShader(fragmentShader)
            fragmentShader = 0
        }
        Log.d(tag, "GL filter resources released.")
    }
}
