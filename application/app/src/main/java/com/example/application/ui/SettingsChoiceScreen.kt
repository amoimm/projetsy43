package com.example.application.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.application.ui.theme.ApplicationTheme

@Composable
fun SettingsChoiceScreen(
    onBackClick: () -> Unit,
    onPersonalInfoClick: () -> Unit,
    onCapacityClick: () -> Unit,
    onMotivationClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(24.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            IconButton(onClick = onBackClick) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Text(
                text = "Edit Profile",
                color = Color.White,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )
            
            Text(
                text = "What would you like to change?",
                color = Color.Gray,
                fontSize = 16.sp,
                modifier = Modifier.padding(bottom = 32.dp)
            )

            SettingsOptionItem(
                title = "Personal Information",
                subtitle = "Name, age, weight, height",
                icon = Icons.Default.Person,
                onClick = onPersonalInfoClick
            )

            Spacer(modifier = Modifier.height(16.dp))

            SettingsOptionItem(
                title = "Physical Capacity",
                subtitle = "Pushups, pullups, squats and running limits",
                icon = Icons.Default.Edit,
                onClick = onCapacityClick
            )

            Spacer(modifier = Modifier.height(16.dp))

            SettingsOptionItem(
                title = "Daily Motivation",
                subtitle = "How you feel today",
                icon = Icons.Default.Favorite,
                onClick = onMotivationClick
            )
        }
    }
}

@Composable
fun SettingsOptionItem(
    title: String,
    subtitle: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E))
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(Color(0xFF1565C0).copy(alpha = 0.2f), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = Color(0xFF90CAF9))
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column {
                Text(
                    text = title,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Text(
                    text = subtitle,
                    color = Color.Gray,
                    fontSize = 12.sp
                )
            }
        }
    }
}
