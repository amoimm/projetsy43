package com.example.application

import android.Manifest
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.application.ui.CapaciteScreen
import com.example.application.ui.PersonalInfoScreen
import com.example.application.ui.HowYouFeelScreen
import com.example.application.ui.BuildToDoListScreen
import com.example.application.ui.MainScreen
import com.example.application.ui.PushupScreen
import com.example.application.ui.RunningScreen
import com.example.application.ui.WelcomeScreen
import com.example.application.ui.ModeSelectionScreen
import com.example.application.ui.PartnerInfoScreen
import com.example.application.ui.PartnerDashboardScreen
import com.example.application.ui.theme.ApplicationTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import com.example.application.ui.bdd.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.autofill.ContentDataType
import kotlin.text.format
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

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
        
        enableEdgeToEdge()
        setContent {
            val context = LocalContext.current
            val coroutineScope = rememberCoroutineScope()
            
            // Notification Permission Handling
            val launcher = rememberLauncherForActivityResult(
                ActivityResultContracts.RequestPermission()
            ) { isGranted ->
                if (isGranted) {
                    NotificationScheduler.scheduleDailyReminder(context)
                }
            }

            LaunchedEffect(Unit) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    if (ContextCompat.checkSelfPermission(
                            context,
                            Manifest.permission.POST_NOTIFICATIONS
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
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

                // Setting the startup screen with animation
                LaunchedEffect(userProfileData) {
                    if (userProfileData != null && currentScreen == "loading") {
                        currentScreen = "welcome"
                    }
                }

                val toDoLists by taskViewModel.allToDoLists.observeAsState(initial = emptyList())
                var activeListIndex by remember { mutableStateOf<Int?>(null) }
                var activeActivityIndex by remember { mutableStateOf<Int?>(null) }

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    AnimatedContent(
                        targetState = currentScreen,
                        transitionSpec = {
                            fadeIn(animationSpec = tween(750))
                                .togetherWith(fadeOut(animationSpec = tween(750)))
                        },
                        label = "screen_transition"
                    ) { targetScreen ->
                        when (targetScreen) {
                            "loading" -> { Box(modifier = Modifier
                                .fillMaxSize()
                                .background(Color.Black)) }
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
                            "welcome" -> WelcomeScreen(
                                modifier = Modifier.padding(innerPadding),
                                onContinueClick = { currentScreen = "mode_selection" }
                            )
                            "mode_selection" -> ModeSelectionScreen(
                                modifier = Modifier.padding(innerPadding),
                                onUserModeSelected = {
                                    if (userProfileData?.hasCompletedOnboarding == true) {
                                        val today = SimpleDateFormat(
                                            "dd/MM/yyyy",
                                            Locale.getDefault()).format(Date()
                                            )

                                        if (userProfileData?.lastMotivationDate == today) {
                                            currentScreen = "Main"
                                        } else {
                                            currentScreen = "HowYouFeel"
                                        }
                                    } else {
                                        currentScreen = "personal_info"
                                    }
                                },
                                onPartnerModeSelected = { currentScreen = "partner_info" }
                            )
                            "partner_info" -> PartnerInfoScreen(
                                modifier = Modifier.padding(innerPadding),
                                onValidateClick = { _, _, _ ->
                                    currentScreen = "partner_dashboard"
                                }
                            )
                            "partner_dashboard" -> PartnerDashboardScreen(
                                modifier = Modifier.padding(innerPadding),
                                onBackClick = { currentScreen = "welcome" }
                            )
                            "personal_info" -> PersonalInfoScreen(
                                modifier = Modifier.padding(innerPadding),
                                initialName = userProfileData?.name ?: "",
                                initialAge = userProfileData?.age ?: "",
                                initialWeight = userProfileData?.weight ?: "",
                                initialHeight = userProfileData?.height ?: "",
                                onValidateClick = { profil ->
                                    coroutineScope.launch {
                                        saveUserProfile(profil)
                                    }
                                    currentScreen = "capacite_info"
                                }
                            )
                            "capacite_info" -> CapaciteScreen(
                                modifier = Modifier.padding(innerPadding),
                                onValidateClick = { p, r ->
                                    coroutineScope.launch {
                                        val current = userProfileData ?: UserProfile()
                                        saveUserProfile(current.copy(maxPushups = p, maxRunningKm = r))
                                    }
                                    currentScreen = "HowYouFeel"
                                }
                            )
                            "HowYouFeel" -> HowYouFeelScreen(
                                modifier = Modifier.padding(innerPadding),
                                userProfile = userProfileData,
                                onValidateClick = { generatedList ->
                                    coroutineScope.launch {
                                        context.setCompletedOnboarding()
                                        context.updateMotivationDate()
                                        taskViewModel.insertToDoList(generatedList)
                                    }
                                    currentScreen="Main" 
                                }
                            )
                            "Build_ToDo_List" -> BuildToDoListScreen(
                                onBackClick = { currentScreen = "Main" },
                                onValidateClick = { newList ->
                                    taskViewModel.insertToDoList(newList)
                                    currentScreen = "Main"
                                }
                            )
                            "Main" -> MainScreen(
                                modifier = Modifier.padding(innerPadding),
                                toDoLists = toDoLists,
                                onValidateClick = { location ->
                                    if (location.contains("|")) {
                                        val parts = location.split("|")
                                        currentScreen = parts[0]
                                        activeListIndex = parts[1].toInt()
                                        activeActivityIndex = parts[2].toInt()
                                    } else {
                                        currentScreen = location
                                    }
                                },
                                onDeleteClick = { list ->
                                    taskViewModel.deleteToDoList(list)
                                },
                                onShareClick = { list ->
                                    val activitiesText = list.activities.joinToString("\n") {
                                        val unit = if (it.categorie == "Running") "km" else "reps"
                                        "- ${it.categorie}: ${it.valeur} $unit (Completed!)"
                                    }
                                    val shareMessage = """
                                        🔥 I just finished my workout: ${list.title}! 🔥
                                        📅 Date: ${list.date}
                                        
                                        My activities:
                                        $activitiesText
                                        
                                        Done with my favotire fitness app 🏋️
                                        Link: "One day maybe !"
                                    """.trimIndent()

                                    val intent = android.content.Intent(android.content.Intent.ACTION_SEND).apply {
                                        type = "text/plain"
                                        putExtra(android.content.Intent.EXTRA_TEXT, shareMessage)
                                    }
                                    context.startActivity(android.content.Intent.createChooser(intent, "Share your progress"))
                                }
                            )
                            "PushupScreen" -> {
                                val (target, initialProgress) = remember(activeListIndex, activeActivityIndex, toDoLists) {
                                    try {
                                        val list = toDoLists[activeListIndex!!]
                                        val activity = list.activitiesJson.split(";")[activeActivityIndex!!]
                                        val parts = activity.split(",")
                                        parts[1].toInt() to (if (parts.size >= 4) parts[3].toInt() else 0)
                                    } catch (e: Exception) { 10 to 0 }
                                }
                                PushupScreen(
                                    modifier = Modifier.padding(innerPadding),
                                    initialCount = initialProgress,
                                    targetObjective = target,
                                    onContinueClick = { finalCount ->
                                        if (activeListIndex != null && activeActivityIndex != null) {
                                            // Mise à jour sécurisée de la base de données
                                            try {
                                                if (activeListIndex!! < toDoLists.size) {
                                                    val list = toDoLists[activeListIndex!!]
                                                    val activities = list.activitiesJson.split(";").toMutableList()
                                                    if (activeActivityIndex!! < activities.size) {
                                                        val parts = activities[activeActivityIndex!!].split(",").toMutableList()
                                                        
                                                        // Update progress
                                                        while (parts.size < 4) parts.add("0")
                                                        parts[3] = finalCount.toString()
                                                        
                                                        // Update isDone if objective reached
                                                        if (finalCount >= target) {
                                                            parts[2] = "true"
                                                            coroutineScope.launch { context.markExerciseDone() }
                                                        }
                                                        
                                                        activities[activeActivityIndex!!] = parts.joinToString(",")
                                                        taskViewModel.insertToDoList(list.copy(activitiesJson = activities.joinToString(";")))
                                                    }
                                                }
                                            } catch (e: Exception) {
                                                e.printStackTrace()
                                            }
                                        }
                                        currentScreen = "Main" 
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
                                            // Mise à jour sécurisée de la base de données
                                            try {
                                                if (activeListIndex!! < toDoLists.size) {
                                                    val list = toDoLists[activeListIndex!!]
                                                    val activities = list.activitiesJson.split(";").toMutableList()
                                                    if (activeActivityIndex!! < activities.size) {
                                                        val parts = activities[activeActivityIndex!!].split(",").toMutableList()
                                                        
                                                        // Update progress
                                                        while (parts.size < 4) parts.add("0")
                                                        parts[3] = finalDistance.toString()
                                                        
                                                        // Update isDone if objective reached
                                                        if (finalDistance >= target) {
                                                            parts[2] = "true"
                                                            coroutineScope.launch { context.markExerciseDone() }
                                                        }
                                                        
                                                        activities[activeActivityIndex!!] = parts.joinToString(",")
                                                        taskViewModel.insertToDoList(list.copy(activitiesJson = activities.joinToString(";")))
                                                    }
                                                }
                                            } catch (e: Exception) {
                                                e.printStackTrace()
                                            }
                                        }
                                        currentScreen = "Main" 
                                    }
                                )
                            }
                            else -> {
                                MainScreen(
                                    modifier = Modifier.padding(innerPadding),
                                    toDoLists = toDoLists,
                                    onValidateClick = { location -> currentScreen = location }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
