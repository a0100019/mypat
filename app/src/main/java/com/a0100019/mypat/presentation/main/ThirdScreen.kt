package com.a0100019.mypat.presentation.main

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.a0100019.mypat.ui.theme.MypatTheme

@Composable
fun ThirdScreen() {
    Surface {
        Column {
            Text("세 번째 화면")
        }
    }
}

@Preview
@Composable
fun ThirdScreenPreview() {
    MypatTheme {
        ThirdScreen()
    }
}