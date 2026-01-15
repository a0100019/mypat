package com.a0100019.mypat.presentation.activity

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
import com.a0100019.mypat.presentation.main.management.loading.LoadingSideEffect
import com.a0100019.mypat.presentation.main.management.loading.LoadingState
import com.a0100019.mypat.presentation.main.management.loading.LoadingViewModel
import com.a0100019.mypat.presentation.ui.component.MainButton
import com.a0100019.mypat.presentation.ui.image.etc.BackGroundImage
import com.a0100019.mypat.presentation.ui.theme.MypatTheme
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

@Composable
fun ActivityContainerScreen(
    activityViewModel: ActivityViewModel = hiltViewModel(),

    popBackStack: () -> Unit = {},
    onDailyNavigateClick: () -> Unit = {},
    onIndexNavigateClick: () -> Unit = {},
    onInformationNavigateClick: () -> Unit = {},
    onStoreNavigateClick: () -> Unit = {},

    ) {

    val activityState : ActivityState = activityViewModel.collectAsState().value

    val context = LocalContext.current

    activityViewModel.collectSideEffect { sideEffect ->
        when (sideEffect) {
            is ActivitySideEffect.Toast -> Toast.makeText(context, sideEffect.message, Toast.LENGTH_SHORT).show()
        }
    }

    ActivityScreen(
        situation = activityState.situation,

        onClose = activityViewModel::onClose,
        popBackStack = popBackStack,
        onDailyNavigateClick = onDailyNavigateClick,
        onIndexNavigateClick = onIndexNavigateClick,
        onInformationNavigateClick = onInformationNavigateClick,
        onStoreNavigateClick = onStoreNavigateClick
    )
}

@Composable
fun ActivityScreen(
    situation: String = "",

    onClose : () -> Unit = {},
    popBackStack: () -> Unit = {},
    onDailyNavigateClick: () -> Unit = {},
    onIndexNavigateClick: () -> Unit = {},
    onInformationNavigateClick: () -> Unit = {},
    onStoreNavigateClick: () -> Unit = {},

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

            MainButton(
                text = "일일미션",
                onClick = onDailyNavigateClick
            )

            MainButton(
                text = "도감",
                onClick = onIndexNavigateClick
            )

            MainButton(
                text = "내정보",
                onClick = onInformationNavigateClick
            )

            MainButton(
                text = "상점",
                onClick = onStoreNavigateClick
            )

            Row {
                MainButton(
                    onClick = popBackStack,
                    text = "닫기"
                )
            }

        }

    }
}

@Preview(showBackground = true)
@Composable
fun ActivityScreenPreview() {
    MypatTheme {
        ActivityScreen(
            situation = ""
        )
    }
}