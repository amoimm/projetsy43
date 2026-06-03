package com.example.application.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
//FOR BDD
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.application.ui.bdd.ToDoList
import com.example.application.ui.bdd.ActiviteSportive

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BuildToDoListScreen(
    modifier: Modifier = Modifier,
    onValidateClick: (ToDoList) -> Unit
) {
    var titleSaisie by remember { mutableStateOf("") }
    var listeActivites by remember { mutableStateOf(listOf<ActiviteSportive>()) }
    var categorieSelectionnee by remember { mutableStateOf("Push up") }
    var valeurSaisie by remember { mutableStateOf("") }
    var compteurId by remember { mutableIntStateOf(0) }

    var dateSelectionnee by remember { mutableStateOf("Select a date") }
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
            Text(
                text = "Create To-Do List",
                color = Color.White,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 24.dp, top = 32.dp)
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

                    Text("Due Date :", fontWeight = FontWeight.SemiBold, color = Color.White)
                    OutlinedButton(
                        onClick = { showDatePicker = true },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = Color(0xFF2C2C2C),
                            contentColor = Color.White
                        )
                    ) {
                        Text(text = dateSelectionnee)
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text("Add Activity :", fontWeight = FontWeight.SemiBold, color = Color.White)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { categorieSelectionnee = "Push up" },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (categorieSelectionnee == "Push up") Color(0xFF1565C0) else Color.Gray
                            ),
                            modifier = Modifier.weight(1f)
                        ) { Text("Push up", color = Color.White) }
                        Button(
                            onClick = { categorieSelectionnee = "Running" },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (categorieSelectionnee == "Running") Color(0xFF1565C0) else Color.Gray
                            ),
                            modifier = Modifier.weight(1f)
                        ) { Text("Running", color = Color.White) }
                    }

                    OutlinedTextField(
                        value = valeurSaisie,
                        onValueChange = { valeurSaisie = it },
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
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1565C0))
                    ) {
                        Text("Add to current list")
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .padding(bottom = 80.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(listeActivites) { activite ->
                    ActiviteItem(activite)
                }
            }
        }

        Button(
            onClick = { 
                if (titleSaisie.isNotBlank() && listeActivites.isNotEmpty()) {
                    val activitiesString = listeActivites.joinToString(";") {
                        "${it.categorie},${it.valeur},${it.isDone}"
                    }
                    onValidateClick(
                        ToDoList(
                            title = titleSaisie,
                            date = dateSelectionnee,
                            activitiesJson = activitiesString
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
                .padding(start = 24.dp, end = 24.dp, bottom = 24.dp)
                .height(56.dp)
        ) {
            Text(text = "Finish my to-do list", fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun ActiviteItem(activite: ActiviteSportive) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF2C2C2C))
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = activite.categorie, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color.White)

            val suffixe = if (activite.categorie == "Running") "km" else "reps"
            Text(text = "${activite.valeur} $suffixe", fontSize = 18.sp, color = Color.LightGray)
        }
    }
}