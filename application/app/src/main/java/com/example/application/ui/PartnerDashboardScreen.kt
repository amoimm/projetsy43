package com.example.application.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.application.ui.bdd.Ad
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PartnerDashboardScreen(
    ads: List<Ad>,
    totalImpressions: Int,
    totalUniqueUsers: Int,
    adSpecificImpressions: Int,
    adSpecificUniqueUsers: Int,
    onPeriodChange: (Long) -> Unit,
    onAdSelectionChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit = {},
    onMyAdsClick: () -> Unit = {},
    onSettingsClick: () -> Unit = {}
) {
    val periods = listOf("Day", "Week", "Month", "Year")
    var periodExpanded by remember { mutableStateOf(false) }
    var selectedPeriod by remember { mutableStateOf(periods[2]) }

    var adExpanded by remember { mutableStateOf(false) }
    var selectedAd by remember { mutableStateOf<Ad?>(null) }

    // Update start time based on period
    LaunchedEffect(selectedPeriod) {
        val cal = Calendar.getInstance()
        val startTime = when(selectedPeriod) {
            "Day" -> cal.apply { set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0); set(Calendar.SECOND, 0) }.timeInMillis
            "Week" -> cal.apply { add(Calendar.WEEK_OF_YEAR, -1) }.timeInMillis
            "Month" -> cal.apply { add(Calendar.MONTH, -1) }.timeInMillis
            else -> cal.apply { add(Calendar.YEAR, -1) }.timeInMillis
        }
        onPeriodChange(startTime)
    }

    // Trigger ad specific query when ad or period changes
    LaunchedEffect(selectedAd, selectedPeriod) {
        selectedAd?.let { onAdSelectionChange(it.id) }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(horizontal = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(top = 32.dp, bottom = 140.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Partner Dashboard",
                    color = Color.White,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold
                )
                IconButton(onClick = onSettingsClick) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Edit Profile",
                        tint = Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
            
            Text(
                text = "Track your advertising performance",
                color = Color.Gray,
                fontSize = 16.sp,
                modifier = Modifier.padding(bottom = 32.dp)
            )

            if (ads.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "You haven't added any ads yet, so there are no stats to view ☝\uFE0F\uD83E\uDD13",
                        color = Color.Gray,
                        textAlign = TextAlign.Center,
                        fontSize = 16.sp
                    )
                }
            } else {
                // Period Selector
                DashboardDropdown(
                    label = "Time Period",
                    selectedText = selectedPeriod,
                    expanded = periodExpanded,
                    onExpandedChange = { periodExpanded = it },
                    items = periods,
                    onItemClick = { 
                        selectedPeriod = it
                        periodExpanded = false
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Ad Selector
                DashboardDropdown(
                    label = "Filter by Ad",
                    selectedText = selectedAd?.title ?: "All Ads",
                    expanded = adExpanded,
                    onExpandedChange = { adExpanded = it },
                    items = listOf("All Ads") + ads.map { it.title },
                    onItemClick = { title ->
                        selectedAd = if (title == "All Ads") null else ads.find { it.title == title }
                        adExpanded = false
                    }
                )

                Spacer(modifier = Modifier.height(32.dp))

                val dispImpressions = if (selectedAd == null) totalImpressions else adSpecificImpressions
                val dispUsers = if (selectedAd == null) totalUniqueUsers else adSpecificUniqueUsers

                StatCard(
                    title = "Ad Impressions",
                    value = dispImpressions.toString(),
                    description = if (selectedAd == null) "Total views for all your ads" else "Views for this specific ad",
                    accentColor = Color(0xFF1565C0)
                )

                Spacer(modifier = Modifier.height(16.dp))

                StatCard(
                    title = "Unique Users Reached",
                    value = dispUsers.toString(),
                    description = if (selectedAd == null) "Total distinct users reached" else "Distinct users who saw this ad",
                    accentColor = Color(0xFF4CAF50)
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .background(Color.Black)
                .padding(bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                onClick = onMyAdsClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF1565C0),
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Icon(imageVector = Icons.Default.List, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "MANAGE MY ADS", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }

            Button(
                onClick = onBackClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                    contentColor = Color.Black
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Text(text = "RETURN TO MENU", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardDropdown(
    label: String,
    selectedText: String,
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    items: List<String>,
    onItemClick: (String) -> Unit
) {
    Column {
        Text(text = label, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(bottom = 8.dp))
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = onExpandedChange,
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedTextField(
                value = selectedText,
                onValueChange = {},
                readOnly = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier.menuAnchor().fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedBorderColor = Color(0xFF1565C0),
                    unfocusedBorderColor = Color.Gray,
                    focusedContainerColor = Color(0xFF1E1E1E),
                    unfocusedContainerColor = Color(0xFF1E1E1E)
                ),
                shape = RoundedCornerShape(12.dp)
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { onExpandedChange(false) },
                modifier = Modifier.background(Color(0xFF1E1E1E))
            ) {
                items.forEach { item ->
                    DropdownMenuItem(
                        text = { Text(text = item, color = Color.White) },
                        onClick = { onItemClick(item) }
                    )
                }
            }
        }
    }
}

@Composable
fun StatCard(
    title: String,
    value: String,
    description: String,
    accentColor: Color
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E))
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = title,
                color = Color.Gray,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
            
            Text(
                text = value,
                color = Color.White,
                fontSize = 40.sp,
                fontWeight = FontWeight.Black,
                modifier = Modifier.padding(vertical = 4.dp)
            )
            
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(8.dp).background(accentColor, CircleShape))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = description,
                    color = Color.Gray,
                    fontSize = 12.sp
                )
            }
        }
    }
}
