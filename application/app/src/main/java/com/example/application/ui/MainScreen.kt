package com.example.application.ui

import android.content.Intent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.jvm.java
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow


@Composable
fun ToDoItem(
    activity: ActiviteSportive,
    isExpanded: Boolean,
    onExpandClick: () -> Unit,
    onPlayClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val suffixe = if (activity.categorie == "Running") "km" else "reps"
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1E1E1E)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "${activity.categorie}",
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp
                )
                TextButton(onClick = onExpandClick) {
                    Text(
                        text = if (isExpanded) "Show less" else "Show more",
                        color = Color(0xFF90CAF9),
                        fontSize = 13.sp
                    )
                }
                Button(
                    onClick = onPlayClick,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.size(48.dp),
                    contentPadding = PaddingValues(0.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1565C0))
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "Start",
                        tint = Color.White
                    )
                }
            }

            if (isExpanded) {
                Spacer(modifier = Modifier.height(8.dp))
                HorizontalDivider(color = Color(0xFF333333))
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Détails de l'activité : ${activity.categorie} \n Goal : ${activity.valeur} $suffixe",
                    color = Color(0xFFB0B0B0),
                    fontSize = 14.sp
                )
            }
        }
    }
}


@Composable
fun ToDoScreen(
    modifier: Modifier = Modifier,
    activities: List<ActiviteSportive> = emptyList(),
    onValidateClick: (String) -> Unit = {}
) {
    val expandedIndices = remember { mutableStateListOf<Int>() }

    Surface(
        modifier = modifier.fillMaxSize(),
        color = Color.Black
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Text(
                text = "My To-Do List",
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 16.dp, top = 24.dp, bottom = 8.dp)
            )

            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(vertical = 8.dp)
            ) {
                itemsIndexed(activities) { index, activity ->
                    ToDoItem(
                        activity = activity,
                        isExpanded = expandedIndices.contains(index),
                        onExpandClick = {
                            if (expandedIndices.contains(index)) {
                                expandedIndices.remove(index)
                            } else {
                                expandedIndices.add(index)
                            }
                        },
                        onPlayClick = { onValidateClick(activity.categorie + "Screen") }
                    )
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = { expandedIndices.clear() },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White)
                ) {
                    Text("Show less")
                }

                Button(
                    onClick = { onValidateClick("Build_ToDo_List") },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1565C0))
                ) {
                    Text("Create my to-do list", color = Color.White)
                }
            }
        }
    }
}


@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
    activities: List<ActiviteSportive> = emptyList(),
    onValidateClick: (String) -> Unit = {}
) {
    ToDoScreen(
        modifier = modifier,
        activities = activities,
        onValidateClick = onValidateClick
    )
}