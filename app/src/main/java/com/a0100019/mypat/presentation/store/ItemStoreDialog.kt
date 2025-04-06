package com.a0100019.mypat.presentation.store

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import com.a0100019.mypat.presentation.ui.image.item.ItemImage
import com.a0100019.mypat.presentation.ui.theme.MypatTheme


@Composable
fun ItemStoreDialog(
    onClose: () -> Unit,
    itemData: List<Item>?,
    onItemClick: (Int) -> Unit,
    onAdvertisementClick: () -> Unit,
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

                Text(text = "아이템 뽑기")

                Text(text = "선택해주세요")

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
                        items(itemData!!.take(5).withIndex().toList()) { (index, item) ->
                                JustImage(
                                    filePath = item.url,
                                    modifier = Modifier
                                        .size(50.dp)
                                        .clickable {
                                            onItemClick(index)
                                        }
                                )
                            Text(
                                text = item.name,
                                fontSize = 10.sp
                            )
                        }
                    }

                }

                Button(
                    onClick = onAdvertisementClick
                ) {
                    Text(
                        text = "새로 고침"
                    )
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
            itemData = listOf(Item(url = "pat/cat.json"),Item(url = "pat/cat.json"),Item(url = "pat/cat.json"),Item(url = "pat/cat.json"),Item(url = "pat/cat.json")),
            onItemClick = {},
            onAdvertisementClick = {}
        )
    }
}