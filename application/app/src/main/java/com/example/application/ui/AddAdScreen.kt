package com.example.application.ui

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.application.ui.bdd.Ad

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddAdScreen(
    onBackClick: () -> Unit,
    onSaveAdClick: (Ad) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    var videoUri by remember { mutableStateOf<String?>(null) }
    var expanded by remember { mutableStateOf(false) }
    
    val locations = listOf(
        "AFTER_LIST" to "After Creating a List",
        "AFTER_TIME" to "After X time on app",
        "AFTER_PUSHUP" to "After Push-ups",
        "AFTER_RUNNING" to "After Running"
    )
    var selectedLocation by remember { mutableStateOf(locations[0]) }
    var triggerValue by remember { mutableStateOf("") }

    val videoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        uri?.let {
            try {
                // Demande de permission persistante pour pouvoir lire la vidéo plus tard
                context.contentResolver.takePersistableUriPermission(
                    it,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
                videoUri = it.toString()
            } catch (e: Exception) {
                videoUri = it.toString()
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("New Advertisement", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Black)
            )
        },
        containerColor = Color.Black
    ) { padding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Ad Title", color = Color.Gray) },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedBorderColor = Color(0xFF1565C0),
                    unfocusedBorderColor = Color.Gray
                )
            )

            OutlinedTextField(
                value = content,
                onValueChange = { content = it },
                label = { Text("Ad Content / Message", color = Color.Gray) },
                modifier = Modifier.fillMaxWidth().height(100.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedBorderColor = Color(0xFF1565C0),
                    unfocusedBorderColor = Color.Gray
                )
            )

            Text("Video Content", color = Color.White, fontWeight = FontWeight.Bold)
            
            OutlinedButton(
                onClick = { videoPickerLauncher.launch(arrayOf("video/*")) },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(Icons.Default.PlayArrow, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text(if (videoUri == null) "Import Video" else "Change Video")
            }
            
            if (videoUri != null) {
                Text(
                    text = "Video selected successfully",
                    color = Color.Green,
                    fontSize = 12.sp
                )
            }

            Text("Trigger Location", color = Color.White, fontWeight = FontWeight.Bold)

            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded },
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = selectedLocation.second,
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable).fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = Color(0xFF1565C0),
                        unfocusedBorderColor = Color.Gray
                    )
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    modifier = Modifier.background(Color(0xFF1E1E1E))
                ) {
                    locations.forEach { location ->
                        DropdownMenuItem(
                            text = { Text(text = location.second, color = Color.White) },
                            onClick = {
                                selectedLocation = location
                                expanded = false
                            }
                        )
                    }
                }
            }

            if (selectedLocation.first == "AFTER_TIME") {
                OutlinedTextField(
                    value = triggerValue,
                    onValueChange = { triggerValue = it },
                    label = { Text("Time in minutes", color = Color.Gray) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = Color(0xFF1565C0),
                        unfocusedBorderColor = Color.Gray
                    )
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    if (title.isNotBlank() && content.isNotBlank()) {
                        onSaveAdClick(
                            Ad(
                                title = title,
                                content = content,
                                triggerLocation = selectedLocation.first,
                                triggerValue = triggerValue,
                                videoUri = videoUri
                            )
                        )
                    }
                },
                enabled = title.isNotBlank() && content.isNotBlank(),
                modifier = Modifier.fillMaxWidth().height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1565C0)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("SAVE ADVERTISEMENT", fontWeight = FontWeight.Bold)
            }
        }
    }
}
