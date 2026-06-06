package com.example.application.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.os.Looper
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
    onContinueClick: (Boolean) -> Unit = {}
) {
    val context = LocalContext.current
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }
    
    // We use a specific list for OSM GeoPoints
    var pathPoints by remember { mutableStateOf(listOf<GeoPoint>()) }
    var totalDistance by remember { mutableFloatStateOf(initialCount) } // in km
    var currentSpeed by remember { mutableFloatStateOf(0f) } // in km/h
    var lastLocation by remember { mutableStateOf<Location?>(null) }
    
    var hasLocationPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        )
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted -> hasLocationPermission = isGranted }
    )

    LaunchedEffect(Unit) {
        if (!hasLocationPermission) {
            launcher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    // Dynamic font size for distance
    var distanceFontSize by remember { mutableStateOf(42.sp) }

    // Map logic with OSM
    if (hasLocationPermission) {
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

            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
            )

            onDispose {
                fusedLocationClient.removeLocationUpdates(locationCallback)
            }
        }
    }

    Box(modifier = modifier.fillMaxSize().background(Color.Black)) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Map Section (OpenStreetMap)
            Box(modifier = Modifier.weight(0.65f).fillMaxWidth()) {
                AndroidView(
                    modifier = Modifier.fillMaxSize(),
                    factory = { ctx ->
                        MapView(ctx).apply {
                            setTileSource(TileSourceFactory.MAPNIK)
                            setMultiTouchControls(true)
                            controller.setZoom(17.5)
                            
                            // Style the map (OSM doesn't have a native dark mode easily but we can invert it or use tiles)
                            // For consistency with black app, let's keep it clean
                            
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
            }

            // Stats Section
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.35f),
                color = Color.Black,
                shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxSize().padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
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
                        
                        Box(modifier = Modifier.width(1.dp).height(50.dp).background(Color.DarkGray))

                        StatItem(
                            label = stringResource(id = R.string.stat_speed),
                            value = "%.1f".format(currentSpeed),
                            unit = "km/h"
                        )
                    }

                    Button(
                        onClick = { onContinueClick(true) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Green, 
                            contentColor = Color.Black
                        ),
                        modifier = Modifier.fillMaxWidth().height(60.dp),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text(
                            text = stringResource(id = R.string.finish_run), 
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
        Text(text = label.uppercase(), color = Color.Gray, fontSize = 12.sp, fontWeight = FontWeight.Bold)
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
                fontSize = 14.sp,
                modifier = Modifier.padding(bottom = 6.dp)
            )
        }
    }
}
