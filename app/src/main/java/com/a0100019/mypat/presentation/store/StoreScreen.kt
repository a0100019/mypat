package com.a0100019.mypat.presentation.store

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.a0100019.mypat.data.room.pet.Pat
import com.a0100019.mypat.data.room.user.User
import com.a0100019.mypat.presentation.index.IndexPatDialog
import com.a0100019.mypat.presentation.ui.theme.MypatTheme
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

@Composable
fun StoreScreen(
    storeViewModel: StoreViewModel = hiltViewModel()

) {

    val storeState : StoreState = storeViewModel.collectAsState().value

    val context = LocalContext.current

    storeViewModel.collectSideEffect { sideEffect ->
        when (sideEffect) {
            is StoreSideEffect.Toast -> Toast.makeText(context, sideEffect.message, Toast.LENGTH_SHORT).show()

        }
    }

    StoreScreen(
        onPatRandomClick = storeViewModel::onPatRandomClick,
        onDialogCloseClick = storeViewModel::onDialogCloseClick,
        onPatRoomUpClick = storeViewModel::onPatRoomUpClick,

        newPat = storeState.newPat,
        userData = storeState.userData,
        showRoomUpDialog = storeState.showRoomUpDialog

    )
}



@Composable
fun StoreScreen(
    onPatRandomClick: () -> Unit,
    onDialogCloseClick: () -> Unit,
    onPatRoomUpClick: () -> Unit,

    newPat: Pat?,
    userData: List<User>,
    showRoomUpDialog: String
) {

    // 다이얼로그 표시
    if (newPat != null) {
        IndexPatDialog(
            onClose = onDialogCloseClick,
            patData = newPat,
        )
    }

    if (showRoomUpDialog != "") {
        RoomUpDialog(
            onClose = onDialogCloseClick,
            userData = userData,
            showRoomUpDialog = showRoomUpDialog
        )
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Row {
            Text("money : ${userData.find { it.id == "money" }?.value}")
            Text("cash : ${userData.find {it.id == "cash"}?.value}")
        }
        Column(
            modifier = Modifier.verticalScroll(rememberScrollState())
        ) {
            Button(
                onClick = onPatRandomClick
            ) {
                Text("알 뽑기 100원")
            }
            Button(
                onClick = {}
            ) {
                Text("아이템 뽑기")
            }
            Button(
                onClick = {}
            ) {
                Text("맵 뽑기")
            }
            Button(
                onClick = onPatRoomUpClick
            ) {
                Text("펫 칸 늘리기 10cash")
            }
            Button(
                onClick = {}
            ) {
                Text("아이템 칸 늘리기")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun StoreScreenPreview() {
    MypatTheme {
        StoreScreen(
            onPatRandomClick = {},
            onDialogCloseClick = {},
            onPatRoomUpClick = {},

            newPat = null,
            userData = emptyList(),
            showRoomUpDialog = ""
        )
    }
}