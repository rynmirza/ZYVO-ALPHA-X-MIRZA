package com.example.zyvo.rtc

import android.content.Context
import android.util.Log
import org.webrtc.DefaultVideoDecoderFactory
import org.webrtc.DefaultVideoEncoderFactory
import org.webrtc.EglBase
import org.webrtc.PeerConnectionFactory
import org.webrtc.audio.JavaAudioDeviceModule

/**
 * PeerConnectionFactoryProvider
 *
 * Thread-safe singleton managing the initialization, lifecycle, hardware EglBase,
 * and cleanup of the native Android WebRTC PeerConnectionFactory.
 */
object PeerConnectionFactoryProvider {
    private const val TAG = "ZYVO_PcfProvider"

    @Volatile
    private var isInitialized = false

    var eglBase: EglBase? = null
        private set

    private var factory: PeerConnectionFactory? = null
    private var audioDeviceModule: JavaAudioDeviceModule? = null

    /**
     * Initializes the global WebRTC engine and EGL context once.
     */
    @Synchronized
    fun initialize(context: Context) {
        if (isInitialized) return

        try {
            val initOptions = PeerConnectionFactory.InitializationOptions.builder(context.applicationContext)
                .setEnableInternalTracer(false)
                .setFieldTrials("WebRTC-H264HighProfile/Enabled/")
                .createInitializationOptions()
            PeerConnectionFactory.initialize(initOptions)

            if (eglBase == null) {
                eglBase = EglBase.create()
            }
            isInitialized = true
            Log.d(TAG, "PeerConnectionFactory initialized globally with EglBase")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to initialize global PeerConnectionFactory", e)
        }
    }

    /**
     * Provides a shared or freshly instantiated PeerConnectionFactory with hardware video
     * codecs and audio device module support.
     */
    @Synchronized
    fun getFactory(context: Context): PeerConnectionFactory {
        if (!isInitialized || eglBase == null) {
            initialize(context)
        }

        factory?.let { return it }

        val egl = eglBase ?: EglBase.create().also { eglBase = it }
        val encoderFactory = DefaultVideoEncoderFactory(egl.eglBaseContext, true, true)
        val decoderFactory = DefaultVideoDecoderFactory(egl.eglBaseContext)

        val adm = JavaAudioDeviceModule.builder(context.applicationContext)
            .setUseHardwareAcousticEchoCanceler(JavaAudioDeviceModule.isBuiltInAcousticEchoCancelerSupported())
            .setUseHardwareNoiseSuppressor(JavaAudioDeviceModule.isBuiltInNoiseSuppressorSupported())
            .createAudioDeviceModule()
        audioDeviceModule = adm

        val newFactory = PeerConnectionFactory.builder()
            .setAudioDeviceModule(adm)
            .setVideoEncoderFactory(encoderFactory)
            .setVideoDecoderFactory(decoderFactory)
            .setOptions(PeerConnectionFactory.Options().apply {
                disableNetworkMonitor = false
            })
            .createPeerConnectionFactory()

        factory = newFactory
        Log.d(TAG, "PeerConnectionFactory successfully created")
        return newFactory
    }

    /**
     * Completely cleans up the factory, audio module, and hardware EGL resources.
     */
    @Synchronized
    fun cleanup() {
        try {
            factory?.dispose()
            factory = null

            audioDeviceModule?.release()
            audioDeviceModule = null

            eglBase?.release()
            eglBase = null

            isInitialized = false
            Log.d(TAG, "PeerConnectionFactoryProvider successfully cleaned up")
        } catch (e: Exception) {
            Log.e(TAG, "Error cleaning up PeerConnectionFactoryProvider", e)
        }
    }
}
