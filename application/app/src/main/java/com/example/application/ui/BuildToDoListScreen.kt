package com.example.application.ui

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import com.example.application.ui.bdd.ToDoList
import com.example.application.ui.bdd.ActiviteSportive
import com.example.application.ui.bdd.ActivityCategory
import com.example.application.ui.bdd.Frequency

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BuildToDoListScreen(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit,
    onSaveClick: (ToDoList) -> Unit
) {
    var titleSaisie by remember { mutableStateOf("") }
    var listeActivites by remember { mutableStateOf(listOf<ActiviteSportive>()) }
    var categorieSelectionnee by remember { mutableStateOf(ActivityCategory.PUSHUP) }
    var valeurSaisie by remember { mutableStateOf("") }
    var compteurId by remember { mutableIntStateOf(0) }

    var frequency by remember { mutableStateOf(Frequency.ONCE) }
    val daysOfWeek = listOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday", "All week")
    var selectedDay by remember { mutableStateOf("Monday") }

    var dateSelectionnee by remember { mutableStateOf(SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())) }
    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    val selectedMillis = datePickerState.selectedDateMillis
                    if (selectedMillis != null) {
                        val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                        dateSelectionnee = formatter.format(Date(selectedMillis))
                    }
                    showDatePicker = false
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("Cancel") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
        ) {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White
                )
            }

            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(bottom = 100.dp)
            ) {
                item {
                    Text(
                        text = "Create To-Do List",
                        color = Color.White,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(vertical = 24.dp)
                    )

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E))
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            OutlinedTextField(
                                value = titleSaisie,
                                onValueChange = { titleSaisie = it },
                                modifier = Modifier.fillMaxWidth(),
                                label = { Text("List Title", color = Color.Gray) },
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White,
                                    focusedBorderColor = Color(0xFF1565C0),
                                    unfocusedBorderColor = Color.Gray
                                ),
                                singleLine = true
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            Text("Frequency :", fontWeight = FontWeight.SemiBold, color = Color.White)
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                val frequencies = listOf(Frequency.ONCE to "Once", Frequency.DAILY to "Daily", Frequency.WEEKLY to "Weekly")
                                frequencies.forEach { (key, label) ->
                                    Button(
                                        onClick = { frequency = key },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = if (frequency == key) Color(0xFF1565C0) else Color.Gray
                                        ),
                                        modifier = Modifier.weight(1f),
                                        contentPadding = PaddingValues(4.dp)
                                    ) {
                                        Text(label, fontSize = 12.sp, color = Color.White)
                                    }
                                }
                            }

                            if (frequency == Frequency.ONCE) {
                                Text("Select Date :", fontWeight = FontWeight.SemiBold, color = Color.White)
                                OutlinedButton(
                                    onClick = { showDatePicker = true },
                                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                                    colors = ButtonDefaults.outlinedButtonColors(containerColor = Color(0xFF2C2C2C), contentColor = Color.White)
                                ) {
                                    Text(text = dateSelectionnee)
                                }
                            } else if (frequency == Frequency.WEEKLY) {
                                Text("Select Day :", fontWeight = FontWeight.SemiBold, color = Color.White)
                                var expandedDays by remember { mutableStateOf(false) }
                                ExposedDropdownMenuBox(
                                    expanded = expandedDays,
                                    onExpandedChange = { expandedDays = !expandedDays },
                                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
                                ) {
                                    OutlinedTextField(
                                        value = selectedDay,
                                        onValueChange = {},
                                        readOnly = true,
                                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedDays) },
                                        modifier = Modifier.menuAnchor().fillMaxWidth(),
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedTextColor = Color.White,
                                            unfocusedTextColor = Color.White,
                                            focusedContainerColor = Color(0xFF2C2C2C),
                                            unfocusedContainerColor = Color(0xFF2C2C2C)
                                        )
                                    )
                                    ExposedDropdownMenu(
                                        expanded = expandedDays,
                                        onDismissRequest = { expandedDays = false }
                                    ) {
                                        daysOfWeek.forEach { day ->
                                            DropdownMenuItem(
                                                text = { Text(day) },
                                                onClick = {
                                                    selectedDay = day
                                                    expandedDays = false
                                                }
                                            )
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))
                            HorizontalDivider(color = Color.DarkGray)
                            Spacer(modifier = Modifier.height(16.dp))

                            Text("Add Activity :", fontWeight = FontWeight.SemiBold, color = Color.White)
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                val cats = listOf(
                                    ActivityCategory.PUSHUP to "Pushup",
                                    ActivityCategory.PULLUP to "Pullup",
                                    ActivityCategory.SQUAT to "Squat",
                                    ActivityCategory.RUNNING to "Running"
                                )
                                cats.forEach { (cat, label) ->
                                    Button(
                                        onClick = {
                                            categorieSelectionnee = cat
                                            valeurSaisie = ""
                                        },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = if (categorieSelectionnee == cat) Color(0xFF1565C0) else Color.Gray
                                        ),
                                        modifier = Modifier.weight(1f),
                                        contentPadding = PaddingValues(horizontal = 4.dp)
                                    ) { Text(label, color = Color.White, fontSize = 11.sp, maxLines = 1) }
                                }
                            }

                            OutlinedTextField(
                                value = valeurSaisie,
                                onValueChange = { input ->
                                    if (categorieSelectionnee == ActivityCategory.RUNNING) {
                                        if (input.isEmpty() || input.matches(Regex("""^\d*[.,]?\d*$"""))) {
                                            valeurSaisie = input.replace(',', '.')
                                        }
                                    } else {
                                        if (input.all { it.isDigit() }) {
                                            valeurSaisie = input
                                        }
                                    }
                                },
                                modifier = Modifier.fillMaxWidth(),
                                label = { Text("Goal (reps or km)", color = Color.Gray) },
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White,
                                    focusedBorderColor = Color(0xFF1565C0),
                                    unfocusedBorderColor = Color.Gray
                                ),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                singleLine = true
                            )

                            Button(
                                onClick = {
                                    if (valeurSaisie.isNotBlank()) {
                                        listeActivites = listeActivites + ActiviteSportive(compteurId++, categorieSelectionnee, valeurSaisie)
                                        valeurSaisie = ""
                                    }
                                },
                                modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1565C0))
                            ) {
                                Text("Add Activity")
                            }
                        }
                    }
                }

                items(listeActivites, key = { it.id }) { activite ->
                    var isVisible by remember { mutableStateOf(true) }
                    
                    AnimatedVisibility(
                        visible = isVisible,
                        exit = slideOutHorizontally(targetOffsetX = { -it }) + fadeOut(),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        ActiviteItem(
                            activite = activite,
                            onDeleteClick = {
                                isVisible = false
                            }
                        )
                    }
                    
                    // Once the exit animation is complete, the item is actually removed from the list
                    LaunchedEffect(isVisible) {
                        if (!isVisible) {
                            kotlinx.coroutines.delay(300) // Durée de l'animation
                            listeActivites = listeActivites.filter { it.id != activite.id }
                        }
                    }
                }
            }
        }

        Button(
            onClick = { 
                if (titleSaisie.isNotBlank() && listeActivites.isNotEmpty()) {
                    val activitiesString = listeActivites.joinToString(";") {
                        "${it.categorie.name},${it.valeur},${it.isDone},${it.progress}"
                    }
                    onSaveClick(
                        ToDoList(
                            title = titleSaisie,
                            date = if (frequency == Frequency.ONCE) dateSelectionnee else "",
                            activitiesJson = activitiesString,
                            frequency = frequency,
                            targetDays = if (frequency == Frequency.WEEKLY) selectedDay else ""
                        )
                    )
                }
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.White,
                contentColor = Color.Black
            ),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(24.dp)
                .height(56.dp)
        ) {
            Text(text = "Finish my to-do list", fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun ActiviteItem(
    activite: ActiviteSportive,
    onDeleteClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF2C2C2C))
    ) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = activite.categorie.name,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = Color.White,
                modifier = Modifier.weight(1f)
            )
            val suffixe = if (activite.categorie == ActivityCategory.RUNNING) "km" else "reps"
            Text(
                text = "${activite.valeur} $suffixe",
                fontSize = 18.sp,
                color = Color.LightGray
            )
            Spacer(modifier = Modifier.width(16.dp))
            IconButton(
                onClick = onDeleteClick,
                modifier = Modifier.size(24.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Remove",
                    tint = Color.Red.copy(alpha = 0.7f),
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}
