package com.example.application

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore by preferencesDataStore(name = "user_stock_file")

data class UserProfile(
    val name: String = "",
    val age: String = "",
    val weight: String = "",
    val height: String = "",
    val lastExerciseDate: Long = 0L,
    val hasCompletedOnboarding: Boolean = false
)

//keys
private val NAME = stringPreferencesKey("name")
private val AGE = stringPreferencesKey("age")
private val WEIGHT = stringPreferencesKey("weight")
private val HEIGHT = stringPreferencesKey("height")
private val LAST_EXERCISE_DATE = longPreferencesKey("last_exercise_date")
private val HAS_COMPLETED_ONBOARDING = booleanPreferencesKey("has_completed_onboarding")

// read flow
val Context.userProfileFlow: Flow<UserProfile>
    get() = dataStore.data.map { prefs ->
        UserProfile(
            name = prefs[NAME] ?: "",
            age = prefs[AGE] ?: "",
            weight = prefs[WEIGHT] ?: "",
            height = prefs[HEIGHT] ?: "",
            lastExerciseDate = prefs[LAST_EXERCISE_DATE] ?: 0L,
            hasCompletedOnboarding = prefs[HAS_COMPLETED_ONBOARDING] ?: false
        )
    }

// save function
suspend fun Context.saveUserProfile(profile: UserProfile) {
    dataStore.edit { prefs ->
        prefs[NAME] = profile.name
        prefs[AGE] = profile.age
        prefs[WEIGHT] = profile.weight
        prefs[HEIGHT] = profile.height
        prefs[LAST_EXERCISE_DATE] = profile.lastExerciseDate
        prefs[HAS_COMPLETED_ONBOARDING] = profile.hasCompletedOnboarding
    }
}

suspend fun Context.setCompletedOnboarding() {
    dataStore.edit { prefs ->
        prefs[HAS_COMPLETED_ONBOARDING] = true
    }
}

suspend fun Context.markExerciseDone() {
    dataStore.edit { prefs ->
        prefs[LAST_EXERCISE_DATE] = System.currentTimeMillis()
    }
}
