package com.example.application.ui

import android.net.Uri
import android.widget.VideoView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.viewinterop.AndroidView
import com.example.application.R
import com.example.application.ui.theme.ApplicationTheme

@Composable
fun PushupScreen(
    modifier: Modifier = Modifier,
    initialCount: Int = 0,
    onContinueClick: () -> Unit = {}
) {
    var count by remember { mutableStateOf(initialCount) }
    // Use of a variable to adapt the font size
    var fontChange by remember(count) { mutableStateOf(256.sp) }
    var showAd by remember { mutableStateOf(false) }

    if (showAd) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black),
            contentAlignment = Alignment.Center
        ) {
            AndroidView(
                factory = { context ->
                    VideoView(context).apply {
                        val videoPath = "android.resource://${context.packageName}/${R.raw.publicite}"
                        setVideoURI(Uri.parse(videoPath))
                        setOnPreparedListener { 
                            it.start() 
                        }
                        setOnCompletionListener {
                            onContinueClick()
                        }
                    }
                },
                update = { videoView ->
                    // VideoView is started in onPreparedListener
                },
                modifier = Modifier.fillMaxSize()
            )
            
            // Optionnel : Bouton pour passer la pub
            TextButton(
                onClick = onContinueClick,
                modifier = Modifier.align(Alignment.TopEnd).padding(16.dp)
            ) {
                Text("Passer la pub >", color = Color.White)
            }
        }
    } else {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(Color.Black)
                .padding(24.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.weight(1f))

                Text(
                    text = stringResource(id = R.string.pushup_done),
                    color = Color.Gray,
                    fontSize = 26.sp,
                )

                Text(
                    text = count.toString(),
                    color = Color.White,
                    fontSize = fontChange,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    softWrap = false,
                    onTextLayout = { textLayoutResult ->
                        //reduce the font size of the text exceed one line
                        if (textLayoutResult.hasVisualOverflow) {
                            fontChange = (fontChange.value * 0.9f).sp
                        }
                    }
                )

                Spacer(modifier = Modifier.weight(2f))
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Button(
                    onClick = { count++ },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White,
                        contentColor = Color.Black
                    ),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth().height(56.dp)
                ) {
                    Text(
                        text = stringResource(id = R.string.add_pushup),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                TextButton(
                    onClick = { showAd = true }
                ) {
                    Text(
                        text = stringResource(id = R.string.finish_session),
                        color = Color.Gray,
                        fontSize = 16.sp
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PushupScreenPreview() {
    ApplicationTheme {
        PushupScreen(initialCount = 0)
    }
}
