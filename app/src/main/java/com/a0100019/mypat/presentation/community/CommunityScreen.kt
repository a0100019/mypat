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
import com.a0100019.mypat.data.room.allUser.AllUser
import com.a0100019.mypat.data.room.item.Item
import com.a0100019.mypat.data.room.pat.Pat
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
        situation = communityState.situation,
        patDataList = communityState.patDataList,
        itemDataList = communityState.itemDataList,
        page = communityState.page,
        allUserDataList = communityState.allUserDataList,
        allUserData1 = communityState.allUserData1,
        allUserData2 = communityState.allUserData2,
        allUserData3 = communityState.allUserData3,
        allUserData4 = communityState.allUserData4,
        allUserWorldDataList1 = communityState.allUserWorldDataList1,
        allUserWorldDataList2 = communityState.allUserWorldDataList2,
        allUserWorldDataList3 = communityState.allUserWorldDataList3,
        allUserWorldDataList4 = communityState.allUserWorldDataList4,
        onPageUpClick = communityViewModel::opPageUpClick
    )
}


@Composable
fun CommunityScreen(
    situation : String,
    patDataList: List<Pat> = emptyList(),
    itemDataList: List<Item> = emptyList(),
    page: Int = 0,
    allUserDataList: List<AllUser> = emptyList(),
    allUserData1: AllUser = AllUser(),
    allUserData2: AllUser = AllUser(),
    allUserData3: AllUser = AllUser(),
    allUserData4: AllUser = AllUser(),
    allUserWorldDataList1: List<String> = emptyList(),
    allUserWorldDataList2: List<String> = emptyList(),
    allUserWorldDataList3: List<String> = emptyList(),
    allUserWorldDataList4: List<String> = emptyList(),

    onPageUpClick: () -> Unit = {},
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

                        },
                    userData = allUserData1,
                    worldDataList = allUserWorldDataList1,
                    patDataList = patDataList,
                    itemDataList = itemDataList
                )
                CommunityWorldCard(
                    modifier = Modifier
                        .weight(1f)
                        .clickable {

                        },
                    userData = allUserData2,
                    worldDataList = allUserWorldDataList2,
                    patDataList = patDataList,
                    itemDataList = itemDataList
                )
            }
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                CommunityWorldCard(
                    modifier = Modifier
                        .weight(1f)
                        .clickable {

                        },
                    userData = allUserData3,
                    worldDataList = allUserWorldDataList3,
                    patDataList = patDataList,
                    itemDataList = itemDataList
                )
                CommunityWorldCard(
                    modifier = Modifier
                        .weight(1f)
                        .clickable {

                        },
                    userData = allUserData4,
                    worldDataList = allUserWorldDataList4,
                    patDataList = patDataList,
                    itemDataList = itemDataList
                )
            }
            Button(
                onClick = onPageUpClick
            ) {
                Text("다음")
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
            situation = "world"
        )
    }
}