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
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
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
    
    val pathPoints = remember { mutableStateListOf<GeoPoint>() }
    var totalDistance by remember { mutableFloatStateOf(initialCount) }
    var currentSpeed by remember { mutableFloatStateOf(0f) }
    var lastLocation by remember { mutableStateOf<Location?>(null) }
    
    var sessionSteps by remember { mutableIntStateOf(0) }
    var initialSteps by remember { mutableFloatStateOf(-1f) }

    var permissionsGranted by remember {
        val locationGranted = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        val activityGranted = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ContextCompat.checkSelfPermission(context, Manifest.permission.ACTIVITY_RECOGNITION) == PackageManager.PERMISSION_GRANTED
        } else true
        mutableStateOf(locationGranted && activityGranted)
    }

    var isGpsEnabled by remember {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as? LocationManager
        mutableStateOf(locationManager?.isProviderEnabled(LocationManager.GPS_PROVIDER) ?: true)
    }

    LaunchedEffect(Unit) {
        while(true) {
            val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as? LocationManager
            isGpsEnabled = locationManager?.isProviderEnabled(LocationManager.GPS_PROVIDER) ?: false
            delay(3000)
        }
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = { res -> permissionsGranted = res.values.all { it } }
    )

    LaunchedEffect(Unit) {
        if (!permissionsGranted) {
            val perms = mutableListOf(Manifest.permission.ACCESS_FINE_LOCATION)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) perms.add(Manifest.permission.ACTIVITY_RECOGNITION)
            launcher.launch(perms.toTypedArray())
        }
    }

    if (permissionsGranted) {
        DisposableEffect(fusedLocationClient) {
            val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 2000)
                .setMinUpdateIntervalMillis(1000)
                .build()

            val locationCallback = object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult) {
                    for (location in locationResult.locations) {
                        val newPoint = GeoPoint(location.latitude, location.longitude)
                        currentSpeed = location.speed * 3.6f
                        
                        lastLocation?.let { last ->
                            val distanceInMeters = last.distanceTo(location)
                            if (distanceInMeters > 1.5) {
                                totalDistance += distanceInMeters / 1000f
                                pathPoints.add(newPoint)
                            }
                        } ?: run {
                            pathPoints.add(newPoint)
                        }
                        lastLocation = location
                    }
                }
            }
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())
            onDispose { fusedLocationClient.removeLocationUpdates(locationCallback) }
        }

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
            stepSensor?.let { sensorManager.registerListener(stepListener, it, SensorManager.SENSOR_DELAY_UI) }
            onDispose { sensorManager.unregisterListener(stepListener) }
        }
    }

    Box(modifier = modifier.fillMaxSize().background(Color.Black)) {
        Column(modifier = Modifier.fillMaxSize()) {
            Box(modifier = Modifier.weight(0.6f).fillMaxWidth()) {
                if (isGpsEnabled) {
                    AndroidView(
                        modifier = Modifier.fillMaxSize(),
                        factory = { ctx ->
                            MapView(ctx).apply {
                                setTileSource(TileSourceFactory.MAPNIK)
                                setMultiTouchControls(true)
                                controller.setZoom(18.5)
                                
                                val myLocationOverlay = MyLocationNewOverlay(GpsMyLocationProvider(ctx), this)
                                myLocationOverlay.enableMyLocation()
                                myLocationOverlay.enableFollowLocation()
                                myLocationOverlay.isDrawAccuracyEnabled = true
                                overlays.add(myLocationOverlay)
                                
                                val line = Polyline().apply {
                                    outlinePaint.color = android.graphics.Color.parseColor("#00E676")
                                    outlinePaint.strokeWidth = 14f
                                }
                                overlays.add(line)
                            }
                        },
                        update = { view ->
                            val line = view.overlays.filterIsInstance<Polyline>().firstOrNull()
                            if (line != null && pathPoints.size >= 2) {
                                line.setPoints(pathPoints.toList())
                                pathPoints.lastOrNull()?.let { 
                                    view.controller.animateTo(it)
                                }
                                view.invalidate()
                            }
                        }
                    )
                } else {
                    GPSDisabledMessage(context)
                }
            }

            Surface(
                modifier = Modifier.fillMaxWidth().weight(0.4f),
                color = Color.Black,
                shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)
            ) {
                RunningStatsLayout(targetObjective, totalDistance, sessionSteps, currentSpeed) {
                    onContinueClick(totalDistance)
                }
            }
        }
    }
}

@Composable
fun GPSDisabledMessage(context: Context) {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("📍 Location Disabled", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        Text("Please enable your GPS to see the map and track your run.", color = Color.Gray, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
        Spacer(modifier = Modifier.height(24.dp))
        Button(onClick = { context.startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)) }) {
            Text("ENABLE GPS")
        }
    }
}

@Composable
fun RunningStatsLayout(target: Float, dist: Float, steps: Int, speed: Float, onFinish: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Text("GOAL : %.1f km".format(target), color = Color.Gray, fontWeight = FontWeight.Bold, fontSize = 14.sp)

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            StatItem(stringResource(id = R.string.stat_distance), "%.2f".format(dist), "km")
            StatItem("Steps", steps.toString(), "steps")
            StatItem(stringResource(id = R.string.stat_speed), "%.1f".format(speed), "km/h")
        }

        Button(
            onClick = onFinish,
            colors = ButtonDefaults.buttonColors(
                containerColor = if (dist >= target) Color(0xFF00E676) else Color.White, 
                contentColor = Color.Black
            ),
            modifier = Modifier.fillMaxWidth().height(60.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text(if (dist >= target) stringResource(id = R.string.finish_run) else "SAVE & QUIT", fontWeight = FontWeight.Black, fontSize = 18.sp)
        }
    }
}

@Composable
fun StatItem(label: String, value: String, unit: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(label.uppercase(), color = Color.Gray, fontSize = 11.sp, fontWeight = FontWeight.Bold)
        Row(verticalAlignment = Alignment.Bottom) {
            Text(value, color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.Black)
            Text(" $unit", color = Color.Gray, fontSize = 12.sp, modifier = Modifier.padding(bottom = 4.dp))
        }
    }
}
