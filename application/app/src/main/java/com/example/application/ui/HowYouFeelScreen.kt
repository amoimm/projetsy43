package com.example.application.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
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
    // Mutable variable to remember the fill percentage
    var fillPercentage by remember { mutableStateOf(0.5f) }

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

            Spacer(modifier = Modifier.height(30.dp))

            // Interactive slider
            Box(
                modifier = Modifier
                    .width(100.dp)
                    .height(400.dp)
                    .clip(RoundedCornerShape(90.dp))
                    .background(Color(0xFF12162B)) // Couleur de fond (partie vide)
                    .pointerInput(Unit) {
                        detectTapGestures { offset ->
                            fillPercentage = (1f - (offset.y / size.height)).coerceIn(0f, 1f)
                        }
                    }
                    .pointerInput(Unit) {
                        detectDragGestures { change, _ ->
                            val newFill = (1f - (change.position.y / size.height)).coerceIn(0f, 1f)
                            fillPercentage = newFill
                        }
                    }
            ) {
                // 1. Fixed gradiant background
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    Color(0xFF42A5F5), // Blue (Top)
                                    Color(0xFF66BB6A), // Green
                                    Color(0xFFFFEE58), // Yellow
                                    Color(0xFFFFA726), // Orange
                                    Color(0xFFEF5350)  // Red (Bottom)
                                )
                            )
                        )
                )

                // 2. Hide the empty part (shaded)
                // This box hides the top part of the gradient based on the percentage
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(1f - fillPercentage)
                        .align(Alignment.TopCenter)
                        .background(Color(0xFF12162B))
                )
            }

            Spacer(modifier = Modifier.height(30.dp))

            Text(
                text = stringResource(id = R.string.how_you_feel_high),
                modifier = Modifier.fillMaxWidth(),
                color = Color.Gray,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Text(
                text = "${(fillPercentage * 100).toInt()} %",
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

        Button(
            onClick = onValidateClick,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.White,
                contentColor = Color.Black
            ),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(bottom = 16.dp)
                .height(56.dp)
        ) {
            Text(
                text = stringResource(id = R.string.validate),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
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
