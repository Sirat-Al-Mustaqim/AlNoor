package com.siratalmustaqim.alnoor.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

@Singleton
class SettingsDataStore @Inject constructor(
    @param:ApplicationContext private val context: Context
) {
    private val dataStore = context.dataStore

    // Quran Settings Keys
    private object QuranKeys {
        val AYAH_TEXT_SIZE = floatPreferencesKey("ayah_text_size")
        val AYAH_FONT = stringPreferencesKey("ayah_font")
    }

    // Prayer Settings Keys
    private object PrayerKeys {
        val CURRENT_LOCATION = stringPreferencesKey("current_location")
        val AZAN_AUDIO = stringPreferencesKey("azan_audio")
        val AUTO_DETECT_LOCATION = booleanPreferencesKey("auto_detect_location")
        val ADHAN_VOLUME = floatPreferencesKey("adhan_volume")
        val SILENT_DURING_PRAYER = booleanPreferencesKey("silent_during_prayer")
        val EARLY_REMINDER = booleanPreferencesKey("early_reminder")
    }

    // Guard Settings Keys
    private object GuardKeys {
        val VPN_ENABLED = booleanPreferencesKey("vpn_enabled")
        val ALWAYS_ON_PROTECTION = booleanPreferencesKey("always_on_protection")
        val OFFLINE_MODE = booleanPreferencesKey("offline_mode")
    }

    // Quran Settings
    val ayahTextSize: Flow<Float> = dataStore.data.map { prefs ->
        prefs[QuranKeys.AYAH_TEXT_SIZE] ?: 20f
    }

    val ayahFont: Flow<String> = dataStore.data.map { prefs ->
        prefs[QuranKeys.AYAH_FONT] ?: "Amiri"
    }

    suspend fun updateAyahTextSize(size: Float) {
        dataStore.edit { prefs ->
            prefs[QuranKeys.AYAH_TEXT_SIZE] = size
        }
    }

    suspend fun updateAyahFont(font: String) {
        dataStore.edit { prefs ->
            prefs[QuranKeys.AYAH_FONT] = font
        }
    }

    // Prayer Settings
    val currentLocation: Flow<String> = dataStore.data.map { prefs ->
        prefs[PrayerKeys.CURRENT_LOCATION] ?: "Not set"
    }

    val azanAudio: Flow<String> = dataStore.data.map { prefs ->
        prefs[PrayerKeys.AZAN_AUDIO] ?: "Default"
    }

    suspend fun updateCurrentLocation(location: String) {
        dataStore.edit { prefs ->
            prefs[PrayerKeys.CURRENT_LOCATION] = location
        }
    }

    suspend fun updateAzanAudio(audio: String) {
        dataStore.edit { prefs ->
            prefs[PrayerKeys.AZAN_AUDIO] = audio
        }
    }

    val autoDetectLocation: Flow<Boolean> = dataStore.data.map { prefs ->
        prefs[PrayerKeys.AUTO_DETECT_LOCATION] ?: true
    }

    suspend fun updateAutoDetectLocation(enabled: Boolean) {
        dataStore.edit { prefs ->
            prefs[PrayerKeys.AUTO_DETECT_LOCATION] = enabled
        }
    }

    val adhanVolume: Flow<Float> = dataStore.data.map { prefs ->
        prefs[PrayerKeys.ADHAN_VOLUME] ?: 0.85f
    }

    suspend fun updateAdhanVolume(volume: Float) {
        dataStore.edit { prefs ->
            prefs[PrayerKeys.ADHAN_VOLUME] = volume
        }
    }

    val silentDuringPrayer: Flow<Boolean> = dataStore.data.map { prefs ->
        prefs[PrayerKeys.SILENT_DURING_PRAYER] ?: false
    }

    suspend fun updateSilentDuringPrayer(enabled: Boolean) {
        dataStore.edit { prefs ->
            prefs[PrayerKeys.SILENT_DURING_PRAYER] = enabled
        }
    }

    val earlyReminder: Flow<Boolean> = dataStore.data.map { prefs ->
        prefs[PrayerKeys.EARLY_REMINDER] ?: true
    }

    suspend fun updateEarlyReminder(enabled: Boolean) {
        dataStore.edit { prefs ->
            prefs[PrayerKeys.EARLY_REMINDER] = enabled
        }
    }

    // Guard Settings
    val vpnEnabled: Flow<Boolean> = dataStore.data.map { prefs ->
        prefs[GuardKeys.VPN_ENABLED] ?: false
    }

    val alwaysOnProtection: Flow<Boolean> = dataStore.data.map { prefs ->
        prefs[GuardKeys.ALWAYS_ON_PROTECTION] ?: false
    }

    suspend fun updateVpnEnabled(enabled: Boolean) {
        dataStore.edit { prefs ->
            prefs[GuardKeys.VPN_ENABLED] = enabled
        }
    }

    suspend fun updateAlwaysOnProtection(enabled: Boolean) {
        dataStore.edit { prefs ->
            prefs[GuardKeys.ALWAYS_ON_PROTECTION] = enabled
        }
    }

    val offlineMode: Flow<Boolean> = dataStore.data.map { prefs ->
        prefs[GuardKeys.OFFLINE_MODE] ?: false
    }

    suspend fun updateOfflineMode(enabled: Boolean) {
        dataStore.edit { prefs ->
            prefs[GuardKeys.OFFLINE_MODE] = enabled
        }
    }
}
