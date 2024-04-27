package com.amefure.linkmark.Repository.DataStore

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.intPreferencesKey
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import kotlinx.coroutines.flow.Flow

class DataStoreRepository(private val context: Context) {

    companion object {
        private val INTERSTITIAL_COUNT = intPreferencesKey("interstitial_count")
        private val APP_LOCK_PASSWORD = intPreferencesKey("app_lock_password")
    }

    suspend fun saveInterstitialCount(count: Int) {
        try {
            context.dataStore.edit { preferences ->
                preferences[INTERSTITIAL_COUNT] = count
            }
        } catch (e: IOException) {
            print("例外が発生したよ")
        }
    }

    public fun observeInterstitialCount(): Flow<Int?> {
        return context.dataStore.data.catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }.map { preferences ->
            preferences[INTERSTITIAL_COUNT]
        }
    }

    suspend fun saveAppLockPassword(pass: Int) {
        try {
            context.dataStore.edit { preferences ->
                preferences[APP_LOCK_PASSWORD] = pass
            }
        } catch (e: IOException) {
            print("例外が発生したよ")
        }
    }

    public fun observeAppLockPassword(): Flow<Int?> {
        return context.dataStore.data.catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }.map { preferences ->
            preferences[APP_LOCK_PASSWORD]
        }
    }
}