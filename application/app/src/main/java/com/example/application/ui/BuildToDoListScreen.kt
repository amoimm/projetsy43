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

data class ActiviteSportive(val id: Int, val categorie: String, val valeur: String)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BuildToDoListScreen(
    modifier: Modifier = Modifier,
    onValidateClick: (List<ActiviteSportive>) -> Unit
) {
    var listeActivites by remember { mutableStateOf(listOf<ActiviteSportive>()) }
    var categorieSelectionnee by remember { mutableStateOf("Push up") }
    var valeurSaisie by remember { mutableStateOf("") }
    var compteurId by remember { mutableIntStateOf(0) }


    var dateSelectionnee by remember { mutableStateOf("Select a date") }
    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()

    // Le dialogue qui s'ouvre au clic pour choisir la date
    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    val selectedMillis = datePickerState.selectedDateMillis
                    if (selectedMillis != null) {
                        // Formater la date en jj/MM/aaaa
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
                text = "My to-do List",
                color = Color.White,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 24.dp, top = 32.dp)
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF0F0F0))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {

                    // Section pour la date
                    Text("Due Date :", fontWeight = FontWeight.SemiBold, color = Color.Black)
                    OutlinedButton(
                        onClick = { showDatePicker = true },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = Color.White,
                            contentColor = Color.Black
                        )
                    ) {
                        Text(text = dateSelectionnee)
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Section Catégorie (décalée vers le bas)
                    Text("Category :", fontWeight = FontWeight.SemiBold, color = Color.Black)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { categorieSelectionnee = "Push up" },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (categorieSelectionnee == "Push up") MaterialTheme.colorScheme.primary else Color.Gray
                            )
                        ) { Text("Push up", color = Color.White) }
                        Button(
                            onClick = { categorieSelectionnee = "Running" },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (categorieSelectionnee == "Running") MaterialTheme.colorScheme.primary else Color.Gray
                            )
                        ) { Text("Running", color = Color.White) }
                    }

                    OutlinedTextField(
                        value = valeurSaisie,
                        onValueChange = { valeurSaisie = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Value (rep or km)") },
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
                            .padding(top = 16.dp)
                    ) {
                        Text("Add to the list")
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
            onClick = { onValidateClick(listeActivites) },
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
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = activite.categorie, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color.Black)

            val suffixe = if (activite.categorie == "Running") "km" else "reps"
            Text(text = "${activite.valeur} $suffixe", fontSize = 18.sp, color = Color.DarkGray)
        }
    }
}