package com.a0100019.mypat.presentation.community

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import coil.compose.rememberAsyncImagePainter
import com.a0100019.mypat.data.room.allUser.AllUser
import com.a0100019.mypat.data.room.item.Item
import com.a0100019.mypat.data.room.letter.Letter
import com.a0100019.mypat.data.room.pat.Pat
import com.a0100019.mypat.presentation.ui.image.etc.JustImage
import com.a0100019.mypat.presentation.ui.image.item.WorldItemImage
import com.a0100019.mypat.presentation.ui.image.pat.PatInformationImage
import com.a0100019.mypat.presentation.ui.theme.MypatTheme

@Composable
fun CommunityUserDialog(
    onClose: () -> Unit,
    clickAllUserData: AllUser,
    clickAllUserWorldDataList: List<String> = emptyList(),
    patDataList: List<Pat> = emptyList(),
    itemDataList: List<Item> = emptyList(),
    onLikeClick: () -> Unit = {},
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

                Surface(
                    modifier = Modifier
                        .fillMaxWidth() // 가로 크기는 최대
                        .aspectRatio(1 / 1.25f), // 세로가 가로의 1.25배
                    color = Color.Gray
                ) {

                    JustImage(
                        filePath = clickAllUserData.map,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.FillBounds
                    )

                    BoxWithConstraints(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        val density = LocalDensity.current

                        // Surface 크기 가져오기 (px → dp 변환)
                        val surfaceWidth = constraints.maxWidth
                        val surfaceHeight = constraints.maxHeight

                        val surfaceWidthDp = with(density) { surfaceWidth.toDp() }
                        val surfaceHeightDp = with(density) { surfaceHeight.toDp() }

                        clickAllUserWorldDataList.forEach { data ->
                            val parts = data.split("@")
                            if (parts[2] == "pat") {
                                // pat일 때 처리
                                patDataList.find { it.id.toString() == parts[0] }?.let { patData ->
                                    PatInformationImage(
                                        patUrl = patData.url,
                                        surfaceWidthDp = surfaceWidthDp,
                                        surfaceHeightDp = surfaceHeightDp,
                                        xFloat = parts[3].toFloat(),
                                        yFloat = parts[4].toFloat(),
                                        sizeFloat = parts[1].toFloat(),
                                    )
                                }

                            } else {
                                // item일 때 처리
                                itemDataList.find { it.id.toString() == parts[0] }?.let { itemData ->
                                    WorldItemImage(
                                        itemUrl = itemData.url,
                                        surfaceWidthDp = surfaceWidthDp,
                                        surfaceHeightDp = surfaceHeightDp,
                                        xFloat = parts[3].toFloat(),
                                        yFloat = parts[4].toFloat(),
                                        sizeFloat = parts[1].toFloat(),
                                    )
                                }
                            }

                        }
                    }
                }

                Row {
                    JustImage(
                        filePath = "etc/arrow.png",
                        modifier = Modifier
                            .clickable {
                                onLikeClick()
                            }
                    )
                    Text(
                        text = clickAllUserData.like
                    )
                }

            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun CommunityUserDialogPreview() {
    MypatTheme {
        CommunityUserDialog(
            onClose = {},
            AllUser(
                tag = "22",
                lastLogIn = 342112,
                ban = "0",
                like = "54",
                warning = "0",
                firstDate = "2025-02-05",
                openItem = "30",
                openItemSpace = "10",
                map = "map/forest.jpg",
                name = "이222유빈",
                openPat = "20",
                openPatSpace = "10",
                totalDate = "134",
                worldData = "1@0.2@pat@0.25@0.69/2@0.2@pat@0.25@0.569/1@0.2@pat@0.125@0.69/1@0.2@item@0.25@0.69/2@0.2@item@0.125@0.769/1@0.2@item@0.225@0.1691@0.2@pat@0.25@0.669/2@0.2@pat@0.25@0.369/2@0.3@pat@0.325@0.69/1@0.2@pat@0.725@0.769/1@0.2@item@0.425@0.669",
            ),
        )
    }
}