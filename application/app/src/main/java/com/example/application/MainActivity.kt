package com.example.application

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.application.ui.CapaciteScreen
import com.example.application.ui.PersonalInfoScreen
import com.example.application.ui.HowYouFeelScreen
import com.example.application.ui.BuildToDoListScreen
import com.example.application.ui.MainScreen
import com.example.application.ui.WelcomeScreen
import com.example.application.ui.theme.ApplicationTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        enableEdgeToEdge()
        setContent {
            ApplicationTheme {
                var currentScreen by remember { mutableStateOf("welcome") }
                val coroutineScope = rememberCoroutineScope()
                
                // On écoute le flux de données (Pas besoin d'import car dans le même package)
                val userProfile by userProfileFlow.collectAsState(initial = UserProfile())

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    when (currentScreen) {
                        "welcome" -> WelcomeScreen(
                            modifier = Modifier.padding(innerPadding),
                            onContinueClick = { currentScreen = "personal_info" }
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
                            modifier = Modifier.padding(innerPadding),
                            onValidateClick = { currentScreen = "Main" }
                        )
                        "Main" -> MainScreen(
                            modifier = Modifier.padding(innerPadding),
                            onValidateClick = { currentScreen="Build_ToDo_List" }
                        )
                    }
                }
            }
        }
    }
}
