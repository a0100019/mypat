package com.a0100019.mypat.presentation.setting

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import coil.compose.rememberAsyncImagePainter
import com.a0100019.mypat.data.room.letter.Letter
import com.a0100019.mypat.presentation.ui.theme.MypatTheme

@Composable
fun LetterViewDialog(
    onClose: () -> Unit,
    onLetterLinkClick: () -> Unit,
    onLetterGetClick: () -> Unit,
    clickLetterData: Letter,
    letterImages: List<String>
) {

    Dialog(
        onDismissRequest = onClose
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.9f)
                .background(Color.White, shape = RoundedCornerShape(16.dp))
                .padding(16.dp)
        ) {
            Column {

                Button(
                    onClick = onClose,
                    modifier = Modifier
                        .padding(16.dp)
                        .align(Alignment.End)
                ) {
                    Text("닫기")
                }

                Text(
                    text = clickLetterData.title,
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.padding(10.dp),
                    color = Color.Black
                )

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                ) {

                    letterImages.forEach { image ->
                        Box{

                            Text("loading...")
                            
                            Image(
                                painter = rememberAsyncImagePainter(image),
                                contentDescription = null,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .aspectRatio(0.6f), // 이거 중요!
                                contentScale = ContentScale.Fit // 또는 ContentScale.Crop, 원하는대로 조절
                            )
                        }
                    }

                    if(clickLetterData.link != "0") {
                        Button(
                            onClick = onLetterLinkClick
                        ) {
                            Text("링크 이동하기")
                        }

                    }

                    if(clickLetterData.state != "get"){
                        Button(
                            onClick = onLetterGetClick,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Text("${clickLetterData.reward} +${clickLetterData.amount}")
                        }
                    }

                }


            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun LetterViewDialogPreview() {
    MypatTheme {
        LetterViewDialog(
            onClose = {},
            clickLetterData = Letter(state = "waiting", title = "첫 편지", image = "sample.png@sample.png", link = "naver.com", reward = "cash", amount = "100" ),
            letterImages = emptyList(),
            onLetterLinkClick = {},
            onLetterGetClick = {}



            )
    }
}