package com.a0100019.mypat.presentation.neighbor.community

import android.annotation.SuppressLint
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.a0100019.mypat.data.room.allUser.AllUser
import com.a0100019.mypat.data.room.item.Item
import com.a0100019.mypat.data.room.pat.Pat
import com.a0100019.mypat.presentation.ui.image.etc.JustImage
import com.a0100019.mypat.presentation.ui.image.item.WorldItemImage
import com.a0100019.mypat.presentation.ui.image.pat.PatImage
import com.a0100019.mypat.presentation.ui.theme.MypatTheme

@Composable
fun CommunityWorldCard(
    userData: AllUser = AllUser(),
    worldDataList: List<String> = emptyList(),
    patDataList: List<Pat> = emptyList(),
    itemDataList: List<Item> = emptyList(),
    onClick: () -> Unit = {},
    @SuppressLint("ModifierParameter") modifier: Modifier = Modifier,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        label = "scale"
    )

    Card(
        modifier = modifier
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
            .border(
                width = 2.dp,
                color = MaterialTheme.colorScheme.primaryContainer,
                shape = RoundedCornerShape(16.dp)
            )
        ,
        shape = RoundedCornerShape(16.dp),
//        elevation = CardDefaults.cardElevation(6.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.scrim
        ),
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Surface(
                modifier = Modifier
                    .fillMaxWidth() // 가로 크기는 최대
                    .aspectRatio(1 / 1.25f)
                    .background(MaterialTheme.colorScheme.scrim, shape = RoundedCornerShape(16.dp))
                    .border(
                        width = 2.dp,
                        color = MaterialTheme.colorScheme.primaryContainer,
                        shape = RoundedCornerShape(16.dp)
                    )
                ,
                shape = RoundedCornerShape(16.dp),
                color = Color.LightGray
            ) {

                JustImage(
                    filePath = userData.area,
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

                    if(worldDataList.isNotEmpty()){
                        worldDataList.forEach { data ->
                            val parts = data.split("@")
                            if (parts[2] == "pat") {
                                // pat일 때 처리
                                patDataList.find { it.id.toString() == parts[0] }?.let { patData ->
                                    PatImage(
                                        patUrl = patData.url,
                                        surfaceWidthDp = surfaceWidthDp,
                                        surfaceHeightDp = surfaceHeightDp,
                                        xFloat = parts[3].toFloat(),
                                        yFloat = parts[4].toFloat(),
                                        sizeFloat = parts[1].toFloat(),
                                        effect = parts[5].toInt(),
                                        onClick = null,
                                        isPlaying = false
                                    )
                                }

                            } else {
                                // item일 때 처리
                                itemDataList.find { it.id.toString() == parts[0] }
                                    ?.let { itemData ->
                                        WorldItemImage(
                                            itemUrl = itemData.url,
                                            surfaceWidthDp = surfaceWidthDp,
                                            surfaceHeightDp = surfaceHeightDp,
                                            xFloat = parts[3].toFloat(),
                                            yFloat = parts[4].toFloat(),
                                            sizeFloat = parts[1].toFloat(),
                                            isPlaying = false
                                        )
                                    }
                            }

                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = userData.name,
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = " #" + userData.tag,
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center
                )
            }

        }
    }
}



@Preview(showBackground = true)
@Composable
fun CommunityWorldCardPreview() {
    MypatTheme {
        CommunityWorldCard(

            userData = AllUser(
                tag = "436",
                lastLogin = 342112,
                ban = "0",
                like = "54",
                warning = "0",
                firstDate = "2025-02-05",
                openItem = "30",
                area = "area/forest.jpg",
                name = "이222유빈",
                openPat = "20",
                totalDate = "134",
            ),
            worldDataList = listOf("1@0.2@pat@0.25@0.69@1", "2@0.2@pat@0.25@0.569@1", "1@0.2@pat@0.125@0.69@1", "1@0.2@item@0.25@0.69@1", "2@0.2@item@0.125@0.769@1", "1@0.2@item@0.225@0.169@2", "1@0.2@pat@0.25@0.669@1", "2@0.2@pat@0.25@0.369@1", "2@0.3@pat@0.325@0.69@1", "1@0.2@pat@0.725@0.769@1", "1@0.2@item@0.425@0.669@1",
            ),
            patDataList = listOf(Pat(id = 1, url = "pat/cat.json"))
        )
    }
}