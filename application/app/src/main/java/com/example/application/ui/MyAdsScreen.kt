package com.example.application.ui

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.application.ui.bdd.Ad

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyAdsScreen(
    ads: List<Ad>,
    onBackClick: () -> Unit,
    onAddAdClick: () -> Unit,
    onDeleteAdClick: (Ad) -> Unit,
    onPlayAdClick: (Ad) -> Unit,
    modifier: Modifier = Modifier
) {
    val groupedAds = ads.groupBy { it.triggerLocation }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Advertisements", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Black)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddAdClick,
                containerColor = Color(0xFF1565C0),
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Ad")
            }
        },
        containerColor = Color.Black
    ) { padding ->
        if (ads.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text("No ads created yet.", color = Color.Gray)
            }
        } else {
            LazyColumn(
                modifier = modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(vertical = 16.dp)
            ) {
                groupedAds.forEach { (location, adsInLocation) ->
                    item {
                        Text(
                            text = when(location) {
                                "AFTER_LIST" -> "After Creating List"
                                "AFTER_TIME" -> "After Time Spent"
                                "AFTER_PUSHUP" -> "After Push-ups"
                                "AFTER_RUNNING" -> "After Running"
                                else -> location
                            },
                            color = Color(0xFF90CAF9),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }
                    items(adsInLocation) { ad ->
                        AdItem(
                            ad = ad, 
                            onDeleteClick = { onDeleteAdClick(ad) },
                            onPlayClick = { onPlayAdClick(ad) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AdItem(ad: Ad, onDeleteClick: () -> Unit, onPlayClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = ad.title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = ad.content, color = Color.Gray, fontSize = 14.sp)
                if (ad.videoUri != null) {
                    Text(text = "Video attached", color = Color(0xFF4CAF50), fontSize = 12.sp)
                }
            }
            Row {
                if (ad.videoUri != null) {
                    IconButton(onClick = onPlayClick) {
                        Icon(Icons.Default.PlayArrow, contentDescription = "Play Video", tint = Color.White)
                    }
                }
                IconButton(onClick = onDeleteClick) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color(0xFFEF5350))
                }
            }
        }
    }
}
