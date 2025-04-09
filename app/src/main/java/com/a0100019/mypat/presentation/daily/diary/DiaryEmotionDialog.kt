package com.a0100019.mypat.presentation.daily.diary

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.a0100019.mypat.data.room.item.Item
import com.a0100019.mypat.presentation.ui.image.etc.JustImage
import com.a0100019.mypat.presentation.ui.theme.MypatTheme

@Composable
fun DiaryEmotionDialog(
    onClose: () -> Unit,
    onEmotionClick: (String) -> Unit,
    removeEmotion: Boolean = false
) {

    Dialog(
        onDismissRequest = onClose
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
//                .fillMaxHeight(0.8f)
                .background(Color.White, shape = RoundedCornerShape(16.dp))
                .padding(16.dp)
        ) {
            Column() {

                Text(text = "감정을 선택해주세요")

                Box(
                    modifier = Modifier
                        .background(Color.Gray, shape = RoundedCornerShape(16.dp))
                        .padding(16.dp)
                    //.fillMaxHeight(0.5f)
                ) {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(5),
                        // modifier = Modifier.height(70.dp) // 높이를 적절히 조정
                    ) {

                        item {
                            JustImage(
                                filePath = "emotion/smile.png",
                                modifier = Modifier.clickable {
                                    onEmotionClick("emotion/smile.png")
                                }
                            )
                        }

                        item {
                            JustImage(
                                filePath = "etc/arrow.png",
                                modifier = Modifier.clickable {
                                    onEmotionClick("etc/arrow.png")
                                }
                            )
                        }

                        item {
                            JustImage(
                                filePath = "emotion/smile.png",
                                modifier = Modifier.clickable {
                                    onEmotionClick("emotion/smile.png")
                                }
                            )
                        }

                        item {
                            JustImage(
                                filePath = "emotion/smile.png",
                                modifier = Modifier.clickable {
                                    onEmotionClick("emotion/smile.png")
                                }
                            )
                        }

                        item {
                            JustImage(
                                filePath = "emotion/smile.png",
                                modifier = Modifier.clickable {
                                    onEmotionClick("emotion/smile.png")
                                }
                            )
                        }

                        if(removeEmotion){
                            item {
                                JustImage(
                                    filePath = "etc/snowball.png",
                                    modifier = Modifier.clickable {
                                        onEmotionClick("etc/snowball.png")
                                    }
                                )
                            }
                        }
                    }

                }
//
//                Button(
//                    onClick = onClose
//                ) {
//                    Text(
//                        text = "취소"
//                    )
//                }


            }
        }
    }

}


@Preview(showBackground = true)
@Composable
fun DiaryEmotionDialogPreview() {
    MypatTheme {
        DiaryEmotionDialog(
            onClose = {},
            onEmotionClick = {}
        )
    }
}