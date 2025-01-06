package com.a0100019.mypat.presentation.main.world.dialog

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.a0100019.mypat.R
import com.a0100019.mypat.data.room.item.Item
import com.a0100019.mypat.data.room.pet.Pat
import com.a0100019.mypat.data.room.world.World
import com.a0100019.mypat.presentation.image.DialogPatImage
import com.a0100019.mypat.presentation.image.HorizontalLineWithValue
import com.a0100019.mypat.presentation.main.world.WorldScreen
import com.a0100019.mypat.ui.theme.MypatTheme


@Composable
fun DialogScreenContent(
    onClose: () -> Unit,
    patData: Pat,
    onFirstGameClick: () -> Unit,
    onSecondGameClick: () -> Unit,
    onThirdGameClick: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {

        Box(
            modifier = Modifier
                .fillMaxHeight(0.3f)
                .fillMaxWidth()
                .background(Color.Gray, shape = RoundedCornerShape(16.dp))
                .padding(16.dp)
        ) {
            DialogPatImage(patData.url)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(id = R.drawable.heart),
                    contentDescription = "Sample Vector Image",
                    modifier = Modifier.size(20.dp),
                )
                Text("애정도 ${patData.love/100}")
                HorizontalLineWithValue(patData.love)
            }
        }
        Text(
            text = patData.name,
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
                    text = patData.memo,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(16.dp),

                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 추가로 원하는 Composable 요소
        Text("미니 게임")
//        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = onFirstGameClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("총 게임")
        }

        Button(
            onClick = onSecondGameClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("피하기 게임")
        }

        Button(
            onClick = onThirdGameClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("맞추기 게임")
        }

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

@Preview(showBackground = true)
@Composable
fun DialogScreenContentPreview() {
    MypatTheme {
        DialogScreenContent(
            onClose = {},
            patData = Pat(
                url = "pat/cat.json",
                name = "고양이",
                love = 1000,
                memo = "귀여운 고양이 입니다. 귀여운 고양이 입니다. 귀여운 고양이 입니다. 귀여운 고양이 입니다. 귀여운 고양이 입니다. 귀여운 고양이 입니다. 귀여운 고양이 입니다. 귀여운 고양이 입니다. 귀여운 고양이 입니다. 귀여운 고양이 입니다. 귀여운 고양이 입니다. 귀여운 고양이 입니다. 귀여운 고양이 입니다. 귀여운 고양이 입니다."
            ),
            onFirstGameClick = {  },
            onSecondGameClick = {  },
            onThirdGameClick = {  },
        )
    }
}