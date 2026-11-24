package ru.example.fonarik

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.graphics.Color
import android.view.Gravity
import android.view.ViewGroup
import android.widget.Toast

class MainActivity : Activity() {
    private lateinit var cameraManager: CameraManager
    private var cameraId: String? = null
    private var isOn = false
    private lateinit var status: TextView
    private lateinit var toggle: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        cameraManager = getSystemService(Context.CAMERA_SERVICE) as CameraManager
        cameraId = cameraManager.cameraIdList.firstOrNull { id ->
            cameraManager.getCameraCharacteristics(id)
                .get(CameraCharacteristics.FLASH_INFO_AVAILABLE) == true
        }

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setPadding(32, 32, 32, 32)
            setBackgroundColor(Color.rgb(24, 27, 34))
        }

        val title = TextView(this).apply {
            text = "Фонарик"
            textSize = 30f
            setTextColor(Color.WHITE)
            gravity = Gravity.CENTER
        }

        status = TextView(this).apply {
            text = "ВЫКЛЮЧЕНО"
            textSize = 18f
            setTextColor(Color.LTGRAY)
            gravity = Gravity.CENTER
            setPadding(0, 28, 0, 28)
        }

        toggle = Button(this).apply {
            text = "ВКЛЮЧИТЬ ФОНАРИК"
            textSize = 18f
            isAllCaps = false
            setOnClickListener { switchFlashlight() }
        }

        val hint = TextView(this).apply {
            text = "Нажми кнопку, чтобы включить или выключить вспышку"
            textSize = 14f
            setTextColor(Color.LTGRAY)
            gravity = Gravity.CENTER
            setPadding(0, 28, 0, 0)
        }

        root.addView(title, LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT))
        root.addView(status, LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT))
        root.addView(toggle, LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT))
        root.addView(hint, LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT))
        setContentView(root)

        if (cameraId == null) {
            status.text = "На этом устройстве не найдена вспышка"
            toggle.isEnabled = false
        }
    }

    private fun switchFlashlight() {
        val id = cameraId ?: return
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                isOn = !isOn
                cameraManager.setTorchMode(id, isOn)
                updateUi()
            }
        } catch (e: Exception) {
            isOn = false
            updateUi()
            Toast.makeText(this, "Не удалось включить фонарик: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
        }
    }

    private fun updateUi() {
        status.text = if (isOn) "ВКЛЮЧЕНО" else "ВЫКЛЮЧЕНО"
        status.setTextColor(if (isOn) Color.YELLOW else Color.LTGRAY)
        toggle.text = if (isOn) "ВЫКЛЮЧИТЬ ФОНАРИК" else "ВКЛЮЧИТЬ ФОНАРИК"
    }

    override fun onPause() {
        super.onPause()
        if (isOn && cameraId != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            try {
                cameraManager.setTorchMode(cameraId!!, false)
            } catch (_: Exception) { }
            isOn = false
            if (::status.isInitialized) updateUi()
        }
    }
}
