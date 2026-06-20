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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.application.R

@Composable
fun PersonalInfoScreen(
    modifier: Modifier = Modifier,
    initialUsername: String = "",
    initialMdp: String = "",
    initialName: String = "",
    initialAge: String = "",
    initialWeight: String = "",
    initialHeight: String = "",
    onBackClick: () -> Unit = {},
    onValidateClick: (username: String, mdp: String, name: String, age: String, weight: String, height: String) -> Unit = { _, _, _, _, _, _ -> }
) {
    var username by remember(initialUsername) { mutableStateOf(initialUsername) }
    var mdp by remember(initialMdp) { mutableStateOf(initialMdp) }
    var name by remember(initialName) { mutableStateOf(initialName) }
    var age by remember(initialAge) { mutableStateOf(initialAge) }
    var weight by remember(initialWeight) { mutableStateOf(initialWeight) }
    var height by remember(initialHeight) { mutableStateOf(initialHeight) }

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
                text = stringResource(id = R.string.personal_info_title),
                color = Color.White,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(30.dp))

            InfoInputField(
                label = "Username",
                value = username,
                onValueChange = { username = it }
            )

            Spacer(modifier = Modifier.height(24.dp))

            InfoInputField(
                label = "Password",
                value = mdp,
                onValueChange = { mdp = it },
                visualTransformation = PasswordVisualTransformation()
            )

            Spacer(modifier = Modifier.height(24.dp))

            InfoInputField(
                label = stringResource(id = R.string.personal_info_name),
                value = name,
                onValueChange = { input ->
                    // Autoriser seulement lettres et espaces
                    if (input.all { it.isLetter() || it.isWhitespace() }) {
                        name = input
                    }
                }
            )

            Spacer(modifier = Modifier.height(24.dp))

            InfoInputField(
                label = stringResource(id = R.string.personal_info_age),
                value = age,
                onValueChange = { age = it.filter { char -> char.isDigit() } },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            Spacer(modifier = Modifier.height(24.dp))

            InfoInputField(
                label = stringResource(id = R.string.personal_info_weight),
                value = weight,
                onValueChange = { input ->
                    if (input.isEmpty() || input.matches(Regex("""^\d*[.,]?\d*$"""))) {
                        weight = input.replace(',', '.')
                    }
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
            )

            Spacer(modifier = Modifier.height(24.dp))

            InfoInputField(
                label = stringResource(id = R.string.personal_info_height),
                value = height,
                onValueChange = { input ->
                    if (input.isEmpty() || input.matches(Regex("""^\d*[.,]?\d*$"""))) {
                        height = input.replace(',', '.')
                    }
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
            )
        }

        val isFormValid = username.isNotBlank() && mdp.isNotBlank() && name.isNotBlank() && age.isNotBlank() && weight.isNotBlank() && height.isNotBlank()

        Button(
            onClick = { onValidateClick(username, mdp, name, age, weight, height) },
            enabled = isFormValid,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.White,
                contentColor = Color.Black,
                disabledContainerColor = Color.Gray
            ),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(bottom = 16.dp)
                .height(56.dp)
        ) {
            Text(
                text = stringResource(id = R.string.personal_info_validate),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun InfoInputField(
    modifier: Modifier = Modifier,
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    visualTransformation: VisualTransformation = VisualTransformation.None
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = label,
            color = Color(0xFF9E9E9E),
            fontSize = 16.sp,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        TextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = TextFieldDefaults.colors(
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                focusedContainerColor = Color(0xFF12162B),
                unfocusedContainerColor = Color(0xFF12162B),
                cursorColor = Color.White,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            ),
            shape = RoundedCornerShape(8.dp),
            keyboardOptions = keyboardOptions,
            visualTransformation = visualTransformation,
            singleLine = true
        )
    }
}
