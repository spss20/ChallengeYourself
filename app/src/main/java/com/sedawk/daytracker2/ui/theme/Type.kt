package com.sedawk.daytracker2.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.sedawk.daytracker2.R

// Set of Material typography styles to start with

val Typography = Typography(
    bodyLarge = TextStyle(
        fontFamily = FontFamily(Font(R.font.nunito_bold)),
        fontWeight = FontWeight.Normal,
        fontSize = 18.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp,
        color = mainColor
    ),
    bodyMedium = TextStyle(
        fontFamily = FontFamily(Font(R.font.nunito)),
        fontWeight = FontWeight.Normal,
        fontSize = 18.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp,
        color = mainColor
    ),
    titleLarge = TextStyle(
        fontFamily = FontFamily(Font(R.font.nunito_extrabold)),
        fontWeight = FontWeight.Normal,
        fontSize = 30.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.5.sp,
        color = mainColor
    ),
    titleMedium = TextStyle(
        fontFamily = FontFamily(Font(R.font.nunito_bold)),
        fontSize = 27.sp,
        letterSpacing = 0.5.sp,

        ),
    labelSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    ),
    displayLarge = TextStyle.Default.copy(
        fontFamily = FontFamily(Font(R.font.nunito_bold)),
        fontSize = 40.sp,
        color = mainColor
    ),

)
