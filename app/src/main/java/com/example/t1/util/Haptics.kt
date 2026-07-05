package com.example.t1.util

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.view.HapticFeedbackConstants
import android.view.SoundEffectConstants
import android.view.View

object Haptics {
    private const val PREFS_NAME = "t1_haptics_prefs"
    private const val KEY_HAPTICS = "haptics_enabled"
    private const val KEY_SOUNDS = "sounds_enabled"

    private var _isEnabled: Boolean? = null
    private var _isSoundEnabled: Boolean? = null

    fun init(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        _isEnabled = prefs.getBoolean(KEY_HAPTICS, true)
        _isSoundEnabled = prefs.getBoolean(KEY_SOUNDS, true)
    }

    var isEnabled: Boolean
        get() = _isEnabled ?: true
        set(value) {
            _isEnabled = value
        }

    var isSoundEnabled: Boolean
        get() = _isSoundEnabled ?: true
        set(value) {
            _isSoundEnabled = value
        }

    fun save(context: Context, hapticsEnabled: Boolean, soundsEnabled: Boolean) {
        _isEnabled = hapticsEnabled
        _isSoundEnabled = soundsEnabled
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit()
            .putBoolean(KEY_HAPTICS, hapticsEnabled)
            .putBoolean(KEY_SOUNDS, soundsEnabled)
            .apply()
    }

    fun playLight(view: View) {
        if (isSoundEnabled) {
            view.playSoundEffect(SoundEffectConstants.CLICK)
        }
        if (isEnabled) {
            view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
        }
    }

    fun playMedium(view: View) {
        if (isSoundEnabled) {
            view.playSoundEffect(SoundEffectConstants.CLICK)
        }
        if (isEnabled) {
            view.performHapticFeedback(HapticFeedbackConstants.CONTEXT_CLICK)
        }
    }

    fun playHeavy(view: View) {
        if (isSoundEnabled) {
            view.playSoundEffect(SoundEffectConstants.CLICK)
        }
        if (isEnabled) {
            view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
        }
    }

    fun playSuccess(context: Context) {
        if (!isEnabled) return
        val vibrator = getVibrator(context)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val timings = longArrayOf(0, 30, 80, 40)
            val amplitudes = intArrayOf(0, 100, 0, 180)
            vibrator?.vibrate(VibrationEffect.createWaveform(timings, amplitudes, -1))
        } else {
            @Suppress("DEPRECATION")
            vibrator?.vibrate(longArrayOf(0, 30, 80, 40), -1)
        }
    }

    fun playError(context: Context) {
        if (!isEnabled) return
        val vibrator = getVibrator(context)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val timings = longArrayOf(0, 60, 100, 60, 100, 60)
            val amplitudes = intArrayOf(0, 200, 0, 200, 0, 200)
            vibrator?.vibrate(VibrationEffect.createWaveform(timings, amplitudes, -1))
        } else {
            @Suppress("DEPRECATION")
            vibrator?.vibrate(longArrayOf(0, 60, 100, 60, 100, 60), -1)
        }
    }

    private fun getVibrator(context: Context): Vibrator? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = context.getSystemService(VibratorManager::class.java)
            vibratorManager?.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        }
    }
}
