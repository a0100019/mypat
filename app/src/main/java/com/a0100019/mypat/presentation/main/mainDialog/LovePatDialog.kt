package com.a0100019.mypat.presentation.main.mainDialog

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.a0100019.mypat.R
import com.a0100019.mypat.data.room.item.Item
import com.a0100019.mypat.data.room.pat.Pat
import com.a0100019.mypat.presentation.ui.component.MainButton
import com.a0100019.mypat.presentation.ui.image.etc.LoveHorizontalLine
import com.a0100019.mypat.presentation.ui.image.item.DraggableItemImage
import com.a0100019.mypat.presentation.ui.image.pat.DialogPatImage
import com.a0100019.mypat.presentation.ui.theme.MypatTheme

@Composable
fun LovePatDialog(
    lovePatData: Pat,
    onItemDrag: (String, Float, Float) -> Unit,
    loveItemData1: Item,
    loveItemData2: Item,
    loveItemData3: Item,
    loveAmount: Int,
    onLovePatNextClick: () -> Unit,
    onLovePatStopClick: () -> Unit,
    situation: String,
    cashAmount: Int = 0
    
) {

    Dialog(
        onDismissRequest = {  }
    ) {
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.6f)
                .background(Color.White, shape = RoundedCornerShape(16.dp))
                .padding(16.dp)
        ) {

            val density = LocalDensity.current

            // Surface 크기 가져오기 (px → dp 변환)
            val surfaceWidth = constraints.maxWidth
            val surfaceHeight = constraints.maxHeight

            val surfaceWidthDp = with(density) { surfaceWidth.toDp() }
            val surfaceHeightDp = with(density) { surfaceHeight.toDp() }

            Column(modifier = Modifier.fillMaxSize()) {

                Box(
                    modifier = Modifier
                        .fillMaxHeight(0.5f)
                        .fillMaxWidth()
                        .background(Color.Gray, shape = RoundedCornerShape(16.dp))
                        .padding(16.dp)
                ) {
                    DialogPatImage(lovePatData.url)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(id = R.drawable.heart),
                            contentDescription = "Sample Vector Image",
                            modifier = Modifier
                                .size(20.dp)
                                .padding(end = 6.dp)
                            ,
                        )
                        Text(
                            text = "${lovePatData.love / 10000}",
                            modifier = Modifier
                                .padding(end = 6.dp)
                        )
                        LoveHorizontalLine(
                            value = lovePatData.love,
                            plusValue = loveAmount,
                            )
                    }

                }

                if(situation == "lovePatOnGoing"){
                    Text(
                        text = "장난감을 펫에게 흔들어주세요\n원하는 장난감을 맞추면 더 큰 보상을 획득할 수 있습니다",
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .padding(10.dp)
                    )
                }

                when(situation) {

                    "lovePatSuccess" -> Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .fillMaxSize()
                    ) {
                        Text(
                            text = "펫이 원하는 장난감입니다"
                            ,
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .align(Alignment.TopCenter)
                                .padding(10.dp)
                        )

                        MainButton(
                            onClick = onLovePatNextClick,
                            text = "한번 더 흔들기",
                            modifier = Modifier
                                .fillMaxWidth(0.5f)
                                .align(Alignment.Center) // ⭐ 중앙 정렬
                        )

                        Text(
                            text = "애정도 +${loveAmount}, 달빛 +${cashAmount}"
                            ,
                            style = MaterialTheme.typography.titleMedium,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .padding(10.dp)
                        )
                    }

                    "lovePatFail" -> Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .fillMaxSize()
                    ){
                        Text(
                            text = "펫이 원하는 장난감이 아닙니다"
                            ,
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .align(Alignment.TopCenter)
                                .padding(10.dp)
                        )

                        MainButton(
                            onClick = onLovePatStopClick,
                            text = "확인",
                            modifier = Modifier
                                .fillMaxWidth(0.5f)
                                .align(Alignment.Center) // ⭐ 중앙 정렬
                        )

                        Text(
                            text = "애정도 +${loveAmount}, 달빛 +${cashAmount}"
                            ,
                            style = MaterialTheme.typography.headlineSmall,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .padding(10.dp)
                        )
                    }

                }

            }

            if(situation == "lovePatOnGoing"){

                DraggableItemImage(
                    itemUrl = loveItemData1.url,
                    surfaceWidthDp = surfaceWidthDp,
                    surfaceHeightDp = surfaceHeightDp,
                    xFloat = loveItemData1.x,
                    yFloat = loveItemData1.y,
                    sizeFloat = 0.2f,
                    onClick = { }
                ) { newXFloat, newYFloat ->
                    onItemDrag(loveItemData1.id.toString(), newXFloat, newYFloat)
                }

                DraggableItemImage(
                    itemUrl = loveItemData2.url,
                    surfaceWidthDp = surfaceWidthDp,
                    surfaceHeightDp = surfaceHeightDp,
                    xFloat = loveItemData2.x,
                    yFloat = loveItemData2.y,
                    sizeFloat = 0.2f,
                    onClick = { }
                ) { newXFloat, newYFloat ->
                    onItemDrag(loveItemData2.id.toString(), newXFloat, newYFloat)
                }

                DraggableItemImage(
                    itemUrl = loveItemData3.url,
                    surfaceWidthDp = surfaceWidthDp,
                    surfaceHeightDp = surfaceHeightDp,
                    xFloat = loveItemData3.x,
                    yFloat = loveItemData3.y,
                    sizeFloat = 0.2f,
                    onClick = { }
                ) { newXFloat, newYFloat ->
                    onItemDrag(loveItemData3.id.toString(), newXFloat, newYFloat)
                }
            }
            
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LovePatDialogPreview() {
    MypatTheme {
        LovePatDialog(
            lovePatData = Pat(url = "pat/cat.json"),
            onItemDrag = { id, newX, newY -> },
            loveItemData1 = Item(id = 1, name = "쓰다듬기", url = "etc/hand.png", x = 0.2f, y = 0.7f, sizeFloat = 0.2f),
            loveItemData2 = Item(id = 2, name = "장난감", url = "etc/hand.png", x = 0.5f, y = 0.7f, sizeFloat = 0.2f),
            loveItemData3 = Item(id = 3, name = "비행기", url = "etc/hand.png", x = 0.8f, y = 0.7f, sizeFloat = 0.2f),
            onLovePatNextClick = {},
            onLovePatStopClick = {},
            loveAmount = 100,
            situation = "lovePatSuccess"
        )
    }
}