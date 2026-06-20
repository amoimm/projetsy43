package com.example.application.ui

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.media.MediaPlayer
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.application.R
import kotlinx.coroutines.delay
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.random.Random

private const val COOLDOWN_MS = 500L

enum class WorkoutState {
    COUNTDOWN, TRAINING
}

@Composable
fun BodyweightScreen(
    modifier: Modifier = Modifier,
    initialCount: Int = 0,
    targetObjective: Int,
    exerciseType: String,
    threshold: Float = 0.25f,
    onContinueClick: (Int) -> Unit = {}
) {
    var count by rememberSaveable { mutableIntStateOf(initialCount) }
    var fontChange by remember(count) { mutableStateOf(120.sp) }

    var workoutState by rememberSaveable { mutableStateOf(WorkoutState.COUNTDOWN) }
    var countdownValue by rememberSaveable { mutableStateOf("5") }

    var isCelebrating by rememberSaveable { mutableStateOf(false) }
    var hasCelebrated by rememberSaveable { mutableStateOf(false) }

    val context = LocalContext.current

    val playPushupSound = {
        try {
            MediaPlayer.create(context, R.raw.pompe_bruit)?.start()
        } catch (e: Exception) { e.printStackTrace() }
    }

    LaunchedEffect(count) {
        if (count >= targetObjective && !hasCelebrated) {
            hasCelebrated = true
            isCelebrating = true
            try {
                MediaPlayer.create(context, R.raw.goal)?.start()
            } catch (e: Exception) { e.printStackTrace() }
            delay(5000)
            isCelebrating = false
        }
    }

    LaunchedEffect(Unit) {
        if (workoutState != WorkoutState.COUNTDOWN) return@LaunchedEffect
        val playSound = { resId: Int ->
            try { MediaPlayer.create(context, resId)?.start() } catch (e: Exception) { e.printStackTrace() }
        }
        delay(500)
        countdownValue = "5"; playSound(R.raw.bip); delay(1000)
        countdownValue = "4"; playSound(R.raw.bip); delay(1000)
        countdownValue = "3"; playSound(R.raw.bip); delay(1000)
        countdownValue = "2"; playSound(R.raw.bip); delay(1000)
        countdownValue = "1"; playSound(R.raw.bip); delay(1000)
        countdownValue = "GO !"; playSound(R.raw.go); delay(800)
        workoutState = WorkoutState.TRAINING
    }

    DisposableEffect(workoutState) {
        if (workoutState != WorkoutState.TRAINING) return@DisposableEffect onDispose {}

        val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val accelSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

        var lastRepTime = 0L

        // Gravity
        val gravity = FloatArray(3)
        val alpha = 0.8f
        var gravityReady = false

        // Vertical position
        var velocity = 0f
        var vertPos = 0f
        var lastTimeNs = 0L
        val driftAlpha = 0.998f

        //reps is between top and down
        var peakMin = 0f
        var peakMax = 0f

        val ampThresh = 0.06f * threshold

        // for pullups
        val invertedMode = exerciseType.contains("Pull", ignoreCase = true)

        val sensorListener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent?) {
                if (event == null || event.sensor.type != Sensor.TYPE_ACCELEROMETER) return

                if (!gravityReady) {
                    gravity[0] = event.values[0]
                    gravity[1] = event.values[1]
                    gravity[2] = event.values[2]
                    gravityReady = true
                    lastTimeNs = event.timestamp
                    return
                }
                gravity[0] = alpha * gravity[0] + (1 - alpha) * event.values[0]
                gravity[1] = alpha * gravity[1] + (1 - alpha) * event.values[1]
                gravity[2] = alpha * gravity[2] + (1 - alpha) * event.values[2]

                val gMag = sqrt(
                    gravity[0] * gravity[0] +
                            gravity[1] * gravity[1] +
                            gravity[2] * gravity[2]
                )
                if (gMag < 0.5f) return

                // Vertical Acceleration
                val linX = event.values[0] - gravity[0]
                val linY = event.values[1] - gravity[1]
                val linZ = event.values[2] - gravity[2]

                val vertAccel = -(linX * gravity[0] + linY * gravity[1] + linZ * gravity[2]) / gMag

                // position
                val dt = (event.timestamp - lastTimeNs) * 1e-9f
                lastTimeNs = event.timestamp
                if (dt <= 0f || dt > 0.1f) return

                velocity = (velocity + vertAccel * dt) * driftAlpha
                vertPos  = (vertPos  + velocity   * dt) * driftAlpha

                // update top and bottom
                if (vertPos < peakMin) peakMin = vertPos
                if (vertPos > peakMax) peakMax = vertPos

                val amplitude = peakMax - peakMin

                if (amplitude >= ampThresh) {
                    val isReturning = if (!invertedMode) {
                        vertPos > peakMin + ampThresh * 0.5f
                    } else {
                        vertPos < peakMax - ampThresh * 0.5f
                    }

                    if (isReturning) {
                        val now = System.currentTimeMillis()
                        if (now - lastRepTime >= COOLDOWN_MS) {
                            count++
                            playPushupSound()
                            lastRepTime = now
                        }
                        // Reset complet pour la prochaine rep
                        velocity = 0f
                        vertPos  = 0f
                        peakMin  = 0f
                        peakMax  = 0f
                    }
                }
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
        }

        accelSensor?.let {
            sensorManager.registerListener(sensorListener, it, SensorManager.SENSOR_DELAY_GAME)
        }

        onDispose { sensorManager.unregisterListener(sensorListener) }
    }

    Box(
        modifier = modifier.fillMaxSize().background(Color.Black).padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        if (workoutState == WorkoutState.COUNTDOWN) {
            Text(
                text = countdownValue,
                color = if (countdownValue == "GO !") Color.Green else Color.White,
                fontSize = 120.sp,
                fontWeight = FontWeight.Black
            )
        }

        if (workoutState == WorkoutState.TRAINING) {
            if (isCelebrating) { FireworkEffect() }

            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.weight(1f))

                Text(
                    text = if (count >= targetObjective) {
                        stringResource(id = R.string.goal_achieved)
                    } else {
                        "$exerciseType done"
                    },
                    color = if (count >= targetObjective) Color.Yellow else Color.Gray,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = count.toString(),
                    color = Color.White,
                    fontSize = fontChange,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    onTextLayout = { if (it.hasVisualOverflow) fontChange = (fontChange.value * 0.9f).sp }
                )

                Text(
                    text = "Objective : $targetObjective",
                    color = Color.Gray,
                    fontSize = 18.sp
                )

                Spacer(modifier = Modifier.weight(2f))
            }

            Column(
                modifier = Modifier.fillMaxWidth().align(Alignment.BottomCenter).padding(bottom = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (count >= targetObjective) {
                    Button(
                        onClick = { onContinueClick(count) },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Green, contentColor = Color.Black),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth().height(56.dp)
                    ) {
                        Text(text = stringResource(id = R.string.validate_session), fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    }
                } else {
                    Button(
                        onClick = {
                            count++
                            playPushupSound()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = Color.Black),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth().height(56.dp)
                    ) {
                        Text(text = "Add manually ${exerciseType.uppercase()}", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    TextButton(onClick = { onContinueClick(count) }) {
                        Text(text = "SAVE & QUIT", color = Color.White.copy(alpha = 0.6f), fontSize = 16.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun FireworkEffect() {
    val infiniteTransition = rememberInfiniteTransition(label = "fireworks")
    val progress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ), label = "progress"
    )

    val particles = remember {
        List(3) {
            List(20) {
                val angle = Random.nextFloat() * 2 * Math.PI
                val speed = Random.nextFloat() * 300f + 100f
                Pair(angle, speed)
            }
        }
    }
    val colors = listOf(Color.Cyan, Color.Magenta, Color.Yellow, Color.Green, Color.Red)

    Canvas(modifier = Modifier.fillMaxSize()) {
        val centers = listOf(
            Offset(size.width * 0.3f, size.height * 0.4f),
            Offset(size.width * 0.7f, size.height * 0.3f),
            Offset(size.width * 0.5f, size.height * 0.5f)
        )

        centers.forEachIndexed { index, center ->
            val color = colors[(index + (progress * 5).toInt()) % colors.size]
            particles[index].forEach { (angle, speed) ->
                val distance = speed * progress
                val x = center.x + (cos(angle) * distance).toFloat()
                val y = center.y + (sin(angle) * distance).toFloat() + (progress * progress * 100f)
                drawCircle(
                    color = color.copy(alpha = 1f - progress),
                    radius = 6.dp.toPx() * (1f - progress),
                    center = Offset(x, y)
                )
            }
        }
    }
}