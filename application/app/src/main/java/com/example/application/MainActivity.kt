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
import kotlinx.coroutines.flow.first
import org.osmdroid.config.Configuration
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
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
                val currentUser by taskViewModel.currentUser.collectAsState()
                val currentPartner by taskViewModel.currentPartner.collectAsState()
                val isSessionRestored by taskViewModel.isSessionRestored.collectAsState()
                var currentScreen by remember { mutableStateOf("loading") }
                var authError by remember { mutableStateOf<String?>(null) }

                // Session restoration logic
                LaunchedEffect(Unit) {
                    val profile = context.userProfileFlow.first()
                    if (profile.loggedInUserId != -1 || profile.loggedInPartnerId != -1) {
                        taskViewModel.restoreSession(profile.loggedInUserId, profile.loggedInPartnerId)
                    } else {
                        delay(500)
                        if (currentScreen == "loading") {
                            currentScreen = "welcome"
                        }
                    }
                }

                LaunchedEffect(isSessionRestored) {
                    if (isSessionRestored) {
                        currentScreen = "welcome_back"
                        delay(2000)
                        if (currentUser != null) {
                            val today = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
                            if (currentUser?.lastMotivationDate != today) {
                                currentScreen = "HowYouFeel"
                            } else {
                                currentScreen = "Main"
                            }
                        } else if (currentPartner != null) {
                            currentScreen = "partner_dashboard"
                        }
                    }
                }

                val toDoLists by (currentUser?.let { taskViewModel.getAllToDoListsForUser(it.id) } ?: taskViewModel.getAllToDoListsForUser(-1)).observeAsState(initial = emptyList())
                val communityToDoLists by (currentUser?.let { taskViewModel.getAllCommunityToDoLists(it.id) } ?: taskViewModel.getAllCommunityToDoLists(-1)).observeAsState(initial = emptyList())
                val ads by (currentPartner?.let { taskViewModel.getAdsForPartner(it.id) } ?: taskViewModel.allAds).observeAsState(initial = emptyList())
                val allAdsForUsers by taskViewModel.allAds.observeAsState(initial = emptyList())
                var activeListIndex by remember { mutableStateOf<Int?>(null) }
                var activeActivityIndex by remember { mutableStateOf<Int?>(null) }

                // Partner Dashboard States
                var dashboardStartTime by remember { mutableLongStateOf(0L) }
                var dashboardAdId by remember { mutableIntStateOf(-1) }
                
                val totalImpressions by (currentPartner?.let { taskViewModel.getPartnerTotalImpressions(it.id, dashboardStartTime) } ?: taskViewModel.getTotalImpressions(dashboardStartTime)).collectAsState(initial = 0)
                val totalUniqueUsers by (currentPartner?.let { taskViewModel.getPartnerTotalUniqueUsers(it.id, dashboardStartTime) } ?: taskViewModel.getTotalUniqueUsers(dashboardStartTime)).collectAsState(initial = 0)
                val adImpressions by taskViewModel.getAdImpressions(dashboardAdId, dashboardStartTime).collectAsState(initial = 0)
                val adUniqueUsers by taskViewModel.getAdUniqueUsers(dashboardAdId, dashboardStartTime).collectAsState(initial = 0)

                // AD LOGIC
                var adToShow by remember { mutableStateOf<Ad?>(null) }
                var nextScreenAfterAd by remember { mutableStateOf("Main") }

                fun triggerAd(location: AdTriggerLocation, nextScreen: String) {
                    val possibleAds = allAdsForUsers.filter { it.triggerLocation.split(",").contains(location.name) }
                    if (possibleAds.isNotEmpty()) {
                        adToShow = possibleAds[Random.nextInt(possibleAds.size)]
                    } else {
                        // Fallback to res/raw/publicite.mp4 if no targeted ad found
                        adToShow = null
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
                                        text = "Welcome back ${currentUser?.name ?: ""}!",
                                        color = Color.White,
                                        fontSize = 32.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                            "welcome" -> WelcomeScreen(modifier = Modifier.padding(innerPadding), onContinueClick = { currentScreen = "mode_selection" })
                            "mode_selection" -> ModeSelectionScreen(
                                modifier = Modifier.padding(innerPadding),
                                onUserModeSelected = {
                                    if (currentUser != null) {
                                        val today = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
                                        if (currentUser?.lastMotivationDate != today) {
                                            currentScreen = "HowYouFeel"
                                        } else {
                                            currentScreen = "Main"
                                        }
                                    } else {
                                        currentScreen = "login_user"
                                    }
                                },
                                onPartnerModeSelected = { 
                                    if (currentPartner != null) currentScreen = "partner_dashboard"
                                    else currentScreen = "login_partner"
                                }
                            )
                            "login_user" -> LoginScreen(
                                isPartner = false,
                                onLoginClick = { u, p ->
                                    taskViewModel.loginUser(u, p, context) { success ->
                                        if (success != null) {
                                            authError = null
                                            currentScreen = "welcome_back"
                                            coroutineScope.launch {
                                                delay(2000)
                                                val today = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
                                                if (success.lastMotivationDate != today) {
                                                    currentScreen = "HowYouFeel"
                                                } else {
                                                    currentScreen = "Main"
                                                }
                                            }
                                        } else {
                                            authError = "Invalid credentials"
                                        }
                                    }
                                },
                                onRegisterClick = { currentScreen = "personal_info" },
                                onBackClick = { currentScreen = "mode_selection" },
                                errorMessage = authError
                            )
                            "login_partner" -> LoginScreen(
                                isPartner = true,
                                onLoginClick = { u, p ->
                                    taskViewModel.loginPartner(u, p, context) { success ->
                                        if (success != null) {
                                            authError = null
                                            currentScreen = "partner_dashboard"
                                        } else {
                                            authError = "Invalid credentials"
                                        }
                                    }
                                },
                                onRegisterClick = { currentScreen = "partner_info" },
                                onBackClick = { currentScreen = "mode_selection" },
                                errorMessage = authError
                            )
                            "personal_info" -> PersonalInfoScreen(
                                modifier = Modifier.padding(innerPadding),
                                initialUsername = currentUser?.username ?: "",
                                initialMdp = currentUser?.mdp ?: "",
                                initialName = currentUser?.name ?: "",
                                initialAge = currentUser?.age ?: "",
                                initialWeight = currentUser?.weight ?: "",
                                initialHeight = currentUser?.height ?: "",
                                onBackClick = { 
                                    if (currentUser != null) currentScreen = "settings_choice"
                                    else currentScreen = "login_user"
                                },
                                onValidateClick = { u, p, n, a, w, h ->
                                    val newUser = User(
                                        id = currentUser?.id ?: 0,
                                        username = u,
                                        mdp = p,
                                        name = n,
                                        age = a,
                                        weight = w,
                                        height = h,
                                        maxPushups = currentUser?.maxPushups ?: "0",
                                        maxPullups = currentUser?.maxPullups ?: "0",
                                        maxSquats = currentUser?.maxSquats ?: "0",
                                        maxRunningKm = currentUser?.maxRunningKm ?: "0.0",
                                        lastMotivationDate = currentUser?.lastMotivationDate ?: "",
                                        lastMotivationLevel = currentUser?.lastMotivationLevel ?: 0.5f
                                    )
                                    if (currentUser == null) {
                                        taskViewModel.registerUser(newUser, context) {
                                            currentScreen = "capacite"
                                        }
                                    } else {
                                        taskViewModel.updateCurrentUser(newUser)
                                        currentScreen = "settings_choice"
                                    }
                                }
                            )
                            "partner_info" -> PartnerInfoScreen(
                                modifier = Modifier.padding(innerPadding),
                                initialUsername = currentPartner?.username ?: "",
                                initialMdp = currentPartner?.mdp ?: "",
                                initialLastName = currentPartner?.lastName ?: "",
                                initialFirstName = currentPartner?.firstName ?: "",
                                initialCompany = currentPartner?.company ?: "",
                                onBackClick = { 
                                    if (currentPartner != null) currentScreen = "settings_choice"
                                    else currentScreen = "login_partner"
                                },
                                onValidateClick = { u, p, ln, fn, co ->
                                    val newPartner = Partner(
                                        id = currentPartner?.id ?: 0,
                                        username = u,
                                        mdp = p,
                                        lastName = ln,
                                        firstName = fn,
                                        company = co
                                    )
                                    if (currentPartner == null) {
                                        taskViewModel.registerPartner(newPartner, context) {
                                            currentScreen = "partner_dashboard"
                                        }
                                    } else {
                                        taskViewModel.updateCurrentPartner(newPartner)
                                        currentScreen = "partner_dashboard"
                                    }
                                }
                            )
                            "capacite" -> CapaciteScreen(
                                modifier = Modifier.padding(innerPadding),
                                initialPushups = currentUser?.maxPushups ?: "0",
                                initialPullups = currentUser?.maxPullups ?: "0",
                                initialSquats = currentUser?.maxSquats ?: "0",
                                initialRunningKm = currentUser?.maxRunningKm ?: "0",
                                onBackClick = { 
                                    if (currentUser != null) currentScreen = "settings_choice"
                                    else currentScreen = "personal_info" 
                                },
                                onValidateClick = { pushups, pullups, squats, runKm ->
                                    currentUser?.let { user ->
                                        val updatedUser = user.copy(
                                            maxPushups = pushups,
                                            maxPullups = pullups,
                                            maxSquats = squats,
                                            maxRunningKm = runKm
                                        )
                                        taskViewModel.updateCurrentUser(updatedUser)
                                        currentScreen = "HowYouFeel"
                                    }
                                }
                            )
                            "HowYouFeel" -> HowYouFeelScreen(
                                modifier = Modifier.padding(innerPadding),
                                user = currentUser,
                                onValidateClick = { generatedList, level ->
                                    currentUser?.let { user ->
                                        val today = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
                                        taskViewModel.updateCurrentUser(user.copy(
                                            lastMotivationDate = today,
                                            lastMotivationLevel = level
                                        ))
                                        taskViewModel.insertToDoList(generatedList)
                                    }
                                    currentScreen = "Main"
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
                                        triggerAd(AdTriggerLocation.AFTER_DELETE, "Main")
                                    },
                                    onSettingsClick = { currentScreen = "settings_choice" },
                                    onLogoutClick = {
                                        taskViewModel.logout(context)
                                        currentScreen = "mode_selection"
                                    },
                                    onCommunityClick = { currentScreen = "community_lists" }
                                )
                            }
                            "community_lists" -> {
                                CommunityToDoListsScreen(
                                    communityLists = communityToDoLists,
                                    getUsernameById = { id -> taskViewModel.getUsernameById(id) },
                                    onBackClick = { currentScreen = "Main" },
                                    onCopyClick = { original ->
                                        currentUser?.let { user ->
                                            val resetActivities = original.activities.map { it.copy(isDone = false, progress = "0") }
                                            val resetJson = resetActivities.joinToString(";") {
                                                "${it.categorie.name},${it.valeur},${it.isDone},${it.progress}"
                                            }
                                            val copiedList = original.copy(
                                                id = 0,
                                                userId = user.id,
                                                activitiesJson = resetJson
                                            )
                                            taskViewModel.insertToDoList(copiedList)
                                            currentScreen = "Main?created=true"
                                        }
                                    }
                                )
                            }
                            "PushupScreen", "PullupScreen", "SquatScreen" -> {
                                val listIdx = activeListIndex
                                val actIdx = activeActivityIndex
                                if (listIdx != null && actIdx != null && listIdx < toDoLists.size) {
                                    val list = toDoLists[listIdx]
                                    val activity = list.activities.getOrNull(actIdx)
                                    if (activity != null) {
                                        val sensorThreshold = when(activity.categorie) {
                                            ActivityCategory.SQUAT -> 0.45f
                                            ActivityCategory.PULLUP -> 0.50f
                                            else -> 0.25f
                                        }
                                        BodyweightScreen(
                                            modifier = Modifier.padding(innerPadding),
                                            initialCount = activity.progress.toIntOrNull() ?: 0,
                                            targetObjective = activity.valeur.toIntOrNull() ?: 20,
                                            exerciseType = activity.categorie.name,
                                            threshold = sensorThreshold,
                                            onContinueClick = { finalCount ->
                                                val updatedActivities = list.activities.toMutableList()
                                                updatedActivities[actIdx] = activity.copy(
                                                    progress = finalCount.toString(),
                                                    isDone = finalCount >= (activity.valeur.toIntOrNull() ?: 20)
                                                )
                                                val activitiesString = updatedActivities.joinToString(";") {
                                                    "${it.categorie.name},${it.valeur},${it.isDone},${it.progress}"
                                                }
                                                taskViewModel.insertToDoList(list.copy(activitiesJson = activitiesString))
                                                if (finalCount >= (activity.valeur.toIntOrNull() ?: 20)) {
                                                    triggerAd(AdTriggerLocation.AFTER_PUSHUP, "Main")
                                                } else {
                                                    currentScreen = "Main"
                                                }
                                            }
                                        )
                                    }
                                }
                            }
                            "RunningScreen" -> {
                                val listIdx = activeListIndex
                                val actIdx = activeActivityIndex
                                if (listIdx != null && actIdx != null && listIdx < toDoLists.size) {
                                    val list = toDoLists[listIdx]
                                    val activity = list.activities.getOrNull(actIdx)
                                    if (activity != null) {
                                        RunningScreen(
                                            modifier = Modifier.padding(innerPadding),
                                            initialCount = activity.progress.toFloatOrNull() ?: 0.0f,
                                            targetObjective = activity.valeur.toFloatOrNull() ?: 5.0f,
                                            onContinueClick = { finalDistance ->
                                                val updatedActivities = list.activities.toMutableList()
                                                updatedActivities[actIdx] = activity.copy(
                                                    progress = finalDistance.toString(),
                                                    isDone = finalDistance >= (activity.valeur.toFloatOrNull() ?: 5.0f)
                                                )
                                                val activitiesString = updatedActivities.joinToString(";") {
                                                    "${it.categorie.name},${it.valeur},${it.isDone},${it.progress}"
                                                }
                                                taskViewModel.insertToDoList(list.copy(activitiesJson = activitiesString))
                                                if (finalDistance >= (activity.valeur.toFloatOrNull() ?: 5.0f)) {
                                                    triggerAd(AdTriggerLocation.AFTER_RUNNING, "Main")
                                                } else {
                                                    currentScreen = "Main"
                                                }
                                            }
                                        )
                                    }
                                }
                            }
                            "Build_ToDo_List" -> BuildToDoListScreen(
                                userId = currentUser?.id ?: 0,
                                modifier = Modifier.padding(innerPadding),
                                onBackClick = { currentScreen = "Main" },
                                onSaveClick = { newList ->
                                    taskViewModel.insertToDoList(newList)
                                    triggerAd(AdTriggerLocation.AFTER_LIST, "Main?created=true")
                                }
                            )
                            "settings_choice" -> SettingsChoiceScreen(
                                modifier = Modifier.padding(innerPadding),
                                onBackClick = {
                                    if (currentUser != null) currentScreen = "Main"
                                    else currentScreen = "partner_dashboard"
                                },
                                onPersonalInfoClick = { currentScreen = "personal_info" },
                                onCapacityClick = { currentScreen = "capacite" },
                                onMotivationClick = { currentScreen = "HowYouFeel" }
                            )
                            "partner_dashboard" -> PartnerDashboardScreen(
                                modifier = Modifier.padding(innerPadding),
                                ads = ads,
                                totalImpressions = totalImpressions,
                                totalUniqueUsers = totalUniqueUsers,
                                adSpecificImpressions = adImpressions,
                                adSpecificUniqueUsers = adUniqueUsers,
                                onPeriodChange = { dashboardStartTime = it },
                                onAdSelectionChange = { dashboardAdId = it },
                                onBackClick = { 
                                    taskViewModel.logout(context)
                                    currentScreen = "mode_selection" 
                                },
                                onSettingsClick = { currentScreen = "partner_info" },
                                onMyAdsClick = { currentScreen = "my_ads" }
                            )
                            "my_ads" -> MyAdsScreen(
                                modifier = Modifier.padding(innerPadding),
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
                                partnerId = currentPartner?.id ?: 0,
                                onBackClick = { currentScreen = "my_ads" },
                                onSaveAdClick = { ad ->
                                    taskViewModel.insertAd(ad)
                                    currentScreen = "my_ads"
                                }
                            )
                            "show_ad" -> {
                                VideoAdScreen(
                                    modifier = Modifier.padding(innerPadding),
                                    ad = adToShow,
                                    onAdFinished = {
                                        adToShow?.let { ad ->
                                            taskViewModel.insertAdMetric(AdMetric(adId = ad.id, userId = currentUser?.id ?: 0, timestamp = System.currentTimeMillis()))
                                        }
                                        currentScreen = nextScreenAfterAd
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
