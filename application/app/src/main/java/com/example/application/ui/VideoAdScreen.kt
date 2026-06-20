package com.example.application.ui

import android.net.Uri
import android.widget.VideoView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.application.R
import com.example.application.ui.bdd.Ad

@Composable
fun VideoAdScreen(
    ad: Ad?,
    onAdFinished: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black),
        contentAlignment = Alignment.Center
    ) {
        AndroidView(
            factory = { ctx ->
                VideoView(ctx).apply {
                    val uri = if (ad?.videoUri != null) {
                        Uri.parse(ad.videoUri)
                    } else {
                        // Fallback to default raw resource if no partner ad is provided
                        Uri.parse("android.resource://${ctx.packageName}/${R.raw.publicite}")
                    }
                    
                    setVideoURI(uri)
                    setOnPreparedListener { 
                        it.isLooping = true
                        it.start() 
                    }
                    setOnCompletionListener { onAdFinished() }
                    setOnErrorListener { _, _, _ -> 
                        onAdFinished()
                        true 
                    }
                }
            },
            modifier = Modifier.fillMaxSize()
        )

        // Overlay for Title and Message
        Column(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(24.dp)
                .background(Color.Black.copy(alpha = 0.6f), RoundedCornerShape(8.dp))
                .padding(12.dp)
        ) {
            Text(
                text = ad?.title ?: "Special Offer",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )
            Text(
                text = ad?.content ?: "Check out this content while we save your progress!",
                color = Color.White.copy(alpha = 0.9f),
                fontSize = 14.sp
            )
        }

        TextButton(
            onClick = onAdFinished,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 32.dp, end = 16.dp)
        ) {
            Text("Skip Ad >", color = Color.White.copy(alpha = 0.7f), fontWeight = FontWeight.Bold)
        }
    }
}
