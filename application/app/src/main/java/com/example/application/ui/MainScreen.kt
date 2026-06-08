package com.example.application.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import com.example.application.ui.bdd.ToDoList
import com.example.application.ui.bdd.ActiviteSportive
import androidx.compose.foundation.lazy.items

@Composable
fun ToDoListCard(
    toDoList: ToDoList,
    isExpanded: Boolean,
    onExpandClick: () -> Unit,
    onActivityPlayClick: (Int) -> Unit, // Reçoit l'index de l'activité
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E)),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .background(
                                color = if (toDoList.isCompleted) Color(0xFF4CAF50) else Color(0xFF333333),
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        if (toDoList.isCompleted) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Terminé",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = toDoList.title,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                        Text(
                            text = toDoList.date,
                            color = Color.Gray,
                            fontSize = 14.sp
                        )
                    }
                }

                TextButton(onClick = onExpandClick) {
                    Text(
                        text = if (isExpanded) "Show less" else "Show more",
                        color = Color(0xFF90CAF9)
                    )
                }
            }

            if (isExpanded) {
                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(color = Color(0xFF333333))
                Spacer(modifier = Modifier.height(8.dp))

                toDoList.activities.forEachIndexed { index, activity ->
                    ActivityRow(
                        activity = activity,
                        onPlayClick = { onActivityPlayClick(index) }
                    )
                }
            }
        }
    }
}

@Composable
fun ActivityRow(
    activity: ActiviteSportive,
    onPlayClick: () -> Unit
) {
    val suffixe = if (activity.categorie == "Running") "km" else "reps"
    val progressVal = try {
        if (activity.categorie == "Running") "%.2f".format(activity.progress.toFloat())
        else activity.progress
    } catch (e: Exception) { "0" }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(
                text = activity.categorie,
                color = Color.White,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = "Progress: $progressVal / ${activity.valeur} $suffixe",
                color = Color.Gray,
                fontSize = 12.sp
            )
        }

        if (activity.isDone) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = "Fait",
                tint = Color(0xFF4CAF50),
                modifier = Modifier.size(28.dp)
            )
        } else {
            IconButton(
                onClick = onPlayClick,
                modifier = Modifier
                    .size(32.dp)
                    .background(Color(0xFF1565C0), RoundedCornerShape(8.dp))
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = "Lancer",
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
    toDoLists: List<ToDoList> = emptyList(),
    onValidateClick: (String) -> Unit = {}
) {
    // Utiliser les IDs pour suivre les cartes dépliées (plus robuste que les index après un tri)
    val expandedIds = remember { mutableStateListOf<Int>() }

    // Tri chronologique des listes par date (dd/MM/yyyy)
    val sortedToDoLists = remember(toDoLists) {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        toDoLists.sortedBy { list ->
            try {
                dateFormat.parse(list.date)
            } catch (e: Exception) {
                Date(0) // Date par défaut si le format est invalide ou "Select a date"
            }
        }
    }

    Surface(
        modifier = modifier.fillMaxSize(),
        color = Color.Black
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Text(
                text = "My To-Do Lists",
                color = Color.White,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 16.dp, top = 24.dp, bottom = 16.dp)
            )

            if (sortedToDoLists.isEmpty()) {
                Box(
                    modifier = Modifier.weight(1f).fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No To-Do Lists created", color = Color.Gray)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(vertical = 8.dp)
                ) {
                    items(sortedToDoLists, key = { it.id }) { toDoList ->
                        ToDoListCard(
                            toDoList = toDoList,
                            isExpanded = expandedIds.contains(toDoList.id),
                            onExpandClick = {
                                if (expandedIds.contains(toDoList.id)) {
                                    expandedIds.remove(toDoList.id)
                                } else {
                                    expandedIds.add(toDoList.id)
                                }
                            },
                            onActivityPlayClick = { activityIndex ->
                                // Navigation formatée : "Screen|ListIndex|ActivityIndex"
                                // Note: on utilise l'index réel de la liste dans toDoLists pour la navigation
                                val originalIndex = toDoLists.indexOf(toDoList)
                                onValidateClick("${toDoList.activities[activityIndex].categorie}Screen|$originalIndex|$activityIndex")
                            }
                        )
                    }
                }
            }

            Button(
                onClick = { onValidateClick("Build_ToDo_List") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1565C0)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Add new list", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}
