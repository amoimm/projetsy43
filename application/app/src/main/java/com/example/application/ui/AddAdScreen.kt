package com.example.application.ui

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.application.ui.bdd.Ad

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddAdScreen(
    partnerId: Int,
    onBackClick: () -> Unit,
    onSaveAdClick: (Ad) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    var videoUri by remember { mutableStateOf<String?>(null) }
    
    val locations = listOf(
        "AFTER_LIST" to "After Creating a List",
        "AFTER_DELETE" to "After Deleting a List",
        "AFTER_PUSHUP" to "After BodyWeight",
        "AFTER_RUNNING" to "After Running"
    )
    
    // Multiple selection state
    val selectedLocations = remember { mutableStateListOf<String>() }

    val videoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        uri?.let {
            try {
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
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
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

            Text("Video Content (Required)", color = Color.White, fontWeight = FontWeight.Bold)
            
            OutlinedButton(
                onClick = { videoPickerLauncher.launch(arrayOf("video/*")) },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = if (videoUri == null) Color(0xFFEF5350) else Color.White
                ),
                shape = RoundedCornerShape(8.dp),
                border = if (videoUri == null) ButtonDefaults.outlinedButtonBorder.copy(brush = androidx.compose.ui.graphics.SolidColor(Color.Red)) else ButtonDefaults.outlinedButtonBorder
            ) {
                Icon(Icons.Default.PlayArrow, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text(if (videoUri == null) "IMPORT VIDEO FILE" else "CHANGE VIDEO")
            }
            
            if (videoUri != null) {
                Text(
                    text = "✓ Video file successfully imported",
                    color = Color(0xFF4CAF50),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Text("Select Trigger Events", color = Color.White, fontWeight = FontWeight.Bold)

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF1E1E1E), RoundedCornerShape(12.dp))
                    .padding(8.dp)
            ) {
                locations.forEach { (key, label) ->
                    val isSelected = selectedLocations.contains(key)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                if (isSelected) selectedLocations.remove(key)
                                else selectedLocations.add(key)
                            }
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = isSelected,
                            onCheckedChange = null, // Handled by row click
                            colors = CheckboxDefaults.colors(
                                checkedColor = Color(0xFF1565C0),
                                uncheckedColor = Color.Gray
                            )
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(text = label, color = Color.White)
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            val canSave = title.isNotBlank() && videoUri != null && selectedLocations.isNotEmpty()

            Button(
                onClick = {
                    if (canSave) {
                        onSaveAdClick(
                            Ad(
                                partnerId = partnerId,
                                title = title,
                                content = content,
                                triggerLocation = selectedLocations.joinToString(","),
                                triggerValue = "",
                                videoUri = videoUri
                            )
                        )
                    }
                },
                enabled = canSave,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF1565C0),
                    disabledContainerColor = Color.Gray.copy(alpha = 0.3f)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("SAVE ADVERTISEMENT", fontWeight = FontWeight.Bold)
            }
            
            if (!canSave) {
                Text(
                    text = "Please provide a title, a video file, and at least one trigger.",
                    color = Color.Red.copy(alpha = 0.7f),
                    fontSize = 12.sp,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}
