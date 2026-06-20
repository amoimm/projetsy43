package com.example.application

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.example.application.ui.PartnerDashboardScreen
import com.example.application.ui.bdd.Ad
import com.example.application.ui.theme.ApplicationTheme
import org.junit.Assert.assertTrue
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
        composeTestRule.onNodeWithText("LOGOUT").assertIsDisplayed()
    }

    @Test
    fun partnerDashboard_displaysCorrectOverallMetrics() {
        val testAds = listOf(
            Ad(id = 1, partnerId = 1, title = "Promo Summer", content = "Get 20% off", triggerLocation = "AFTER_LIST")
        )
        
        composeTestRule.setContent {
            ApplicationTheme {
                PartnerDashboardScreen(
                    ads = testAds,
                    totalImpressions = 1500,
                    totalUniqueUsers = 450,
                    adSpecificImpressions = 0,
                    adSpecificUniqueUsers = 0,
                    onPeriodChange = {},
                    onAdSelectionChange = {}
                )
            }
        }

        // Verify overall metrics are displayed accurately
        composeTestRule.onNodeWithText("1500").assertIsDisplayed()
        composeTestRule.onNodeWithText("450").assertIsDisplayed()
        composeTestRule.onNodeWithText("Total views for all your ads").assertIsDisplayed()
        composeTestRule.onNodeWithText("Total distinct users reached").assertIsDisplayed()
    }

    @Test
    fun partnerDashboard_periodSelector_updatesCorrectly() {
        val testAds = listOf(
            Ad(id = 1, partnerId = 1, title = "Promo Summer", content = "Get 20% off", triggerLocation = "AFTER_LIST")
        )
        var lastCapturedStartTime: Long = 0

        composeTestRule.setContent {
            ApplicationTheme {
                PartnerDashboardScreen(
                    ads = testAds,
                    totalImpressions = 100,
                    totalUniqueUsers = 50,
                    adSpecificImpressions = 0,
                    adSpecificUniqueUsers = 0,
                    onPeriodChange = { lastCapturedStartTime = it },
                    onAdSelectionChange = {}
                )
            }
        }

        // Open period selector
        composeTestRule.onNodeWithText("Month").performClick()
        
        // Select "Day"
        composeTestRule.onNodeWithText("Day").performClick()
        
        // Verify the UI updated the selected text
        composeTestRule.onNodeWithText("Day").assertIsDisplayed()

        // Verify that the callback was triggered
        assertTrue(lastCapturedStartTime > 0)
        
        // Verify we can see other options by clicking again
        composeTestRule.onNodeWithText("Day").performClick()
        composeTestRule.onNodeWithText("Week").assertIsDisplayed()
        composeTestRule.onNodeWithText("Year").assertIsDisplayed()
        
        // Ensure no layout break (basic check: top bar and buttons still visible)
        composeTestRule.onNodeWithText("Partner Dashboard").assertIsDisplayed()
        composeTestRule.onNodeWithText("MANAGE MY ADS").assertIsDisplayed()
    }

    @Test
    fun partnerDashboard_adSelector_displaysSpecificMetrics() {
        val ad1 = Ad(id = 1, partnerId = 1, title = "Campaign A", content = "Content A", triggerLocation = "AFTER_LIST")
        val ad2 = Ad(id = 2, partnerId = 1, title = "Campaign B", content = "Content B", triggerLocation = "AFTER_PUSHUP")
        val testAds = listOf(ad1, ad2)
        
        var selectedAdId = -1

        composeTestRule.setContent {
            ApplicationTheme {
                PartnerDashboardScreen(
                    ads = testAds,
                    totalImpressions = 2000,
                    totalUniqueUsers = 1000,
                    adSpecificImpressions = 750,
                    adSpecificUniqueUsers = 300,
                    onPeriodChange = {},
                    onAdSelectionChange = { selectedAdId = it }
                )
            }
        }

        // Initial state: All Ads
        composeTestRule.onNodeWithText("All Ads").assertIsDisplayed()
        composeTestRule.onNodeWithText("2000").assertIsDisplayed()

        // Open Ad selector
        composeTestRule.onNodeWithText("All Ads").performClick()
        
        // Select "Campaign A"
        composeTestRule.onNodeWithText("Campaign A").performClick()
        
        // Verify UI shows the specific ad name
        composeTestRule.onNodeWithText("Campaign A").assertIsDisplayed()
        
        // Verify it switched to displaying specific metrics (750 and 300 instead of 2000 and 1000)
        composeTestRule.onNodeWithText("750").assertIsDisplayed()
        composeTestRule.onNodeWithText("300").assertIsDisplayed()
        composeTestRule.onNodeWithText("Views for this specific ad").assertIsDisplayed()
        
        // Verify the callback was triggered with the correct ID
        assert(selectedAdId == 1)
    }
}
