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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.application.R
import com.example.application.ui.theme.ApplicationTheme

@Composable
fun PartnerInfoScreen(
    modifier: Modifier = Modifier,
    initialLastName: String = "",
    initialFirstName: String = "",
    initialCompany: String = "",
    onBackClick: () -> Unit = {},
    onValidateClick: (String, String, String) -> Unit = { _, _, _ -> }
) {
    // Keyed remember ensures fields update when initial data changes (e.g. loaded from memory)
    var lastName by remember(initialLastName) { mutableStateOf(initialLastName) }
    var firstName by remember(initialFirstName) { mutableStateOf(initialFirstName) }
    var company by remember(initialCompany) { mutableStateOf(initialCompany) }

    // Mandatory fields check
    val isFormValid = lastName.isNotBlank() && firstName.isNotBlank() && company.isNotBlank()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(horizontal = 24.dp, vertical = 32.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White
                )
            }

            Text(
                text = stringResource(id = R.string.partner_info_title),
                color = Color.White,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(40.dp))

            PartnerInputField(
                label = stringResource(id = R.string.partner_info_lastname),
                value = lastName,
                onValueChange = { lastName = it }
            )

            Spacer(modifier = Modifier.height(24.dp))

            PartnerInputField(
                label = stringResource(id = R.string.partner_info_firstname),
                value = firstName,
                onValueChange = { firstName = it }
            )

            Spacer(modifier = Modifier.height(24.dp))

            PartnerInputField(
                label = stringResource(id = R.string.partner_info_company),
                value = company,
                onValueChange = { company = it }
            )
        }

        Button(
            onClick = { onValidateClick(lastName, firstName, company) },
            enabled = isFormValid, // Confirm only possible if all fields are filled
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
    modifier: Modifier = Modifier
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
            singleLine = true
        )
    }
}
