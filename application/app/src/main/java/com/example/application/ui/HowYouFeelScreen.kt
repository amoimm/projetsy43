package com.example.application.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
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
import com.example.application.UserProfile
import com.example.application.ui.bdd.ToDoList
import com.example.application.ui.theme.ApplicationTheme
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun generateTodolist(motivation: Float, maxpushups: Int, maxpullups: Int, maxsquats: Int, maxkm: Double): ToDoList {
    val ratio = 0.3f + (motivation * 0.7f)

    val pushupGoal = (maxpushups * ratio).toInt().coerceAtLeast(5)
    val pullupGoal = (maxpullups * ratio).toInt().coerceAtLeast(2)
    val squatGoal = (maxsquats * ratio).toInt().coerceAtLeast(10)
    val runningGoal = String.format(Locale.US, "%.1f", (maxkm * ratio).coerceAtLeast(1.0))

    val title = when {
        motivation > 0.8f -> "You're the man !"
        motivation > 0.5f -> "A better you !"
        else -> "Stay in movement !"
    }

    val activities = "Pushup,$pushupGoal,false;Pullup,$pullupGoal,false;Squat,$squatGoal,false;Running,$runningGoal,false"

    return ToDoList(
        title = title,
        date = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date()),
        activitiesJson = activities
    )
}

@Composable
fun HowYouFeelScreen(
    modifier: Modifier = Modifier,
    userProfile: UserProfile?,
    onValidateClick: (ToDoList, Float) -> Unit
) {
    var fillPercentage by remember(userProfile?.lastMotivationLevel) {
        mutableStateOf(userProfile?.lastMotivationLevel ?: 0.5f) 
    }

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
                color = Color.White,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = stringResource(id = R.string.how_you_feel_description),
                color = Color.Gray,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(30.dp))

            Box(
                modifier = Modifier
                    .width(100.dp)
                    .height(400.dp)
                    .clip(RoundedCornerShape(90.dp))
                    .background(Color(0xFF12162B))
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
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    Color(0xFF42A5F5),
                                    Color(0xFF66BB6A),
                                    Color(0xFFFFEE58),
                                    Color(0xFFFFA726),
                                    Color(0xFFEF5350)
                                )
                            )
                        )
                )

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
                color = Color.Gray,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Text(
                text = "${(fillPercentage * 100).toInt()} %",
                color = Color.White,
                fontSize = 28.sp,
                textAlign = TextAlign.Center
            )

            Text(
                text = stringResource(id = R.string.how_you_feel_low),
                color = Color.Gray,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        }

        Button(
            onClick = {
                val maxPush = userProfile?.maxPushups?.toIntOrNull() ?: 15
                val maxPull = userProfile?.maxPullups?.toIntOrNull() ?: 5
                val maxS = userProfile?.maxSquats?.toIntOrNull() ?: 20
                val maxK = userProfile?.maxRunningKm?.toDoubleOrNull() ?: 3.0

                val autoList = generateTodolist(fillPercentage, maxPush, maxPull, maxS, maxK)

                onValidateClick(autoList, fillPercentage)
            },
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
