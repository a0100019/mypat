package com.a0100019.mypat.presentation.neighbor

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.a0100019.mypat.presentation.loading.LoadingSideEffect
import com.a0100019.mypat.presentation.loading.LoadingState
import com.a0100019.mypat.presentation.loading.LoadingViewModel
import com.a0100019.mypat.presentation.ui.component.MainButton
import com.a0100019.mypat.presentation.ui.image.etc.BackGroundImage
import com.a0100019.mypat.presentation.ui.theme.MypatTheme
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

@Composable
fun NeighborScreen(
    neighborViewModel: NeighborViewModel = hiltViewModel(),

    popBackStack: () -> Unit = {},
    onChatNavigateClick: () -> Unit = {},
    onCommunityNavigateClick: () -> Unit = {},
    onBoardNavigateClick: () -> Unit = {},
) {

    val neighborState : NeighborState = neighborViewModel.collectAsState().value

    val context = LocalContext.current

    neighborViewModel.collectSideEffect { sideEffect ->
        when (sideEffect) {
            is NeighborSideEffect.Toast -> Toast.makeText(context, sideEffect.message, Toast.LENGTH_SHORT).show()
        }
    }

    NeighborScreen(
        onClose = neighborViewModel::onClose,

        popBackStack = popBackStack,
        onChatNavigateClick = onChatNavigateClick,
        onCommunityNavigateClick = onCommunityNavigateClick,
        onBoardNavigateClick = onBoardNavigateClick

    )
}

@Composable
fun NeighborScreen(
    text: String = "",

    onClose : () -> Unit = {},

    popBackStack: () -> Unit = {},
    onCommunityNavigateClick: () -> Unit = {},
    onChatNavigateClick: () -> Unit = {},
    onBoardNavigateClick: () -> Unit = {}

) {
    Surface(
        modifier = Modifier
            .fillMaxSize()
    ) {

        BackGroundImage()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Row {
                MainButton(
                    onClick = popBackStack,
                    text = "닫기"
                )
            }

            MainButton(
                onClick = onCommunityNavigateClick,
                text = "마을"
            )

            MainButton(
                onClick = onChatNavigateClick,
                text = "채팅"
            )

            MainButton(
                onClick = onBoardNavigateClick,
                text = "게시판"
            )

        }

    }
}

@Preview(showBackground = true)
@Composable
fun NeighborScreenPreview() {
    MypatTheme {
        NeighborScreen(
            text = ""
        )
    }
}