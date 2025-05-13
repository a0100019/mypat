package com.a0100019.mypat.presentation.community

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.a0100019.mypat.presentation.ui.theme.MypatTheme
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

@Composable
fun CommunityScreen(
    communityViewModel: CommunityViewModel = hiltViewModel()

) {

    val communityState : CommunityState = communityViewModel.collectAsState().value

    val context = LocalContext.current

    communityViewModel.collectSideEffect { sideEffect ->
        when (sideEffect) {
            is CommunitySideEffect.Toast -> Toast.makeText(context, sideEffect.message, Toast.LENGTH_SHORT).show()
        }
    }

    CommunityScreen(
        situation = "스크린 나누기"
    )
}



@Composable
fun CommunityScreen(
    situation : String
) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {

        Text(
            text = "마을 구경하기"
        )

        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.padding(16.dp)
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                CommunityWorldCard(
                    modifier = Modifier
                        .weight(1f)
                        .clickable {

                        }
                )
                CommunityWorldCard(modifier = Modifier.weight(1f))
            }
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                CommunityWorldCard(modifier = Modifier.weight(1f))
                CommunityWorldCard(modifier = Modifier.weight(1f))
            }
        }



        Row {
            Button(
                onClick = {}
            ) {
                Text("마을")
            }

            Button(
                onClick = {}
            ) {
                Text("게임1")
            }

            Button(
                onClick = {}
            ) {
                Text("게임2")
            }

            Button(
                onClick = {}
            ) {
                Text("게임3")
            }

            Button(
                onClick = {}
            ) {
                Text("채팅")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CommunityScreenPreview() {
    MypatTheme {
        CommunityScreen(
            situation = ""
        )
    }
}