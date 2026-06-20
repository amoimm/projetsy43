package com.example.application.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.application.R

@Composable
fun PartnerInfoScreen(
    modifier: Modifier = Modifier,
    initialUsername: String = "",
    initialMdp: String = "",
    initialLastName: String = "",
    initialFirstName: String = "",
    initialCompany: String = "",
    onBackClick: () -> Unit = {},
    onValidateClick: (username: String, mdp: String, lastName: String, firstName: String, company: String) -> Unit = { _, _, _, _, _ -> }
) {
    var username by remember(initialUsername) { mutableStateOf(initialUsername) }
    var mdp by remember(initialMdp) { mutableStateOf(initialMdp) }
    var lastName by remember(initialLastName) { mutableStateOf(initialLastName) }
    var firstName by remember(initialFirstName) { mutableStateOf(initialFirstName) }
    var company by remember(initialCompany) { mutableStateOf(initialCompany) }

    val isFormValid = username.isNotBlank() && mdp.isNotBlank() && lastName.isNotBlank() && firstName.isNotBlank() && company.isNotBlank()

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
            Text(
                text = stringResource(id = R.string.partner_info_title),
                color = Color.White,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(40.dp))

            PartnerInputField(
                label = "Username",
                value = username,
                onValueChange = { username = it }
            )

            Spacer(modifier = Modifier.height(24.dp))

            PartnerInputField(
                label = "Password",
                value = mdp,
                onValueChange = { mdp = it },
                visualTransformation = PasswordVisualTransformation()
            )

            Spacer(modifier = Modifier.height(24.dp))

            PartnerInputField(
                label = stringResource(id = R.string.partner_info_lastname),
                value = lastName,
                onValueChange = { input ->
                    if (input.all { it.isLetter() || it.isWhitespace() }) {
                        lastName = input
                    }
                }
            )

            Spacer(modifier = Modifier.height(24.dp))

            PartnerInputField(
                label = stringResource(id = R.string.partner_info_firstname),
                value = firstName,
                onValueChange = { input ->
                    if (input.all { it.isLetter() || it.isWhitespace() }) {
                        firstName = input
                    }
                }
            )

            Spacer(modifier = Modifier.height(24.dp))

            PartnerInputField(
                label = stringResource(id = R.string.partner_info_company),
                value = company,
                onValueChange = { company = it }
            )
        }

        Button(
            onClick = { onValidateClick(username, mdp, lastName, firstName, company) },
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
fun PartnerInputField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    visualTransformation: androidx.compose.ui.text.input.VisualTransformation = androidx.compose.ui.text.input.VisualTransformation.None
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
            visualTransformation = visualTransformation,
            singleLine = true
        )
    }
}
