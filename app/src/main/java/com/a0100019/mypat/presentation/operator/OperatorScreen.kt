package com.a0100019.mypat.presentation.operator

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.a0100019.mypat.R
import com.a0100019.mypat.data.room.allUser.AllUser
import com.a0100019.mypat.data.room.item.Item
import com.a0100019.mypat.data.room.pat.Pat
import com.a0100019.mypat.data.room.user.User
import com.a0100019.mypat.domain.AppBgmManager
import com.a0100019.mypat.presentation.chat.CommunityAskDialog
import com.a0100019.mypat.presentation.community.CommunityUserDialog
import com.a0100019.mypat.presentation.main.mainDialog.SimpleAlertDialog
import com.a0100019.mypat.presentation.ui.component.MainButton
import com.a0100019.mypat.presentation.ui.image.etc.BackGroundImage
import com.a0100019.mypat.presentation.ui.image.etc.JustImage
import com.a0100019.mypat.presentation.ui.theme.MypatTheme
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.abs

@Composable
fun OperatorScreen(
    operatorViewModel: OperatorViewModel = hiltViewModel(),
    popBackStack: () -> Unit = {}

) {

    val operatorState : OperatorState = operatorViewModel.collectAsState().value

    val context = LocalContext.current

    operatorViewModel.collectSideEffect { sideEffect ->
        when (sideEffect) {
            is OperatorSideEffect.Toast -> Toast.makeText(context, sideEffect.message, Toast.LENGTH_SHORT).show()
        }
    }

    CommunityScreen(
        situation = operatorState.situation,
        patDataList = operatorState.patDataList,
        itemDataList = operatorState.itemDataList,
        allUserDataList = operatorState.allUserDataList,
        clickAllUserData = operatorState.clickAllUserData,
        clickAllUserWorldDataList = operatorState.clickAllUserWorldDataList,
        allUserRankDataList = operatorState.allUserRankDataList,
        newChat = operatorState.newChat,
        userDataList = operatorState.userDataList,
        alertState = operatorState.alertState,
        allAreaCount = operatorState.allAreaCount,
        dialogState = operatorState.dialogState,
        text2 = operatorState.text2,
        text3 = operatorState.text3,
        askMessages = operatorState.askMessages,

        onSituationChange = operatorViewModel::onSituationChange,
        onChatTextChange = operatorViewModel::onChatTextChange,
        onUserRankClick = operatorViewModel::onUserRankClick,
        alertStateChange = operatorViewModel::alertStateChange,
        popBackStack = popBackStack,
        onAskChatWrite = operatorViewModel::onAskChatWrite,
        onDialogChangeClick = operatorViewModel::onDialogChangeClick,
        onNoticeChatWrite = operatorViewModel::onNoticeChatWrite,
        onCloseClick = operatorViewModel::onCloseClick,
        onTextChange2 = operatorViewModel::onTextChange2,
        onTextChange3 = operatorViewModel::onTextChange3,
        onOperatorChatSubmitClick = operatorViewModel::onOperatorChatSubmitClick,
        onAskClick = operatorViewModel::onAskClick

    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommunityScreen(
    situation : String = "chat",
    patDataList: List<Pat> = emptyList(),
    itemDataList: List<Item> = emptyList(),
    allUserDataList: List<AllUser> = emptyList(),
    clickAllUserData: AllUser = AllUser(),
    clickAllUserWorldDataList: List<String> = emptyList(),
    allUserRankDataList: List<AllUser> = listOf(AllUser(), AllUser()),
    askMessages: List<OperatorMessage> = emptyList(),
    newChat: String = "",
    userDataList: List<User> = emptyList(),
    alertState: String = "",
    allAreaCount: String = "0",
    dialogState: String = "",
    text2: String = "",
    text3: String = "",

    onPageUpClick: () -> Unit = {},
    onSituationChange: (String) -> Unit = {},
    onChatTextChange: (String) -> Unit = {},
    onUserRankClick: (Int) -> Unit = {},
    alertStateChange: (String) -> Unit = {},
    popBackStack: () -> Unit = {},
    onDialogChangeClick: (String) -> Unit = {},
    onAskChatWrite: () -> Unit = {},
    onNoticeChatWrite: () -> Unit = {},
    onCloseClick: () -> Unit = {},
    onTextChange2: (String) -> Unit = {},
    onTextChange3: (String) -> Unit = {},
    onOperatorChatSubmitClick: () -> Unit = {},
    onAskClick: (String) -> Unit = {},

    ) {

    val context = LocalContext.current
    val prefs = context.getSharedPreferences("bgm_prefs", Context.MODE_PRIVATE)
    val bgmOn = prefs.getBoolean("bgmOn", true)

    when(dialogState) {
        "askView" -> CommunityAskViewDialog(
            onClose = onCloseClick,
            onAskClick = onAskClick,
            askMessages = askMessages
        )
        "askWrite" -> CommunityAskWriteDialog(
            onClose = onCloseClick,
            onTextChange = onChatTextChange,
            text = newChat,
            onConfirmClick = onAskChatWrite,
        )
        "notice" -> CommunityNoticeDialog(
            onClose = onCloseClick,
            onTextChange = onChatTextChange,
            text = newChat,
            onConfirmClick = onNoticeChatWrite,
        )
        "operatorChat" -> CommunityOperatorChatDialog(
            onClose = onCloseClick,
            onTextChange = onChatTextChange,
            onTextChange2 = onTextChange2,
            onTextChange3 = onTextChange3,
            text2 = text2,
            text3 = text3,
            text = newChat,
            onConfirmClick = {},
            onOperatorChatSubmitClick = onOperatorChatSubmitClick

        )
    }

    if(clickAllUserData.tag != "0") {
        AppBgmManager.pause()
        CommunityUserDialog(
            onClose = { onUserRankClick(0) },
            clickAllUserData = clickAllUserData,
            clickAllUserWorldDataList = clickAllUserWorldDataList,
            patDataList = patDataList,
            itemDataList = itemDataList,
            onLikeClick = {
            },
            onBanClick = {
                alertStateChange("-1")
            },
            allUserDataList = allUserDataList,
            allMapCount = allAreaCount
        )
    } else {
        if (bgmOn) {
            AppBgmManager.play()
        }
    }

    if(alertState != "") {
        SimpleAlertDialog(
            onConfirm = {
                alertStateChange("")
            },
            onDismiss = { alertStateChange("") },
            text = "신고하시겠습니까?"
        )
    }

    Surface(
        modifier = Modifier
            .fillMaxSize()
    ) {

        BackGroundImage()

        Box(
            modifier = Modifier.padding(6.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {


                // 오른쪽 버튼
                MainButton(
                    text = "닫기",
                    onClick = popBackStack,
                )

                Spacer(modifier = Modifier.size(30.dp))

                Row {
                    MainButton(
                        text = "도란도란",
                        onClick = { onDialogChangeClick("askView") },
                        modifier = Modifier
                    )
                    MainButton(
                        text = "공지",
                        onClick = { onDialogChangeClick("notice") },
                        modifier = Modifier
                    )
                    MainButton(
                        text = "채팅",
                        onClick = { onDialogChangeClick("operatorChat") },
                        modifier = Modifier
                    )
                }


            }

        }

    }
}

@Preview(showBackground = true)
@Composable
fun CommunityScreenPreview() {
    MypatTheme {
        CommunityScreen(
            userDataList = listOf(User(id = "auth")),
            situation = "chat",
//            chatMessages = emptyList()
        )
    }
}
