package com.a0100019.mypat.presentation.store

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.a0100019.mypat.data.room.item.Item
import com.a0100019.mypat.presentation.ui.image.etc.JustImage
import com.a0100019.mypat.presentation.ui.theme.MypatTheme


@Composable
fun ItemStoreDialog(
    onClose: () -> Unit,
    itemData: List<String>?,
    onItemClick: (String) -> Unit,
) {

    Dialog(
        onDismissRequest = onClose
    ) {
        Box(
            modifier = Modifier
                .width(340.dp)
                .shadow(12.dp, RoundedCornerShape(24.dp))
                .border(
                    width = 2.dp,
                    color = MaterialTheme.colorScheme.outline,
                    shape = RoundedCornerShape(24.dp)
                )
                .background(
                    color = MaterialTheme.colorScheme.background,
                    shape = RoundedCornerShape(24.dp)
                )
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                ,
                horizontalAlignment = Alignment.CenterHorizontally
                ) {

                Text(
                    text = "아이템 뽑기",
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier
                        .padding(bottom = 6.dp)
                    )

                Text(
                    text = "원하는 아이템을 선택해주세요",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier
                        .padding(6.dp)
                    )

                Box(
                    modifier = Modifier
                        .background(
                            MaterialTheme.colorScheme.scrim,
                            shape = RoundedCornerShape(16.dp)
                        )
                        .border(
                            width = 2.dp,
                            color = MaterialTheme.colorScheme.primaryContainer, // 테두리
                            shape = RoundedCornerShape(16.dp)
                        )
                        .padding(16.dp)
                    //.fillMaxHeight(0.5f)
                ) {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(5),
                        // modifier = Modifier.height(70.dp) // 높이를 적절히 조정
                    ) {

                        items(itemData!!.take(5).withIndex().toList()) { (index, item) ->

                            val part = item.split("@")

                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                JustImage(
                                    filePath = part[2],
                                    modifier = Modifier
                                        .size(50.dp)
                                        .clickable {
                                            onItemClick(item)
                                        }
                                )
                                Text(
                                    text = part[3],
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }

                }

            }
        }
    }

}


@Preview(showBackground = true)
@Composable
fun ItemStoreDialogPreview() {
    MypatTheme {
        ItemStoreDialog(
            onClose = {},
            itemData = listOf("item@1@pat/cat.json@고양이"),
            onItemClick = {},
        )
    }
}