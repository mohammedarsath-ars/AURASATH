package com.aurasath.music.ui.theme

import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import com.aurasath.music.R

val bbhBartle: FontFamily by lazy {
    runCatching {
        FontFamily(
            Font(R.font.bbh_bartle_regular, FontWeight.Normal)
        )
    }.getOrDefault(FontFamily.Default)
}
