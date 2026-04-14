package com.example.application.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.application.R
import com.example.application.ui.theme.ApplicationTheme

@Composable
fun HowYouFeelScreen(
    modifier: Modifier = Modifier,
    onValidateClick: () -> Unit = {}
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(horizontal = 24.dp, vertical = 32.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(id = R.string.how_you_feel_title),
                modifier = Modifier.fillMaxWidth(),
                color = Color.White,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = stringResource(id = R.string.how_you_feel_description),
                modifier = Modifier.fillMaxWidth(),
                color = Color.Gray,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(50.dp))

            Box(
                modifier = Modifier
                    .width(100.dp)
                    .height(500.dp)
                    .background(
                        color = Color(0xFF1E1E1E),
                        shape = RoundedCornerShape(90.dp)
                    )
                    .fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(50.dp))

            Text(
                text = stringResource(id = R.string.how_you_feel_high),
                modifier = Modifier.fillMaxWidth(),
                color = Color.Gray,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Text(
                text = "50 %",
                modifier = Modifier.fillMaxWidth(),
                color = Color.White,
                fontSize = 28.sp,
                textAlign = TextAlign.Center
            )

            Text(
                text = stringResource(id = R.string.how_you_feel_low),
                modifier = Modifier.fillMaxWidth(),
                color = Color.Gray,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun HowYouFeelScreenPreview() {
    ApplicationTheme {
        HowYouFeelScreen()
    }
}
