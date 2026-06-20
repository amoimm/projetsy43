package com.example.application

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.example.application.ui.PartnerDashboardScreen
import com.example.application.ui.theme.ApplicationTheme
import org.junit.Rule
import org.junit.Test

class PartnerDashboardUiTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun partnerDashboard_showsNoAdsMessage_whenListIsEmpty() {
        composeTestRule.setContent {
            ApplicationTheme {
                PartnerDashboardScreen(
                    ads = emptyList(),
                    totalImpressions = 0,
                    totalUniqueUsers = 0,
                    adSpecificImpressions = 0,
                    adSpecificUniqueUsers = 0,
                    onPeriodChange = {},
                    onAdSelectionChange = {}
                )
            }
        }

        // Verify the specific message for partners with no ads
        composeTestRule.onNodeWithText("You haven't added any ads yet, so there are no stats to view ☝\uFE0F\uD83E\uDD13").assertIsDisplayed()
        
        // Buttons should still be there
        composeTestRule.onNodeWithText("MANAGE MY ADS").assertIsDisplayed()
        composeTestRule.onNodeWithText("RETURN TO MENU").assertIsDisplayed()
    }
}
