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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.privacysandbox.tools.core.generator.build
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
import kotlinx.coroutines.launch
import com.example.application.ui.ToDoList
import com.example.application.ui.ActiviteSportive
//bdd
import com.example.application.ui.bdd.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.room.Room

class MainActivity : ComponentActivity() {

    private val taskViewModel: TaskViewModel by viewModels {
        val database = Room.databaseBuilder(applicationContext, TaskDatabase::class.java, "task_db").build()
        val repository = TaskRepository(database.taskDao())
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
                var currentScreen by remember { mutableStateOf("welcome") }
                var toDoLists by remember { mutableStateOf(listOf<ToDoList>()) }
                
                // On garde en mémoire quelle activité est en cours pour pouvoir la marquer comme faite
                var activityList by remember { mutableStateOf(listOf<ActiviteSportive>()) }
                val toDoLists by taskViewModel.allToDoLists.observeAsState(initial = emptyList())
                var activeListIndex by remember { mutableStateOf<Int?>(null) }
                var activeActivityIndex by remember { mutableStateOf<Int?>(null) }

                val userProfile by userProfileFlow.collectAsState(initial = UserProfile())

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    when (currentScreen) {
                        "welcome" -> WelcomeScreen(
                            modifier = Modifier.padding(innerPadding),
                            onContinueClick = { currentScreen = "mode_selection" }
                        )
                        "mode_selection" -> ModeSelectionScreen(
                            modifier = Modifier.padding(innerPadding),
                            onUserModeSelected = { currentScreen = "personal_info" },
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
                            initialName = userProfile.name,
                            initialAge = userProfile.age,
                            initialWeight = userProfile.weight,
                            initialHeight = userProfile.height,
                            onValidateClick = { profil ->
                                coroutineScope.launch {
                                    saveUserProfile(profil)
                                }
                                currentScreen = "capacite_info"
                            }
                        )
                        "capacite_info" -> CapaciteScreen(
                            modifier = Modifier.padding(innerPadding),
                            onValidateClick = { currentScreen="HowYouFeel" }
                        )
                        "HowYouFeel" -> HowYouFeelScreen(
                            modifier = Modifier.padding(innerPadding),
                            onValidateClick = { currentScreen="Main" }
                        )
                        "Build_ToDo_List" -> BuildToDoListScreen(
                            onValidateClick = { newList ->
                                toDoLists = toDoLists + newList.copy(id = toDoLists.size)
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
                            }
                        )
                        "Push upScreen" -> PushupScreen(
                            modifier = Modifier.padding(innerPadding),
                            onContinueClick = {
                                currentScreen = "Main" 
                            }
                        )

                        "RunningScreen" -> RunningScreen(
                            modifier = Modifier.padding(innerPadding),
                            onContinueClick = {
                                currentScreen = "Main" 
                            }
                        )
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
