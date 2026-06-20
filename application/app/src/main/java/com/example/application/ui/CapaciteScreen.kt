package com.example.application.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.application.R

@Composable
fun CapaciteScreen(
    modifier: Modifier = Modifier,
    initialPushups: String = "0",
    initialPullups: String = "0",
    initialSquats: String = "0",
    initialRunningKm: String = "0",
    onBackClick: () -> Unit = {},
    onValidateClick: (String, String, String, String) -> Unit = { _, _, _, _ -> }
) {
    var pushup by remember(initialPushups) { mutableStateOf(initialPushups) }
    var pullup by remember(initialPullups) { mutableStateOf(initialPullups) }
    var squat by remember(initialSquats) { mutableStateOf(initialSquats) }
    var courseKm by remember(initialRunningKm) { mutableStateOf(initialRunningKm) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(horizontal = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(top = 32.dp, bottom = 100.dp)
        ) {
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
                label = "Pushups (Max)",
                value = pushup,
                onValueChange = { if (it.all { char -> char.isDigit() } || it.isEmpty()) pushup = it },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            Spacer(modifier = Modifier.height(16.dp))

            InfoInputField(
                label = "Pullups (Max)",
                value = pullup,
                onValueChange = { if (it.all { char -> char.isDigit() } || it.isEmpty()) pullup = it },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            Spacer(modifier = Modifier.height(16.dp))

            InfoInputField(
                label = "Squats (Max)",
                value = squat,
                onValueChange = { if (it.all { char -> char.isDigit() } || it.isEmpty()) squat = it },
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

        val isFormValid = pushup.isNotBlank()
                && pullup.isNotBlank()
                && squat.isNotBlank()
                && courseKm.isNotBlank()

        Button(
            onClick = { onValidateClick(pushup, pullup, squat, courseKm) },
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
