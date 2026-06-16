package com.example.application

import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

val Context.dataStore by preferencesDataStore(name = "user_stock_file")

data class UserProfile(
    val name: String = "",
    val age: String = "",
    val weight: String = "",
    val height: String = "",
    val maxPushups: String = "0",
    val maxPullups: String = "0",
    val maxSquats: String = "0",
    val maxRunningKm: String = "0.0",
    val lastExerciseDate: Long = 0L,
    val lastMotivationDate: String = "",
    val lastMotivationLevel: Float = 0.5f,
    val hasCompletedOnboarding: Boolean = false,
    // Partner info
    val partnerLastName: String = "",
    val partnerFirstName: String = "",
    val partnerCompany: String = ""
)

// Keys
private val NAME = stringPreferencesKey("name")
private val AGE = stringPreferencesKey("age")
private val WEIGHT = stringPreferencesKey("weight")
private val HEIGHT = stringPreferencesKey("height")
private val MAX_PUSHUPS = stringPreferencesKey("max_pushups")
private val MAX_PULLUPS = stringPreferencesKey("max_pullups")
private val MAX_SQUATS = stringPreferencesKey("max_squats")
private val MAX_RUNNING_KM = stringPreferencesKey("max_running_km")
private val LAST_EXERCISE_DATE = longPreferencesKey("last_exercise_date")
private val LAST_MOTIVATION_DATE = stringPreferencesKey("last_motivation_date")
private val LAST_MOTIVATION_LEVEL = floatPreferencesKey("last_motivation_level")
private val HAS_COMPLETED_ONBOARDING = booleanPreferencesKey("has_completed_onboarding")
// Partner Keys
private val PARTNER_LAST_NAME = stringPreferencesKey("partner_last_name")
private val PARTNER_FIRST_NAME = stringPreferencesKey("partner_first_name")
private val PARTNER_COMPANY = stringPreferencesKey("partner_company")

// Read flow
val Context.userProfileFlow: Flow<UserProfile>
    get() = dataStore.data.map { prefs ->
        UserProfile(
            name = prefs[NAME] ?: "",
            age = prefs[AGE] ?: "",
            weight = prefs[WEIGHT] ?: "",
            height = prefs[HEIGHT] ?: "",
            maxPushups = prefs[MAX_PUSHUPS] ?: "0",
            maxPullups = prefs[MAX_PULLUPS] ?: "0",
            maxSquats = prefs[MAX_SQUATS] ?: "0",
            maxRunningKm = prefs[MAX_RUNNING_KM] ?: "0.0",
            lastExerciseDate = prefs[LAST_EXERCISE_DATE] ?: 0L,
            lastMotivationDate = prefs[LAST_MOTIVATION_DATE] ?: "",
            lastMotivationLevel = prefs[LAST_MOTIVATION_LEVEL] ?: 0.5f,
            hasCompletedOnboarding = prefs[HAS_COMPLETED_ONBOARDING] ?: false,
            partnerLastName = prefs[PARTNER_LAST_NAME] ?: "",
            partnerFirstName = prefs[PARTNER_FIRST_NAME] ?: "",
            partnerCompany = prefs[PARTNER_COMPANY] ?: ""
        )
    }

// Atomic update functions
suspend fun Context.updatePersonalProfile(name: String, age: String, weight: String, height: String) {
    dataStore.edit { prefs ->
        prefs[NAME] = name
        prefs[AGE] = age
        prefs[WEIGHT] = weight
        prefs[HEIGHT] = height
    }
}

suspend fun Context.updateCapacity(maxPushups: String, maxPullups: String, maxSquats: String, maxRunningKm: String) {
    dataStore.edit { prefs ->
        prefs[MAX_PUSHUPS] = maxPushups
        prefs[MAX_PULLUPS] = maxPullups
        prefs[MAX_SQUATS] = maxSquats
        prefs[MAX_RUNNING_KM] = maxRunningKm
    }
}

suspend fun Context.updateMotivation(level: Float) {
    val today = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
    dataStore.edit { prefs ->
        prefs[LAST_MOTIVATION_DATE] = today
        prefs[LAST_MOTIVATION_LEVEL] = level
        prefs[HAS_COMPLETED_ONBOARDING] = true
    }
}

suspend fun Context.updatePartnerProfile(lastName: String, firstName: String, company: String) {
    dataStore.edit { prefs ->
        prefs[PARTNER_LAST_NAME] = lastName
        prefs[PARTNER_FIRST_NAME] = firstName
        prefs[PARTNER_COMPANY] = company
    }
}

suspend fun Context.markExerciseDone() {
    dataStore.edit { prefs ->
        prefs[LAST_EXERCISE_DATE] = System.currentTimeMillis()
    }
}

suspend fun Context.saveUserProfile(profile: UserProfile) {
    dataStore.edit { prefs ->
        prefs[NAME] = profile.name
        prefs[AGE] = profile.age
        prefs[WEIGHT] = profile.weight
        prefs[HEIGHT] = profile.height
        prefs[MAX_PUSHUPS] = profile.maxPushups
        prefs[MAX_PULLUPS] = profile.maxPullups
        prefs[MAX_SQUATS] = profile.maxSquats
        prefs[MAX_RUNNING_KM] = profile.maxRunningKm
        prefs[LAST_EXERCISE_DATE] = profile.lastExerciseDate
        prefs[LAST_MOTIVATION_DATE] = profile.lastMotivationDate
        prefs[LAST_MOTIVATION_LEVEL] = profile.lastMotivationLevel
        prefs[HAS_COMPLETED_ONBOARDING] = profile.hasCompletedOnboarding
        prefs[PARTNER_LAST_NAME] = profile.partnerLastName
        prefs[PARTNER_FIRST_NAME] = profile.partnerFirstName
        prefs[PARTNER_COMPANY] = profile.partnerCompany
    }
}
