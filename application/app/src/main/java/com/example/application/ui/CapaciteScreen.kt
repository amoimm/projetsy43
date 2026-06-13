package com.example.application.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.application.R
import com.example.application.ui.theme.ApplicationTheme

@Composable
fun CapaciteScreen(
    modifier: Modifier = Modifier,
    initialPushups: String = "",
    initialRunningKm: String = "",
    onBackClick: () -> Unit = {},
    onValidateClick: (String, String) -> Unit = { _, _ -> }
) {
    var pompes by remember(initialPushups) { mutableStateOf(initialPushups) }
    var courseKm by remember(initialRunningKm) { mutableStateOf(initialRunningKm) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(horizontal = 24.dp, vertical = 32.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            IconButton(onClick = onBackClick, modifier = Modifier.padding(bottom = 8.dp)) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White,
                    modifier = Modifier.size(32.dp)
                )
            }

            Text(
                text = stringResource(id = R.string.capacity_info_title),
                color = Color.White,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(40.dp))

            InfoInputField(
                label = stringResource(id = R.string.capacity_info_pompes) + " (Max without a break)",
                value = pompes,
                onValueChange = { if (it.all { char -> char.isDigit() }) pompes = it },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            Spacer(modifier = Modifier.height(24.dp))

            InfoInputField(
                label = stringResource(id = R.string.capacity_info_courses) + " (km Max without a break)",
                value = courseKm,
                onValueChange = { input ->
                    if (input.isEmpty() || input.matches(Regex("""^\d*[.,]?\d*$"""))) {
                        courseKm = input.replace(',', '.')
                    }
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
            )
        }

        val isFormValid = pompes.isNotBlank() && courseKm.isNotBlank()

        Button(
            onClick = { onValidateClick(pompes, courseKm) },
            enabled = isFormValid,
            colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = Color.Black),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(bottom = 16.dp)
                .height(56.dp)
        ) {
            Text(text = stringResource(id = R.string.personal_info_validate), fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }
    }
}
