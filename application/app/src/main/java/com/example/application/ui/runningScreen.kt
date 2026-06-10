package com.example.application.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.content.Intent
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.os.Looper
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.example.application.R
import com.google.android.gms.location.*
import kotlinx.coroutines.delay
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Polyline
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay

@SuppressLint("MissingPermission")
@Composable
fun RunningScreen(
    modifier: Modifier = Modifier,
    initialCount: Float = 0.0f,
    targetObjective: Float = 5.0f,
    onContinueClick: (Float) -> Unit = {}
) {
    val context = LocalContext.current
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }
    
    // We use a specific list for OSM GeoPoints
    var pathPoints by remember { mutableStateOf(listOf<GeoPoint>()) }
    var totalDistance by remember { mutableFloatStateOf(initialCount) } // in km
    var currentSpeed by remember { mutableFloatStateOf(0f) } // in km/h
    var lastLocation by remember { mutableStateOf<Location?>(null) }
    
    // Step counter state
    var initialSteps by remember { mutableFloatStateOf(-1f) }
    var sessionSteps by remember { mutableIntStateOf(0) }

    // Permissions handling
    var permissionsGranted by remember {
        val locationGranted = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        val activityGranted = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ContextCompat.checkSelfPermission(context, Manifest.permission.ACTIVITY_RECOGNITION) == PackageManager.PERMISSION_GRANTED
        } else true
        mutableStateOf(locationGranted && activityGranted)
    }

    // Check if GPS is enabled
    var isGpsEnabled by remember {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        mutableStateOf(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
    }

    // Listen for GPS status changes
    LaunchedEffect(Unit) {
        while(true) {
            val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
            delay(2000) // Check every 2 seconds
        }
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = { res ->
            permissionsGranted = res.values.all { it }
        }
    )

    LaunchedEffect(Unit) {
        if (!permissionsGranted) {
            val perms = mutableListOf(Manifest.permission.ACCESS_FINE_LOCATION)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                perms.add(Manifest.permission.ACTIVITY_RECOGNITION)
            }
            launcher.launch(perms.toTypedArray())
        }
    }

    // Dynamic font size for distance
    var distanceFontSize by remember { mutableStateOf(32.sp) }

    // Map and Location logic
    if (permissionsGranted) {
        // Location Updates
        DisposableEffect(fusedLocationClient) {
            val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 3000)
                .setMinUpdateIntervalMillis(1000)
                .build()

            val locationCallback = object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult) {
                    for (location in locationResult.locations) {
                        val newPoint = GeoPoint(location.latitude, location.longitude)
                        currentSpeed = location.speed * 3.6f
                        lastLocation?.let { last ->
                            val distanceInMeters = last.distanceTo(location)
                            totalDistance += distanceInMeters / 1000f
                        }
                        lastLocation = location
                        pathPoints = pathPoints + newPoint
                    }
                }
            }
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())
            onDispose { fusedLocationClient.removeLocationUpdates(locationCallback) }
        }

        // Step Counter logic
        DisposableEffect(Unit) {
            val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
            val stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
            
            val stepListener = object : SensorEventListener {
                override fun onSensorChanged(event: SensorEvent) {
                    if (event.sensor.type == Sensor.TYPE_STEP_COUNTER) {
                        if (initialSteps < 0) initialSteps = event.values[0]
                        sessionSteps = (event.values[0] - initialSteps).toInt()
                    }
                }
                override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
            }

            stepSensor?.let {
                sensorManager.registerListener(stepListener, it, SensorManager.SENSOR_DELAY_UI)
            }

            onDispose {
                sensorManager.unregisterListener(stepListener)
            }
        }
    }

    Box(modifier = modifier.fillMaxSize().background(Color.Black)) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Map Section or GPS Message
            Box(modifier = Modifier.weight(0.6f).fillMaxWidth()) {
                if (isGpsEnabled) {
                    AndroidView(
                        modifier = Modifier.fillMaxSize(),
                        factory = { ctx ->
                            MapView(ctx).apply {
                                setTileSource(TileSourceFactory.MAPNIK)
                                setMultiTouchControls(true)
                                controller.setZoom(17.5)
                                val myLocationOverlay = MyLocationNewOverlay(this)
                                myLocationOverlay.enableMyLocation()
                                myLocationOverlay.enableFollowLocation()
                                overlays.add(myLocationOverlay)
                            }
                        },
                        update = { view ->
                            if (pathPoints.size > 1) {
                                val line = view.overlays.filterIsInstance<Polyline>().firstOrNull() 
                                    ?: Polyline().apply {
                                        outlinePaint.color = android.graphics.Color.parseColor("#1565C0")
                                        outlinePaint.strokeWidth = 14f
                                        view.overlays.add(this)
                                    }
                                line.setPoints(pathPoints)
                                view.controller.animateTo(pathPoints.last())
                                view.invalidate()
                            }
                        }
                    )
                } else {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "📍 Location Disabled",
                            color = Color.White,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Please enable your GPS to see the map and track your run.",
                            color = Color.Gray,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                            fontSize = 16.sp
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                        Button(
                            onClick = {
                                context.startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1565C0))
                        ) {
                            Text("ENABLE GPS", color = Color.White)
                        }
                    }
                }
            }

            // Stats Section
            Surface(
                modifier = Modifier.fillMaxWidth().weight(0.4f),
                color = Color.Black,
                shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxSize().padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "GOAL : %.1f km".format(targetObjective),
                        color = Color.Gray,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        StatItem(
                            label = stringResource(id = R.string.stat_distance),
                            value = "%.2f".format(totalDistance),
                            unit = "km",
                            fontSize = distanceFontSize,
                            onOverflow = { distanceFontSize = (distanceFontSize.value * 0.9f).sp }
                        )
                        
                        Box(modifier = Modifier.width(1.dp).height(40.dp).background(Color.DarkGray))

                        StatItem(
                            label = "STEPS",
                            value = sessionSteps.toString(),
                            unit = "steps",
                            fontSize = 32.sp
                        )

                        Box(modifier = Modifier.width(1.dp).height(40.dp).background(Color.DarkGray))

                        StatItem(
                            label = stringResource(id = R.string.stat_speed),
                            value = "%.1f".format(currentSpeed),
                            unit = "km/h",
                            fontSize = 32.sp
                        )
                    }

                    Button(
                        onClick = { onContinueClick(totalDistance) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (totalDistance >= targetObjective) Color.Green else Color.White, 
                            contentColor = Color.Black
                        ),
                        modifier = Modifier.fillMaxWidth().height(60.dp),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text(
                            text = if (totalDistance >= targetObjective) 
                                stringResource(id = R.string.finish_run) 
                            else "SAVE & QUIT", 
                            fontWeight = FontWeight.ExtraBold, 
                            fontSize = 20.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun StatItem(
    label: String, 
    value: String, 
    unit: String, 
    fontSize: androidx.compose.ui.unit.TextUnit = 42.sp,
    onOverflow: () -> Unit = {}
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = label.uppercase(), color = Color.Gray, fontSize = 11.sp, fontWeight = FontWeight.Bold)
        Row(verticalAlignment = Alignment.Bottom) {
            Text(
                text = value,
                color = Color.White,
                fontSize = fontSize,
                fontWeight = FontWeight.Black,
                maxLines = 1,
                onTextLayout = { if (it.hasVisualOverflow) onOverflow() }
            )
            Text(
                text = " $unit",
                color = Color.Gray,
                fontSize = 12.sp,
                modifier = Modifier.padding(bottom = 4.dp)
            )
        }
    }
}
