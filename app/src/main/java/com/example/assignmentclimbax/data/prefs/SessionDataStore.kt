package com.example.assignmentclimbax.data.prefs

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.assignmentclimbax.domain.model.UserSession
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.sessionDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "user_session"
)

class SessionDataStore(private val context: Context) {

    val sessionFlow: Flow<UserSession?> = context.sessionDataStore.data.map { prefs ->
        val id = prefs[KEY_ID] ?: return@map null
        UserSession(
            id = id,
            email = prefs[KEY_EMAIL].orEmpty(),
            firstName = prefs[KEY_FIRST_NAME].orEmpty(),
            lastName = prefs[KEY_LAST_NAME].orEmpty(),
            image = prefs[KEY_IMAGE].orEmpty()
        )
    }

    suspend fun saveSession(
        id: Int,
        email: String,
        firstName: String,
        lastName: String,
        image: String
    ) {
        context.sessionDataStore.edit { prefs ->
            prefs[KEY_ID] = id
            prefs[KEY_EMAIL] = email
            prefs[KEY_FIRST_NAME] = firstName
            prefs[KEY_LAST_NAME] = lastName
            prefs[KEY_IMAGE] = image
        }
    }

    suspend fun getSession(): UserSession? = sessionFlow.first()

    suspend fun clear() {
        context.sessionDataStore.edit { it.clear() }
    }

    suspend fun isLoggedIn(): Boolean {
        val session = getSession() ?: return false
        return session.id > 0
    }

    companion object {
        private val KEY_ID = intPreferencesKey("id")
        private val KEY_EMAIL = stringPreferencesKey("email")
        private val KEY_FIRST_NAME = stringPreferencesKey("first_name")
        private val KEY_LAST_NAME = stringPreferencesKey("last_name")
        private val KEY_IMAGE = stringPreferencesKey("image")
    }
}
