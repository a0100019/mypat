package com.a0100019.mypat.presentation.ui.component

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp


@Composable
fun AutoResizeSingleLineText(
    text: String,
    modifier: Modifier = Modifier,
    maxTextStyle: TextStyle = MaterialTheme.typography.bodyMedium.copy(
        color = MaterialTheme.colorScheme.onSurface,
        fontSize = 15.sp // 기본 최대 크기
    ),
    minTextSize: TextUnit = 10.sp // 최소 글자 크기
) {
    var textStyle by remember { mutableStateOf(maxTextStyle) }
    var readyToDraw by remember { mutableStateOf(false) }

    Text(
        textAlign = TextAlign.Center,
        text = text, // 여러 줄 → 한 줄
        maxLines = 1,
        softWrap = false,
        style = textStyle,
        modifier = modifier.drawWithContent {
            if (readyToDraw) drawContent()
        },
        onTextLayout = { textLayoutResult ->
            if (textLayoutResult.didOverflowWidth && textStyle.fontSize > minTextSize) {
                // 글자가 넘치면 줄임
                textStyle = textStyle.copy(fontSize = textStyle.fontSize * 0.9f)
            } else {
                readyToDraw = true
            }
        }
    )
}

