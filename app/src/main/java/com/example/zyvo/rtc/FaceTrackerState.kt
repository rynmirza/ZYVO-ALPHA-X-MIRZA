package com.example.zyvo.rtc

import android.graphics.PointF
import android.graphics.RectF

/**
 * FaceTrackerState
 *
 * Thread-safe singleton containing real-time normalized face landmarks and bounds.
 * Written by the background CameraX ImageAnalysis thread and read by the OpenGL rendering thread.
 */
object FaceTrackerState {
    @Volatile
    var hasFace: Boolean = false

    @Volatile
    var faceCenter: PointF = PointF(0.5f, 0.5f)

    @Volatile
    var leftEye: PointF = PointF(0.4f, 0.4f)

    @Volatile
    var rightEye: PointF = PointF(0.6f, 0.4f)

    @Volatile
    var leftCheek: PointF = PointF(0.35f, 0.55f)

    @Volatile
    var rightCheek: PointF = PointF(0.65f, 0.55f)

    @Volatile
    var mouthCenter: PointF = PointF(0.5f, 0.7f)

    @Volatile
    var faceBoundingBox: RectF = RectF(0.3f, 0.3f, 0.7f, 0.7f)

    /**
     * Resets the face tracking state back to default center values when no face is detected.
     */
    fun reset() {
        hasFace = false
        faceCenter.set(0.5f, 0.5f)
        leftEye.set(0.4f, 0.4f)
        rightEye.set(0.6f, 0.4f)
        leftCheek.set(0.35f, 0.55f)
        rightCheek.set(0.65f, 0.55f)
        mouthCenter.set(0.5f, 0.7f)
        faceBoundingBox.set(0.3f, 0.3f, 0.7f, 0.7f)
    }
}
