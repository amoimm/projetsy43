package com.example.application

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore by preferencesDataStore(name = "user_stock_file")

data class UserProfile(
    val name: String = "",
    val age: String = "",
    val weight: String = "",
    val height: String = ""
)

//keys
private val NAME = stringPreferencesKey("name")
private val AGE = stringPreferencesKey("age")
private val WEIGHT = stringPreferencesKey("weight")
private val HEIGHT = stringPreferencesKey("height")

// read flow --> watch on changes and update on screen (ask from MainActivity)
val Context.userProfileFlow: Flow<UserProfile>
    get() = dataStore.data.map { prefs ->
        UserProfile(
            name = prefs[NAME] ?: "",
            age = prefs[AGE] ?: "",
            weight = prefs[WEIGHT] ?: "",
            height = prefs[HEIGHT] ?: ""
        )
    }

// save function --> Save in Context
suspend fun Context.saveUserProfile(profile: UserProfile) {
    dataStore.edit { prefs ->
        prefs[NAME] = profile.name
        prefs[AGE] = profile.age
        prefs[WEIGHT] = profile.weight
        prefs[HEIGHT] = profile.height
    }
}