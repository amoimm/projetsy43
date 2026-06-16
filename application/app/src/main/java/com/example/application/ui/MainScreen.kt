package com.example.application.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import java.util.Calendar
import java.util.Locale
import com.example.application.ui.bdd.ToDoList
import com.example.application.ui.bdd.ActiviteSportive

@Composable
fun ToDoListCard(
    toDoList: ToDoList,
    isExpanded: Boolean,
    onExpandClick: () -> Unit,
    onActivityPlayClick: (Int) -> Unit,
    onDeleteClick: () -> Unit,
    onShareClick: () -> Unit,
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
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            when(toDoList.frequency) {
                                "DAILY" -> {
                                    Badge(containerColor = Color(0xFF1565C0)) {
                                        Text("DAILY", color = Color.White, fontSize = 8.sp)
                                    }
                                }
                                "WEEKLY" -> {
                                    val calendar = Calendar.getInstance()
                                    val currentDay = calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.ENGLISH)
                                    val isForToday = toDoList.targetDays == "All week" || toDoList.targetDays == currentDay
                                    
                                    Badge(containerColor = if (isForToday) Color(0xFF592BC4) else Color.DarkGray) {
                                        Text("WEEKLY", color = Color.White, fontSize = 8.sp)
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "(${toDoList.targetDays})",
                                        color = if (isForToday) Color.Gray else Color.DarkGray,
                                        fontSize = 12.sp
                                    )
                                }
                                else -> {
                                    Text(
                                        text = "Due date : ${toDoList.date}",
                                        color = Color.Gray,
                                        fontSize = 12.sp
                                    )
                                }
                            }
                        }
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

                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (toDoList.isCompleted) {
                        IconButton(onClick = onShareClick) {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = "Share",
                                tint = Color(0xFF90CAF9)
                            )
                        }
                    }
                    IconButton(onClick = onDeleteClick) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete",
                            tint = Color(0xFFEF5350)
                        )
                    }
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
fun SectionHeader(
    title: String,
    isExpanded: Boolean,
    onToggle: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onToggle() }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp
        )
        Spacer(modifier = Modifier.width(8.dp))
        Box(
            modifier = Modifier
                .weight(1f)
                .height(1.dp)
                .background(Color.DarkGray.copy(alpha = 0.5f))
        )
        Icon(
            imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
            contentDescription = null,
            tint = Color.Gray,
            modifier = Modifier.size(20.dp)
        )
    }
}

