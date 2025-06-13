package com.a0100019.mypat.presentation.store

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.a0100019.mypat.data.room.pat.Pat
import com.a0100019.mypat.presentation.ui.image.etc.JustImage
import com.a0100019.mypat.presentation.ui.image.pat.DialogPatImage
import com.a0100019.mypat.presentation.ui.theme.MypatTheme

@Composable
fun PatStoreDialog(
    onClose: () -> Unit,
    patData: List<Pat>?,
    patEggData: List<Pat>?,
    onPatEggClick: (Int) -> Unit,
    selectIndexList: List<Int>
) {

    Dialog(
        onDismissRequest = onClose
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White, shape = RoundedCornerShape(16.dp))
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                ,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Text(
                    text = "펫 뽑기",
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier
                        .padding(bottom = 6.dp)
                    )

                Row(
                    modifier = Modifier
                        .background(
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            shape = RoundedCornerShape(12.dp)
                        )
                        .border(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.outline,
                            shape = RoundedCornerShape(12.dp)
                        )
                        .padding(6.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    repeat(5) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            tonalElevation = 4.dp,
                            shadowElevation = 4.dp,
                            color = MaterialTheme.colorScheme.surface,
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                            modifier = Modifier
                        ) {
                            Column(
                                modifier = Modifier,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                DialogPatImage(
                                    patUrl = patData!![it].url,
                                    modifier = Modifier.size(45.dp)
                                )
                                Text(
                                    text = patData[it].name,
                                    fontSize = 13.sp,
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.size(6.dp))

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
                        items(patEggData!!.take(10).withIndex().toList()) { (index, pat) ->
                            if(!selectIndexList.contains(index)) {
                                JustImage(
                                    filePath = "etc/egg.json",
                                    modifier = Modifier
                                        .size(50.dp)
                                        .clickable {
                                            onPatEggClick(index)
                                        }
                                )
                            } else {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    DialogPatImage(
                                        patUrl = pat.url,
                                        modifier = Modifier.size(30.dp)
                                    )
                                    Text(
                                        text = pat.name, // 인덱스 표시
                                        fontSize = 10.sp,
                                        modifier = Modifier
                                    )
                                }
                            }
                        }
                    }


                }

//                Button(
//                    onClick = onClose,
//                    modifier = Modifier
//                        .align(Alignment.End)
//                        .padding(16.dp)
//                ) {
//                    Text("다시 하기")
//                }

            }
        }
    }

}


@Preview(showBackground = true)
@Composable
fun PatStoreDialogPreview() {
    MypatTheme {
        PatStoreDialog(
            onClose = {},
            patData = listOf(Pat(url = "pat/cat.json", name = "고양이"),Pat(url = "pat/cat.json"),Pat(url = "pat/cat.json"),Pat(url = "pat/cat.json"),Pat(url = "pat/cat.json")),
            patEggData = listOf(Pat(url = "pat/cat.json"),Pat(url = "pat/cat.json"),Pat(url = "pat/cat.json"),Pat(url = "pat/cat.json"),Pat(url = "pat/cat.json"),Pat(url = "pat/cat.json"),Pat(url = "pat/cat.json"),Pat(url = "pat/cat.json"),Pat(url = "pat/cat.json"),Pat(url = "pat/cat.json"),Pat(url = "pat/cat.json"),),
            onPatEggClick = {},
            selectIndexList = emptyList()
        )
    }
}