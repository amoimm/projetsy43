package com.example.application

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.example.application.ui.MainScreen
import com.example.application.ui.bdd.ToDoList
import com.example.application.ui.theme.ApplicationTheme
import org.junit.Rule
import org.junit.Test

class MainScreenUiTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun mainScreen_showsEmptyState_whenNoLists() {
        composeTestRule.setContent {
            ApplicationTheme {
                MainScreen(
                    toDoLists = emptyList()
                )
            }
        }

        // Verify the empty state message
        composeTestRule.onNodeWithText("No lists created yet").assertIsDisplayed()
        
        // Verify the "Add new list" button is present
        composeTestRule.onNodeWithText("Add new list").assertIsDisplayed()
    }

    @Test
    fun mainScreen_showsLists_whenProvided() {
        val testLists = listOf(
            ToDoList(id = 1, title = "Morning Workout", date = "10/10/2024", activitiesJson = "Pushup,20,false,0", frequency = "DAILY")
        )

        composeTestRule.setContent {
            ApplicationTheme {
                MainScreen(
                    toDoLists = testLists
                )
            }
        }

        // Verify the list title is displayed
        composeTestRule.onNodeWithText("Morning Workout").assertIsDisplayed()
        
        // Use onAllNodes because "DAILY" appears in both the header and the card badge
        composeTestRule.onAllNodesWithText("DAILY").onFirst().assertIsDisplayed()
    }
    
    @Test
    fun mainScreen_expandCard_showsActivities() {
        val testLists = listOf(
            ToDoList(id = 1, title = "Gym", date = "10/10/2024", activitiesJson = "Pushup,50,false,0", frequency = "ONCE")
        )

        composeTestRule.setContent {
            ApplicationTheme {
                MainScreen(
                    toDoLists = testLists
                )
            }
        }

        // Click "Show more"
        composeTestRule.onNodeWithText("Show more").performClick()

        // Verify activity details appear (using ignoreCase = true to be flexible)
        composeTestRule.onNodeWithText("Pushup", ignoreCase = true).assertIsDisplayed()
        composeTestRule.onNodeWithText("Progress: 0 / 50 reps", substring = true).assertIsDisplayed()
    }
}
