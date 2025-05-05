package com.example.moviedbapplication.database

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map


class UserPreferencesRepository(private val context: Context) {

    companion object {
        private val SELECTED_LIST_KEY = stringPreferencesKey("selected_list")
    }

    // Read selected list as Flow
    val selectedListFlow: Flow<String> = context.dataStore.data
        .map { preferences ->
            preferences[SELECTED_LIST_KEY] ?: "Top Rated"
        }

    // Save selected list
    suspend fun saveSelectedList(listType: String) {
        context.dataStore.edit { preferences ->
            preferences[SELECTED_LIST_KEY] = listType
        }
    }
}