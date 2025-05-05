package com.example.moviedbapplication.database

import android.content.Context
import androidx.datastore.preferences.preferencesDataStore
import androidx.datastore.preferences.core.Preferences

val Context.dataStore: androidx.datastore.core.DataStore<Preferences> by preferencesDataStore(name = "user_prefs")