package com.example.application.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.application.ui.bdd.ToDoList
import com.example.application.ui.bdd.Frequency
import java.util.Calendar
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommunityToDoListsScreen(
    communityLists: List<ToDoList>,
    getUsernameById: suspend (Int) -> String?,
    onBackClick: () -> Unit,
    onCopyClick: (ToDoList) -> Unit,
    modifier: Modifier = Modifier
) {
    val expandedCardIds = remember { mutableStateListOf<Int>() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Community Lists", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Black)
            )
        },
        containerColor = Color.Black
    ) { padding ->
        if (communityLists.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text("No community lists available yet.", color = Color.Gray)
            }
        } else {
            LazyColumn(
                modifier = modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                items(communityLists) { list ->
                    val isExpanded = expandedCardIds.contains(list.id)
                    var creatorName by remember { mutableStateOf<String?>(null) }
                    
                    LaunchedEffect(list.userId) {
                        creatorName = getUsernameById(list.userId)
                    }

                    CommunityListCard(
                        toDoList = list,
                        creatorName = creatorName,
                        isExpanded = isExpanded,
                        onExpandClick = {
                            if (isExpanded) expandedCardIds.remove(list.id)
                            else expandedCardIds.add(list.id)
                        },
                        onCopyClick = { onCopyClick(list) }
                    )
                }
            }
        }
    }
}

@Composable
fun CommunityListCard(
    toDoList: ToDoList,
    creatorName: String?,
    isExpanded: Boolean,
    onExpandClick: () -> Unit,
    onCopyClick: () -> Unit,
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
                Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                    Column {
                        Text(
                            text = toDoList.title,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                        
                        creatorName?.let { name ->
                            Text(
                                text = "Created by: $name",
                                color = Color(0xFF90CAF9),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }

                        when (toDoList.frequency) {
                            Frequency.DAILY -> {
                                Badge(containerColor = Color(0xFF1565C0)) {
                                    Text("DAILY", color = Color.White, fontSize = 8.sp)
                                }
                            }
                            Frequency.WEEKLY -> {
                                Badge(containerColor = Color(0xFF592BC4)) {
                                    Text("WEEKLY", color = Color.White, fontSize = 8.sp)
                                }
                            }
                            else -> {
                                Text(
                                    text = "Date: ${toDoList.date}",
                                    color = Color.Gray,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = onCopyClick,
                        modifier = Modifier
                            .size(32.dp)
                            .background(Color(0xFF4CAF50), CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Copy List",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    
                    Spacer(modifier = Modifier.width(8.dp))

                    TextButton(onClick = onExpandClick) {
                        Text(
                            text = if (isExpanded) "Show less" else "Show more",
                            color = Color(0xFF90CAF9)
                        )
                    }
                }
            }

            if (isExpanded) {
                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(color = Color(0xFF333333))
                Spacer(modifier = Modifier.height(8.dp))

                toDoList.activities.forEach { activity ->
                    Column(modifier = Modifier.padding(vertical = 4.dp)) {
                        Text(
                            text = activity.categorie.name,
                            color = Color.White,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "Goal: ${activity.valeur} ${if (activity.categorie.name == "RUNNING") "km" else "reps"}",
                            color = Color.Gray,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }
    }
}
