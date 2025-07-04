package com.a0100019.mypat.presentation.index

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.window.Dialog
import com.a0100019.mypat.data.room.area.Area
import com.a0100019.mypat.presentation.ui.image.item.ItemImage
import com.a0100019.mypat.presentation.ui.theme.MypatTheme

@Composable
fun IndexAreaDialog(
    onClose: () -> Unit,
    areaData: Area,
    ) {
        Dialog(
            onDismissRequest = onClose
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.8f)
                    .background(Color.White, shape = RoundedCornerShape(16.dp))
                    .padding(16.dp)
            ) {
                Column(modifier = Modifier.fillMaxSize()) {

                    Box(
                        modifier = Modifier
                            .fillMaxHeight(0.3f)
                            .fillMaxWidth()
                            .background(Color.Gray, shape = RoundedCornerShape(16.dp))
                            .padding(16.dp)
                    ) {
                        ItemImage(areaData.url)
                    }
                    Text(
                        text = areaData.name,
                        style = MaterialTheme.typography.headlineMedium,
                        modifier = Modifier.padding(16.dp),
                        color = Color.Black
                    )

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxHeight(0.3f)
                            .fillMaxWidth()
                            .background(Color.Gray, shape = RoundedCornerShape(16.dp))
                            .padding(16.dp)
                    ) {
                        item {
                            Text(
                                text = areaData.memo,
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(16.dp),

                                )
                        }
                    }
                    Text("획득 날짜 : ${areaData.date}")

                    Spacer(modifier = Modifier.height(16.dp))

                    // 추가로 원하는 Composable 요소


                    Button(
                        onClick = onClose,
                        modifier = Modifier
                            .align(Alignment.End)
                            .padding(16.dp)
                    ) {
                        Text("Close")
                    }

                }
            }
        }
    }


    @Preview(showBackground = true)
    @Composable
    fun IndexMapDialogPreview() {
        MypatTheme {
            IndexAreaDialog(
                onClose = {},
                areaData = Area(
                    url = "area/forest.jpg",
                    name = "숲",
                    memo = "귀여운 고양이 입니다. 귀여운 고양이 입니다. 귀여운 고양이 입니다. 귀여운 고양이 입니다. 귀여운 고양이 입니다. 귀여운 고양이 입니다. 귀여운 고양이 입니다. 귀여운 고양이 입니다. 귀여운 고양이 입니다. 귀여운 고양이 입니다. 귀여운 고양이 입니다. 귀여운 고양이 입니다. 귀여운 고양이 입니다. 귀여운 고양이 입니다."
                ),
            )
        }
    }