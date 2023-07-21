package com.krishna.stitch.Settings

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Switch
import com.krishna.stitch.R

class PrivacySettingsActivity : AppCompatActivity() {
    private lateinit var switchPrivateAccount: Switch
    private lateinit var switchHideLastSeen: Switch
    private lateinit var switchBlockContacts: Switch

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_privacy_settings)

        switchPrivateAccount = findViewById(R.id.switchPrivateAccount)
        switchHideLastSeen = findViewById(R.id.switchHideLastSeen)
        switchBlockContacts = findViewById(R.id.switchBlockContacts)

        loadPrivacySettings()

        switchPrivateAccount.setOnCheckedChangeListener { _, isChecked ->
            savePrivateAccountSetting(isChecked)
        }

        switchHideLastSeen.setOnCheckedChangeListener { _, isChecked ->
            saveHideLastSeenSetting(isChecked)
        }

        switchBlockContacts.setOnCheckedChangeListener { _, isChecked ->
            saveBlockContactsSetting(isChecked)
        }
    }

    private fun loadPrivacySettings() {
        val sharedPreferences = getSharedPreferences("PrivacySettings", Context.MODE_PRIVATE)
        switchPrivateAccount.isChecked = sharedPreferences.getBoolean("privateAccount", false)
        switchHideLastSeen.isChecked = sharedPreferences.getBoolean("hideLastSeen", false)
        switchBlockContacts.isChecked = sharedPreferences.getBoolean("blockContacts", false)
    }

    private fun savePrivateAccountSetting(isPrivate: Boolean) {
        val sharedPreferences = getSharedPreferences("PrivacySettings", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putBoolean("privateAccount", isPrivate)
        editor.apply()
    }

    private fun saveHideLastSeenSetting(isHidden: Boolean) {
        val sharedPreferences = getSharedPreferences("PrivacySettings", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putBoolean("hideLastSeen", isHidden)
        editor.apply()
    }

    private fun saveBlockContactsSetting(isBlocked: Boolean) {
        val sharedPreferences = getSharedPreferences("PrivacySettings", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putBoolean("blockContacts", isBlocked)
        editor.apply()
    }
}
