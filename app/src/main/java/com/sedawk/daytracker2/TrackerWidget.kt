package com.sedawk.daytracker2

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.ContentScale
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.size
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider

class TrackerWidget : GlanceAppWidget() {
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            TrackerContent()
        }
    }

    private val trackerTextStyle = TextStyle(
        color = ColorProvider(Color.White),
        fontWeight = FontWeight.Medium,
        fontSize = 50.sp // Optional font size adjustment
    )

    @Composable
    private fun TrackerContent() {
        Box(modifier = GlanceModifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Image(
                provider = ImageProvider(R.drawable.widget_5),
                contentDescription = "App widget background image",
                contentScale = ContentScale.Crop,
                modifier = GlanceModifier.fillMaxSize()
            )

            Box(
                modifier = GlanceModifier.size(100.dp)
                    .cornerRadius(50.dp).background(Color(0x66000000)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "6", style = trackerTextStyle)
            }
        }
    }
}