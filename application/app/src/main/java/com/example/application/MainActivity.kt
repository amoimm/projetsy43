package com.example.application

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.application.ui.CapaciteScreen
import com.example.application.ui.PersonalInfoScreen
//import com.example.application.ui.BuildToDoListScreen
import com.example.application.ui.HowYouFeelScreen
import com.example.application.ui.BuildToDoListScreen
import com.example.application.ui.CapaciteScreen
import com.example.application.ui.HowYouFeelScreen
import com.example.application.ui.PersonalInfoScreen
import com.example.application.ui.WelcomeScreen
import com.example.application.ui.theme.ApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ApplicationTheme {
                var currentScreen by remember { mutableStateOf("welcome") }

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    when (currentScreen) {
                        "welcome" -> WelcomeScreen(
                            modifier = Modifier.padding(innerPadding),
                            onContinueClick = { currentScreen = "personal_info" }
                        )
                        "personal_info" -> PersonalInfoScreen(
                            modifier = Modifier.padding(innerPadding),
                            onValidateClick = { currentScreen = "capacite_info" }
                        )
                        "capacite_info" -> CapaciteScreen(
                            modifier = Modifier.padding(innerPadding),
                            onValidateClick = { currentScreen="Build_ToDo_List" }
                        )
                        "HowYouFeel" -> HowYouFeelScreen(
                            modifier = Modifier.padding(innerPadding),
                            onValidateClick = { currentScreen="Build_ToDo_List" }
                        )
                        "Build_ToDo_List" -> BuildToDoListScreen(
                            modifier = Modifier.padding(innerPadding),
                            onValidateClick = { currentScreen = "HowYouFeel" }
                        )


                    }
                }
            }
        }
    }
}
