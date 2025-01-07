package com.a0100019.mypat.presentation.main.world.patDialog

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.a0100019.mypat.data.room.pet.Pat
import com.a0100019.mypat.presentation.image.DialogPatImage
import com.a0100019.mypat.ui.theme.MypatTheme


@Composable
fun DialogScreenContent(
    onClose: () -> Unit,
    patData: Pat
) {
    Column(modifier = Modifier.fillMaxSize()) {

        Box(
            modifier = Modifier
                .fillMaxHeight(0.4f) // 화면의 60%만 차지
                .background(Color.White, shape = RoundedCornerShape(16.dp))
                .padding(16.dp)
        ) {
            DialogPatImage(patData.url)
        }
        Text(
            text = patData.name,
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(16.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = onClose,
            modifier = Modifier.align(Alignment.End).padding(16.dp)
        ) {
            Text("Close")
        }
        Spacer(modifier = Modifier.height(16.dp))

        // 추가로 원하는 Composable 요소
        Text("Add any other content here.")
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = { /* 다른 동작 */ },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Perform Action")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DialogScreenContentPreview() {
    MypatTheme {
        DialogScreenContent(
            onClose = {},
            patData = Pat(url = "pat/cat.json", name = "고양이")
        )
    }
}