@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
    toDoLists: List<ToDoList> = emptyList(),
    justCreated: Boolean = false,
    onValidateClick: (String) -> Unit = {},
    onDeleteClick: (ToDoList) -> Unit = {},
    onShareClick: (ToDoList) -> Unit = {},
    onSettingsClick: () -> Unit = {},
    onLogoutClick: () -> Unit = {}
) {
    val expandedCardIds = remember { mutableStateListOf<Int>() }
    
    // Toast state
    var showToast by remember { mutableStateOf(false) }
    
    LaunchedEffect(justCreated) {
        if (justCreated) {
            showToast = true
            delay(3000)
            showToast = false
        }
    }
    
    // States for section expansion
    var dailyExpanded by remember { mutableStateOf(true) }
    var weeklyExpanded by remember { mutableStateOf(true) }
    var onceExpanded by remember { mutableStateOf(true) }

    // Filter and Group
    val calendar = Calendar.getInstance()
    val currentDay = calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.ENGLISH)

    val dailyLists = toDoLists.filter { it.frequency == "DAILY" }.sortedByDescending { it.id }
    val weeklyLists = toDoLists.filter { it.frequency == "WEEKLY" }.sortedByDescending { it.id }
    val onceLists = toDoLists.filter { it.frequency == "ONCE" }.sortedByDescending { it.id }

    Surface(
        modifier = modifier.fillMaxSize(),
        color = Color.Black
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(modifier = Modifier.fillMaxSize()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 24.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "My To-Do Lists",
                        color = Color.White,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f)
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = onSettingsClick) {
                            Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = "Settings",
                                tint = Color.White,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                        IconButton(onClick = onLogoutClick) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                                contentDescription = "Switch Mode",
                                tint = Color.White,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }
                }

                if (toDoLists.isEmpty()) {
                    Box(
                        modifier = Modifier.weight(1f).fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("No lists created yet", color = Color.Gray)
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(bottom = 16.dp)
                    ) {
                        // DAILY Section
                        if (dailyLists.isNotEmpty()) {
                            item {
                                SectionHeader(
                                    title = "DAILY",
                                    isExpanded = dailyExpanded,
                                    onToggle = { dailyExpanded = !dailyExpanded }
                                )
                            }
                            if (dailyExpanded) {
                                items(dailyLists, key = { "daily_${it.id}" }) { toDoList ->
                                    ToDoListCard(
                                        toDoList = toDoList,
                                        isExpanded = expandedCardIds.contains(toDoList.id),
                                        onExpandClick = {
                                            if (expandedCardIds.contains(toDoList.id)) expandedCardIds.remove(toDoList.id)
                                            else expandedCardIds.add(toDoList.id)
                                        },
                                        onActivityPlayClick = { activityIndex ->
                                            val originalIndex = toDoLists.indexOf(toDoList)

                                            val rawCategory = toDoList.activities[activityIndex].categorie
                                            val cleanCategory = rawCategory.replace(" ", "").lowercase().replaceFirstChar { it.uppercase() }
                                            val screenName = cleanCategory + "Screen"


                                            onValidateClick("$screenName|$originalIndex|$activityIndex")
                                        },
                                        onDeleteClick = { onDeleteClick(toDoList) },
                                        onShareClick = { onShareClick(toDoList) }
                                    )
                                }
                            }
                        }

                        // WEEKLY Section
                        if (weeklyLists.isNotEmpty()) {
                            item {
                                SectionHeader(
                                    title = "WEEKLY",
                                    isExpanded = weeklyExpanded,
                                    onToggle = { weeklyExpanded = !weeklyExpanded }
                                )
                            }
                            if (weeklyExpanded) {
                                items(weeklyLists, key = { "weekly_${it.id}" }) { toDoList ->
                                    ToDoListCard(
                                        toDoList = toDoList,
                                        isExpanded = expandedCardIds.contains(toDoList.id),
                                        onExpandClick = {
                                            if (expandedCardIds.contains(toDoList.id)) expandedCardIds.remove(toDoList.id)
                                            else expandedCardIds.add(toDoList.id)
                                        },
                                        onActivityPlayClick = { activityIndex ->
                                            val originalIndex = toDoLists.indexOf(toDoList)
                                            val screenName = toDoList.activities[activityIndex].categorie.replace(" ", "") + "Screen"
                                            onValidateClick("$screenName|$originalIndex|$activityIndex")
                                        },
                                        onDeleteClick = { onDeleteClick(toDoList) },
                                        onShareClick = { onShareClick(toDoList) }
                                    )
                                }
                            }
                        }

                        // ONCE Section
                        if (onceLists.isNotEmpty()) {
                            item {
                                SectionHeader(
                                    title = "ONCE",
                                    isExpanded = onceExpanded,
                                    onToggle = { onceExpanded = !onceExpanded }
                                )
                            }
                            if (onceExpanded) {
                                items(onceLists, key = { "once_${it.id}" }) { toDoList ->
                                    ToDoListCard(
                                        toDoList = toDoList,
                                        isExpanded = expandedCardIds.contains(toDoList.id),
                                        onExpandClick = {
                                            if (expandedCardIds.contains(toDoList.id)) expandedCardIds.remove(toDoList.id)
                                            else expandedCardIds.add(toDoList.id)
                                        },
                                        onActivityPlayClick = { activityIndex ->
                                            val originalIndex = toDoLists.indexOf(toDoList)
                                            onValidateClick("${toDoList.activities[activityIndex].categorie}Screen|$originalIndex|$activityIndex")
                                        },
                                        onDeleteClick = { onDeleteClick(toDoList) },
                                        onShareClick = { onShareClick(toDoList) }
                                    )
                                }
                            }
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

            // Animated Message Overlay (Toast style)
            AnimatedVisibility(
                visible = showToast,
                enter = slideInVertically(
                    initialOffsetY = { -it },
                    animationSpec = tween(durationMillis = 150, easing = LinearOutSlowInEasing)
                ) + fadeIn(),
                exit = slideOutVertically(
                    targetOffsetY = { -it },
                    animationSpec = tween(durationMillis = 250, easing = FastOutLinearInEasing)
                ) + fadeOut(),
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 16.dp)
            ) {
                Surface(
                    color = Color(0xFF4CAF50).copy(alpha = 0.85f),
                    shape = RoundedCornerShape(24.dp),
                    shadowElevation = 8.dp,
                    modifier = Modifier.padding(horizontal = 32.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "List created successfully!",
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }
}
