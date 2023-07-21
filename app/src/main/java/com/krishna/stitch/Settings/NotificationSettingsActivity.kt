package com.krishna.stitch.Settings

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Switch
import com.krishna.stitch.R

class NotificationSettingsActivity : AppCompatActivity() {

    private lateinit var switchNotification: Switch
    private lateinit var switchSound: Switch
    private lateinit var switchVibration: Switch

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notification_settings)

        // Initialize UI components
        switchNotification = findViewById(R.id.switchNotification)
        switchSound = findViewById(R.id.switchSound)
        switchVibration = findViewById(R.id.switchVibration)

        // Load notification settings from preferences or other storage mechanism
        val notificationEnabled = loadNotificationSetting()
        val soundEnabled = loadSoundSetting()
        val vibrationEnabled = loadVibrationSetting()

        // Set the initial state of switches based on loaded settings
        switchNotification.isChecked = notificationEnabled
        switchSound.isChecked = soundEnabled
        switchVibration.isChecked = vibrationEnabled

        // Set up switch change listeners to update the notification settings
        switchNotification.setOnCheckedChangeListener { _, isChecked ->
            saveNotificationSetting(isChecked)
        }

        switchSound.setOnCheckedChangeListener { _, isChecked ->
            saveSoundSetting(isChecked)
        }

        switchVibration.setOnCheckedChangeListener { _, isChecked ->
            saveVibrationSetting(isChecked)
        }
    }

    private fun loadNotificationSetting(): Boolean {
        // Retrieve the notification setting value from SharedPreferences or any other storage mechanism
        val sharedPreferences = getSharedPreferences("NotificationSettings", Context.MODE_PRIVATE)
        return sharedPreferences.getBoolean("enableNotifications", true)
    }

    private fun loadSoundSetting(): Boolean  {
        // Retrieve the sound setting value from SharedPreferences or any other storage mechanism
        val sharedPreferences = getSharedPreferences("NotificationSettings", Context.MODE_PRIVATE)
        return sharedPreferences.getBoolean("enableSound", true)
    }

    private fun loadVibrationSetting(): Boolean {
        // Retrieve the vibration setting value from SharedPreferences or any other storage mechanism
        val sharedPreferences = getSharedPreferences("NotificationSettings", Context.MODE_PRIVATE)
        return sharedPreferences.getBoolean("enableVibration", true)
    }

    private fun saveNotificationSetting(enabled: Boolean) {
        val sharedPreferences = getSharedPreferences("NotificationSettings", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putBoolean("enableNotification", enabled)
        editor.apply()
    }

    private fun saveSoundSetting(enabled: Boolean) {
        val sharedPreferences = getSharedPreferences("NotificationSettings", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putBoolean("enableSound", enabled)
        editor.apply()
    }

    private fun saveVibrationSetting(enabled: Boolean) {
        val sharedPreferences = getSharedPreferences("NotificationSettings", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putBoolean("enableVibration", enabled)
        editor.apply()
    }
}
