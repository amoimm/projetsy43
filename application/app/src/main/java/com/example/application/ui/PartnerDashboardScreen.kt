package com.example.application.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.application.ui.theme.ApplicationTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PartnerDashboardScreen(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit = {}
) {
    val periods = listOf("Day", "Week", "Month", "Year")
    var expanded by remember { mutableStateOf(false) }
    var selectedPeriod by remember { mutableStateOf(periods[2]) } // Default to Month

    // Fake data based on selected period for demonstration
    val views = when(selectedPeriod) {
        "Day" -> "1,240"
        "Week" -> "8,650"
        "Month" -> "34,200"
        else -> "412,000"
    }
    
    val reachedUsers = when(selectedPeriod) {
        "Day" -> "850"
        "Week" -> "5,120"
        "Month" -> "21,400"
        else -> "256,000"
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(24.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Text(
                text = "Partner Dashboard",
                color = Color.White,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            
            Text(
                text = "Track your advertising performance",
                color = Color.Gray,
                fontSize = 16.sp,
                modifier = Modifier.padding(bottom = 32.dp)
            )

            // Period Selector
            Text(
                text = "Select Period",
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded },
                modifier = Modifier.fillMaxWidth().padding(bottom = 32.dp)
            ) {
                OutlinedTextField(
                    value = selectedPeriod,
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
                    onDismissRequest = { expanded = false },
                    modifier = Modifier.background(Color(0xFF1E1E1E))
                ) {
                    periods.forEach { period ->
                        DropdownMenuItem(
                            text = { Text(text = period, color = Color.White) },
                            onClick = {
                                selectedPeriod = period
                                expanded = false
                            }
                        )
                    }
                }
            }

            // Stats Cards
            StatCard(
                title = "Ad Impressions",
                value = views,
                description = "Total number of times your ads were shown",
                iconText = "👁"
            )

            Spacer(modifier = Modifier.height(16.dp))

            StatCard(
                title = "Unique Users Reached",
                value = reachedUsers,
                description = "Total number of distinct people who saw your ads",
                iconText = "👤"
            )
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
                .align(Alignment.BottomCenter)
                .height(56.dp)
        ) {
            Text(text = "Return to Menu", fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun StatCard(
    title: String,
    value: String,
    description: String,
    iconText: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E))
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = title,
                    color = Color.Gray,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
            }
            
            Text(
                text = value,
                color = Color.White,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = 8.dp)
            )
            
            Text(
                text = description,
                color = Color(0xFF90CAF9),
                fontSize = 12.sp
            )
        }
    }
}

@Preview
@Composable
fun PartnerDashboardPreview() {
    ApplicationTheme {
        PartnerDashboardScreen()
    }
}
