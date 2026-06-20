package com.example.application

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.application.ui.*
import com.example.application.ui.theme.ApplicationTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import com.example.application.ui.bdd.*
import androidx.compose.runtime.livedata.observeAsState
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import org.osmdroid.config.Configuration
import kotlin.random.Random

class TaskViewModelFactory(private val repository: TaskRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TaskViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TaskViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

class MainActivity : ComponentActivity() {

    private val taskViewModel: TaskViewModel by viewModels {
        val database = TaskDatabase.getDatabase(applicationContext)
        val repository = OfflineTaskRepository(database.taskDao())
        TaskViewModelFactory(repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Configuration.getInstance().userAgentValue = packageName
        val sharedPrefs = getSharedPreferences("osmdroid", Context.MODE_PRIVATE)
        Configuration.getInstance().load(this, sharedPrefs)
        
        enableEdgeToEdge()
        setContent {
            val context = LocalContext.current
            val coroutineScope = rememberCoroutineScope()
            
            val launcher = rememberLauncherForActivityResult(
                ActivityResultContracts.RequestPermission()
            ) { isGranted ->
                if (isGranted) {
                    NotificationScheduler.scheduleDailyReminder(context)
                }
            }

            LaunchedEffect(Unit) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    if (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                        launcher.launch(Manifest.permission.POST_NOTIFICATIONS)
                    } else {
                        NotificationScheduler.scheduleDailyReminder(context)
                    }
                } else {
                    NotificationScheduler.scheduleDailyReminder(context)
                }
            }

            ApplicationTheme {
                val userProfileData by userProfileFlow.collectAsState(initial = null)
                var currentScreen by remember { mutableStateOf("loading") }

                // Animation logic for startup
                LaunchedEffect(userProfileData) {
                    if (userProfileData != null && currentScreen == "loading") {
                        if (userProfileData!!.hasCompletedOnboarding) {
                            currentScreen = "welcome_back"
                            delay(2500)
                            currentScreen = "Main"
                        } else {
                            currentScreen = "welcome"
                        }
                    }
                }

                val toDoLists by taskViewModel.allToDoLists.observeAsState(initial = emptyList())
                val ads by taskViewModel.allAds.observeAsState(initial = emptyList())
                var activeListIndex by remember { mutableStateOf<Int?>(null) }
                var activeActivityIndex by remember { mutableStateOf<Int?>(null) }

                // Partner Dashboard States
                var dashboardStartTime by remember { mutableLongStateOf(0L) }
                var dashboardAdId by remember { mutableIntStateOf(-1) }
                
                val totalImpressions by taskViewModel.getTotalImpressions(dashboardStartTime).collectAsState(initial = 0)
                val totalUniqueUsers by taskViewModel.getTotalUniqueUsers(dashboardStartTime).collectAsState(initial = 0)
                val adImpressions by taskViewModel.getAdImpressions(dashboardAdId, dashboardStartTime).collectAsState(initial = 0)
                val adUniqueUsers by taskViewModel.getAdUniqueUsers(dashboardAdId, dashboardStartTime).collectAsState(initial = 0)

                // AD LOGIC
                var adToShow by remember { mutableStateOf<Ad?>(null) }
                var nextScreenAfterAd by remember { mutableStateOf("Main") }

                fun triggerAd(location: String, nextScreen: String) {
                    // Check if any ad has the requested location in its trigger string
                    val possibleAds = ads.filter { it.triggerLocation.split(",").contains(location) }
                    
                    // Even if no ads match, we go to show_ad. VideoAdScreen will handle the fallback.
                    adToShow = if (possibleAds.isNotEmpty()) {
                        possibleAds[Random.nextInt(possibleAds.size)]
                    } else {
                        null
                    }
                    
                    nextScreenAfterAd = nextScreen
                    currentScreen = "show_ad"
                }

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    AnimatedContent(
                        targetState = currentScreen,
                        transitionSpec = { fadeIn(animationSpec = tween(750)).togetherWith(fadeOut(animationSpec = tween(750))) },
                        label = "screen_transition"
                    ) { targetScreen ->
                        when (targetScreen) {
                            "loading" -> { Box(modifier = Modifier.fillMaxSize().background(Color.Black)) }
                            "welcome_back" -> {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(Color.Black),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "Welcome back !",
                                        color = Color.White,
                                        fontSize = 48.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                            "welcome" -> WelcomeScreen(modifier = Modifier.padding(innerPadding), onContinueClick = { currentScreen = "mode_selection" })
                            "mode_selection" -> ModeSelectionScreen(
                                modifier = Modifier.padding(innerPadding),
                                onUserModeSelected = {
                                    if (userProfileData?.hasCompletedOnboarding == true) {
                                        currentScreen = "Main"
                                    } else currentScreen = "personal_info"
                                },
                                onPartnerModeSelected = {
                                    if (userProfileData?.partnerLastName?.isNotBlank() == true) currentScreen = "partner_dashboard"
                                    else currentScreen = "partner_info"
                                }
                            )
                            "partner_info" -> PartnerInfoScreen(
                                modifier = Modifier.padding(innerPadding),
                                initialLastName = userProfileData?.partnerLastName ?: "",
                                initialFirstName = userProfileData?.partnerFirstName ?: "",
                                initialCompany = userProfileData?.partnerCompany ?: "",
                                onBackClick = { 
                                    if (userProfileData?.partnerLastName?.isNotBlank() == true) currentScreen = "partner_dashboard"
                                    else currentScreen = "mode_selection"
                                },
                                onValidateClick = { ln, fn, co ->
                                    coroutineScope.launch { context.updatePartnerProfile(ln, fn, co) }
                                    currentScreen = "partner_dashboard"
                                }
                            )
                            "partner_dashboard" -> PartnerDashboardScreen(
                                ads = ads,
                                totalImpressions = totalImpressions,
                                totalUniqueUsers = totalUniqueUsers,
                                adSpecificImpressions = adImpressions,
                                adSpecificUniqueUsers = adUniqueUsers,
                                onPeriodChange = { dashboardStartTime = it },
                                onAdSelectionChange = { dashboardAdId = it },
                                modifier = Modifier.padding(innerPadding),
                                onBackClick = { 
                                    currentScreen = "mode_selection"
                                },
                                onSettingsClick = { currentScreen = "partner_info" },
                                onMyAdsClick = { currentScreen = "my_ads" }
                            )
                            "my_ads" -> MyAdsScreen(
                                ads = ads,
                                onBackClick = { currentScreen = "partner_dashboard" },
                                onAddAdClick = { currentScreen = "add_ad" },
                                onDeleteAdClick = { ad -> taskViewModel.deleteAd(ad) },
                                onPlayAdClick = { ad ->
                                    adToShow = ad
                                    nextScreenAfterAd = "my_ads"
                                    currentScreen = "show_ad"
                                }
                            )
                            "add_ad" -> AddAdScreen(
                                onBackClick = { currentScreen = "my_ads" },
                                onSaveAdClick = { ad ->
                                    taskViewModel.insertAd(ad)
                                    currentScreen = "my_ads"
                                }
                            )
                            "show_ad" -> VideoAdScreen(
                                ad = adToShow,
                                onAdFinished = { 
                                    // Insert metric
                                    adToShow?.let {
                                        val metric = AdMetric(adId = it.id, userName = userProfileData?.name ?: "Guest")
                                        taskViewModel.insertAdMetric(metric)
                                    }
                                    currentScreen = nextScreenAfterAd 
                                }
                            )
                            "settings_choice" -> SettingsChoiceScreen(
                                onBackClick = { currentScreen = "Main" },
                                onPersonalInfoClick = { currentScreen = "personal_info" },
                                onCapacityClick = { currentScreen = "capacite_info" },
                                onMotivationClick = { currentScreen = "HowYouFeel" }
                            )
                            "personal_info" -> PersonalInfoScreen(
                                modifier = Modifier.padding(innerPadding),
                                initialName = userProfileData?.name ?: "",
                                initialAge = userProfileData?.age ?: "",
                                initialWeight = userProfileData?.weight ?: "",
                                initialHeight = userProfileData?.height ?: "",
                                onBackClick = { 
                                    if (userProfileData?.hasCompletedOnboarding == true) currentScreen = "settings_choice"
                                    else currentScreen = "mode_selection"
                                },
                                onValidateClick = { n, a, w, h ->
                                    coroutineScope.launch { context.updatePersonalProfile(n, a, w, h) }
                                    if (userProfileData?.hasCompletedOnboarding == true) currentScreen = "settings_choice"
                                    else currentScreen = "capacite_info"
                                }
                            )
                            "capacite_info" -> CapaciteScreen(
                                modifier = Modifier.padding(innerPadding),
                                initialPushups = userProfileData?.maxPushups ?: "0",
                                initialPullups = userProfileData?.maxPullups ?: "0",
                                initialSquats = userProfileData?.maxSquats ?: "0",
                                initialRunningKm = userProfileData?.maxRunningKm ?: "0",
                                onBackClick = { 
                                    if (userProfileData?.hasCompletedOnboarding == true) currentScreen = "settings_choice"
                                    else currentScreen = "personal_info" 
                                },
                                onValidateClick = { push, pull, squat, run ->
                                    coroutineScope.launch {
                                        context.updateCapacity(push, pull, squat, run)
                                    }
                                    if (userProfileData?.hasCompletedOnboarding == true) {
                                        currentScreen = "settings_choice"
                                    } else {
                                        currentScreen = "HowYouFeel"
                                    }
                                }
                            )
                            "HowYouFeel" -> HowYouFeelScreen(
                                modifier = Modifier.padding(innerPadding),
                                userProfile = userProfileData,
                                onValidateClick = { generatedList, level ->
                                    coroutineScope.launch {
                                        context.updateMotivation(level)
                                        taskViewModel.insertToDoList(generatedList)
                                    }
                                    currentScreen = "Main" 
                                }
                            )
                            "Build_ToDo_List" -> BuildToDoListScreen(
                                onBackClick = { currentScreen = "Main" },
                                onValidateClick = { newList ->
                                    taskViewModel.insertToDoList(newList)
                                    triggerAd("AFTER_LIST", "Main?created=true")
                                }
                            )
                            "Main", "Main?created=true" -> {
                                val isJustCreated = targetScreen == "Main?created=true"
                                MainScreen(
                                    modifier = Modifier.padding(innerPadding),
                                    toDoLists = toDoLists,
                                    justCreated = isJustCreated,
                                    onValidateClick = { location ->
                                        if (location.contains("|")) {
                                            val parts = location.split("|")
                                            currentScreen = parts[0].replace(" ", "")
                                            activeListIndex = parts[1].toInt()
                                            activeActivityIndex = parts[2].toInt()
                                        } else {
                                            currentScreen = location
                                        }
                                    },
                                    onDeleteClick = { list -> 
                                        taskViewModel.deleteToDoList(list)
                                        triggerAd("AFTER_DELETE", "Main")
                                    },
                                    onSettingsClick = { currentScreen = "settings_choice" },
                                    onLogoutClick = { currentScreen = "mode_selection" }
                                )
                            }
                            "PushupScreen", "PullupScreen", "SquatScreen" -> {
                                val exerciseData = remember(activeListIndex, activeActivityIndex, toDoLists) {
                                    try {
                                        val list = toDoLists.getOrNull(activeListIndex ?: -1)
                                        val activity = list?.activities?.getOrNull(activeActivityIndex ?: -1)

                                        if (activity != null) {
                                            Triple(
                                                activity.categorie, // "Pushup", "Pullup" ou "Squat"
                                                activity.valeur.toInt(),
                                                activity.progress.toIntOrNull() ?: 0
                                            )
                                        } else {
                                            Triple("Pushup", 10, 0)
                                        }
                                    } catch (e: Exception) {
                                        Triple("Pushup", 10, 0)
                                    }
                                }

                                val (exerciseId, target, initialProgress) = exerciseData

                                val sensorThreshold = when(exerciseId) {
                                    "Squat" -> 0.45f   // even more amplitude
                                    "Pullup" -> 0.50f  // normal amplitude
                                    else -> 0.25f      // basic
                                }

                                BodyweightScreen(
                                    modifier = Modifier.padding(innerPadding),
                                    initialCount = initialProgress,
                                    targetObjective = target,
                                    exerciseType = exerciseId,
                                    threshold = sensorThreshold,
                                    onContinueClick = { finalCount ->
                                        if (activeListIndex != null && activeActivityIndex != null) {
                                            coroutineScope.launch {
                                                try {
                                                    val list = toDoLists[activeListIndex!!]
                                                    val activities = list.activitiesJson.split(";").toMutableList()
                                                    val parts = activities[activeActivityIndex!!].split(",").toMutableList()

                                                    while (parts.size < 4) parts.add("0")
                                                    parts[3] = finalCount.toString()

                                                    if (finalCount >= target) {
                                                        parts[2] = "true"
                                                        context.markExerciseDone()
                                                    }

                                                    activities[activeActivityIndex!!] = parts.joinToString(",")
                                                    val updatedList = list.copy(activitiesJson = activities.joinToString(";"))
                                                    taskViewModel.insertToDoList(updatedList)

                                                    currentScreen = "Main"
                                                } catch (e: Exception) {
                                                    currentScreen = "Main"
                                                }
                                            }
                                        }
                                    }
                                )
                            }
                            "RunningScreen" -> {
                                val (target, initialProgress) = remember(activeListIndex, activeActivityIndex, toDoLists) {
                                    try {
                                        val list = toDoLists[activeListIndex!!]
                                        val activity = list.activitiesJson.split(";")[activeActivityIndex!!]
                                        val parts = activity.split(",")
                                        parts[1].toFloat() to (if (parts.size >= 4) parts[3].toFloat() else 0f)
                                    } catch (e: Exception) { 5.0f to 0.0f }
                                }
                                RunningScreen(
                                    modifier = Modifier.padding(innerPadding),
                                    initialCount = initialProgress,
                                    targetObjective = target,
                                    onContinueClick = { finalDistance ->
                                        if (activeListIndex != null && activeActivityIndex != null) {
                                            try {
                                                if (activeListIndex!! < toDoLists.size) {
                                                    val list = toDoLists[activeListIndex!!]
                                                    val activities = list.activitiesJson.split(";").toMutableList()
                                                    if (activeActivityIndex!! < activities.size) {
                                                        val parts = activities[activeActivityIndex!!].split(",").toMutableList()
                                                        while (parts.size < 4) parts.add("0")
                                                        parts[3] = finalDistance.toString()
                                                        if (finalDistance >= target) {
                                                            parts[2] = "true"
                                                            coroutineScope.launch { context.markExerciseDone() }
                                                        }
                                                        activities[activeActivityIndex!!] = parts.joinToString(",")
                                                        taskViewModel.insertToDoList(list.copy(activitiesJson = activities.joinToString(";")))
                                                        
                                                        if (finalDistance >= target) triggerAd("AFTER_RUNNING", "Main")
                                                        else currentScreen = "Main"
                                                    }
                                                }
                                            } catch (e: Exception) { currentScreen = "Main" }
                                        } else currentScreen = "Main"
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
