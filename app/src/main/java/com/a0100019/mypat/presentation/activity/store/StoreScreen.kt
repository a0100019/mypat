package com.a0100019.mypat.presentation.activity.store

import android.app.Activity
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.a0100019.mypat.R
import com.a0100019.mypat.data.room.area.Area
import com.a0100019.mypat.data.room.item.Item
import com.a0100019.mypat.data.room.pat.Pat
import com.a0100019.mypat.data.room.user.User
import com.a0100019.mypat.domain.AppBgmManager
import com.a0100019.mypat.presentation.activity.index.IndexItemDialog
import com.a0100019.mypat.presentation.activity.index.IndexAreaDialog
import com.a0100019.mypat.presentation.activity.index.IndexPatDialog
import com.a0100019.mypat.presentation.main.mainDialog.SimpleAlertDialog
import com.a0100019.mypat.presentation.setting.Donation
import com.a0100019.mypat.presentation.setting.DonationDialog
import com.a0100019.mypat.presentation.ui.SfxPlayer
import com.a0100019.mypat.presentation.ui.component.MainButton
import com.a0100019.mypat.presentation.ui.image.etc.BackGroundImage
import com.a0100019.mypat.presentation.ui.image.etc.JustImage
import com.a0100019.mypat.presentation.ui.theme.MypatTheme
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

@Composable
fun StoreScreen(
    storeViewModel: StoreViewModel = hiltViewModel(),
    billingManager: BillingManager,
    popBackStack: () -> Unit = {}
) {
    val storeState: StoreState = storeViewModel.collectAsState().value

    val context = LocalContext.current
    val activity = context as? Activity   // ✅ 프리뷰 안전

    // 🔑 결제 이벤트 연결 (한 번만)
    LaunchedEffect(Unit) {
        billingManager.setBillingEventListener { event ->
            when (event) {
                BillingEvent.PurchaseSuccess -> {
                    storeViewModel.onPurchaseSuccess()
                }
                is BillingEvent.PurchaseFailed -> {
                    storeViewModel.onPurchaseFail()
                }
            }
        }
    }

    storeViewModel.collectSideEffect { sideEffect ->
        when (sideEffect) {
            is StoreSideEffect.Toast -> {
                Toast.makeText(context, sideEffect.message, Toast.LENGTH_SHORT).show()
            }

            StoreSideEffect.StartDonatePurchase -> {
                activity?.let {
                    Log.d("BILLING", "결제 시작")
                    billingManager.startPurchase(it, "remove_ads")
                }
            }
        }
    }

    StoreScreen(
        onDialogCloseClick = storeViewModel::onDialogCloseClick,
        onPatRoomUpClick = storeViewModel::onPatRoomUpClick,
        onSimpleDialog = storeViewModel::onSimpleDialog,
        onItemRoomUpClick = storeViewModel::onItemRoomUpClick,
        onTextChange = storeViewModel::onTextChange,
        onShowDialogChange = storeViewModel::changeShowDialog,
        onNameChangeConfirm = storeViewModel::onNameChangeConfirm,
        onNameChangeClick = storeViewModel::onNameChangeClick,
        onMoneyChangeClick = storeViewModel::onMoneyChangeClick,
        onPatStoreClick = storeViewModel::onPatStoreClick,
        onPatEggClick = storeViewModel::onPatEggClick,
        onPatSelectClick = storeViewModel::onPatSelectClick,
        onItemClick = storeViewModel::onItemClick,
        onItemStoreClick = storeViewModel::onItemStoreClick,
        onItemSelectClick = storeViewModel::onItemSelectClick,
        onItemSelectCloseClick = storeViewModel::onItemSelectCloseClick,
        popBackStack = popBackStack,
        onDonateClick = storeViewModel::onDonateClick,
        loadDonationList = storeViewModel::loadDonationList,

        newPat = storeState.newPat,
        userData = storeState.userData,
        newItem = storeState.newItem,
        newArea = storeState.newArea,
        showDialog = storeState.showDialog,
        simpleDialogState = storeState.simpleDialogState,
        text = storeState.text,
        patEggDataList = storeState.patEggDataList,
        patStoreDataList = storeState.patStoreDataList,
        patSelectIndexList = storeState.patSelectIndexList,
        selectPatData = storeState.selectPatData,
        selectItemData = storeState.selectItemData,
        selectAreaData = storeState.selectAreaData,
        shuffledItemDataList = storeState.shuffledItemDataList,
        patPrice = storeState.patPrice,
        itemPrice = storeState.itemPrice,
        patSpacePrice = storeState.patSpacePrice,
        itemSpacePrice = storeState.itemSpacePrice,
        pay = storeState.pay,
        donationList = storeState.donationList,

    )
}

@Composable
fun StoreScreen(
    onDialogCloseClick: () -> Unit,
    onPatRoomUpClick: () -> Unit,
    onItemRoomUpClick: () -> Unit,
    onSimpleDialog: (String) -> Unit,
    onTextChange: (String) -> Unit,
    onShowDialogChange: (String) -> Unit,
    onNameChangeClick: () -> Unit,
    onNameChangeConfirm: () -> Unit,
    onMoneyChangeClick: () -> Unit,
    onPatStoreClick: () -> Unit,
    onPatEggClick: (Int) -> Unit,
    onPatSelectClick: () -> Unit,
    onItemClick: (String) -> Unit,
    onItemStoreClick: () -> Unit,
    onItemSelectClick: () -> Unit,
    onItemSelectCloseClick: () -> Unit,
    popBackStack: () -> Unit = {},
    onDonateClick: () -> Unit = {},
    loadDonationList: () -> Unit = {},

    newPat: Pat?,
    newItem: Item?,
    newArea: Area?,
    userData: List<User>,
    showDialog: String,
    simpleDialogState: String,
    text: String,
    patEggDataList: List<Pat>?,
    patStoreDataList: List<Pat>?,
    patSelectIndexList: List<Int>,
    selectPatData: Pat?,
    selectItemData: Item?,
    selectAreaData: Area?,
    shuffledItemDataList: List<String>?,
    patPrice: Int = 0,
    itemPrice: Int = 0,
    patSpacePrice: Int = 0,
    itemSpacePrice: Int = 0,
    pay: String = "0",
    donationList: List<Donation> = emptyList(),

    ) {

    val context = LocalContext.current
    val prefs = context.getSharedPreferences("bgm_prefs", Context.MODE_PRIVATE)
    val bgmOn = prefs.getBoolean("bgmOn", true)
    val adPrefs = context.getSharedPreferences("ad_prefs", Context.MODE_PRIVATE)

    if (selectPatData != null) {
        PatSelectDialog(
            onSelectClick = onPatSelectClick,
            patData = selectPatData
        )
    }

    if (selectItemData != null) {
        ItemSelectDialog(
            onCloseClick = onItemSelectCloseClick,
            onSelectClick = onItemSelectClick,
            itemData = "${selectItemData.url}@${selectItemData.name}"
        )
    }

    if (selectAreaData != null) {
        AppBgmManager.pause()
        ItemSelectDialog(
            onCloseClick = onItemSelectCloseClick,
            onSelectClick = onItemSelectClick,
            itemData = "${selectAreaData.url}@${selectAreaData.name}"
        )
    } else {
        if (bgmOn) {
            AppBgmManager.play()
        }
    }

    // 다이얼로그 표시
    if (newPat != null) {
        IndexPatDialog(
            onClose = onDialogCloseClick,
            patData = newPat,
        )
    }

    if (newItem != null) {
        IndexItemDialog(
            onClose = onDialogCloseClick,
            itemData = newItem,
        )
    }

    if (newArea != null) {
        IndexAreaDialog(
            onClose = onDialogCloseClick,
            areaData = newArea,
        )
    }

    when (showDialog) {
        "pat" -> RoomUpDialog(
            onClose = onDialogCloseClick,
            userData = userData,
            showRoomUpDialog = showDialog
        )
        "item" -> RoomUpDialog(
            onClose = onDialogCloseClick,
            userData = userData,
            showRoomUpDialog = showDialog
        )
        "name" -> NameChangeDialog(
            onClose = onDialogCloseClick,
            userData = userData,
            onNameTextChange = onTextChange,
            onConfirmClick = onNameChangeConfirm,
            newName = text
        )
        "patStore" -> PatStoreDialog(
            onClose = { },
            patData = patStoreDataList,
            patEggData = patEggDataList,
            onPatEggClick = onPatEggClick,
            selectIndexList = patSelectIndexList
        )
        "itemStore" -> ItemStoreDialog(
            onClose = { },
            itemData = shuffledItemDataList,
            onItemClick = onItemClick,
        )
        "donate" -> DonateDialog(
            onClose = onDialogCloseClick,
            onTextChange = onTextChange,
            text = text,
            onConfirmClick = onDonateClick
        )
        "donation" -> DonationDialog(
            onClose = onDialogCloseClick,
            donationList = donationList
        )
        "removeAdSuccess" -> SimpleAlertDialog(
            onConfirmClick = {
                adPrefs
                    .edit()
                    .putString("banner", "2")
                    .apply()
                onDialogCloseClick()
                             },
            text = "모든 광고가 제거되었습니다! 방명록은 설정에서 확인할 수 있으며, 상단의 베너 광고는 다음 접속부터 제거됩니다. 감사합니다 :)",
            onDismissOn = false,
        )

    }

    if (simpleDialogState != "") {
        //다이얼로그 맨트 변경 시 얘도 바꿔야하는 거 주의!!!!!!!
        SimpleAlertDialog (
            onDismissClick = { onSimpleDialog("") },
            onConfirmClick = {
                when(simpleDialogState) {
                    "펫을 뽑으시겠습니까?" -> onPatStoreClick()
                    "아이템을 뽑으시겠습니까?" -> onItemStoreClick()
                    "펫 공간을 늘리겠습니까?" -> onPatRoomUpClick()
                    "아이템 공간을 늘리겠습니까?" -> onItemRoomUpClick()
                    "부적절한 닉네임(욕설, 부적절한 내용, 운영자 사칭 등)일 경우, 경고 없이 제제를 받을 수 있습니다. 변경하겠습니까?" -> {
                        onNameChangeClick()
                        SfxPlayer.play(context, R.raw.positive11)
                    }
                    "화폐를 변경하겠습니까?" -> {
                        onMoneyChangeClick()
                        SfxPlayer.play(context, R.raw.counter2)
                    }
                }
                onSimpleDialog("")
            },
            text = simpleDialogState
        )
    }

    Surface(
        modifier = Modifier
            .fillMaxSize()
    ) {

        BackGroundImage()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 6.dp, start = 24.dp, end = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // 1. 헤더 영역 (타이틀 & 닫기 버튼)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp), // 상하 여백 추가
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "상점",
                    // displaySmall보다 조금 더 정갈한 headlineMedium 추천
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 2.sp
                    ),
                    color = MaterialTheme.colorScheme.onBackground
                )

                JustImage(
                    filePath = "etc/exit.png",
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .size(30.dp)
                        .clickable {
                            popBackStack()
                        }
                )
            }

// 2. 재화 정보 영역 (한 줄의 깔끔한 상태바 형태)
            Surface(
                modifier = Modifier
                    .fillMaxWidth(0.95f) // 약간의 여백
                    .padding(vertical = 12.dp),
                shape = RoundedCornerShape(24.dp), // 둥근 캡슐 모양
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f), // 은은한 배경색
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant) // 얇은 테두리
            ) {
                Row(
                    modifier = Modifier
                        .padding(horizontal = 20.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // 햇살 정보
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        JustImage(
                            filePath = "etc/sun.png",
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "${userData.find { it.id == "money" }?.value ?: 0}",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    // 중앙 구분선 (선택 사항)
                    Box(
                        modifier = Modifier
                            .size(1.dp, 16.dp)
                            .background(MaterialTheme.colorScheme.outlineVariant)
                    )

                    // 달빛 정보
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        JustImage(
                            filePath = "etc/moon.png",
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "${userData.find { it.id == "money" }?.value2 ?: 0}",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }
                }
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                item {

                    Text(
                        text = "아래로 드래그하세요",
                        style = MaterialTheme.typography.titleSmall
                    )

                }

                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp) // 간격을 일정하게 유지
                    ) {
                        // --- [1] 펫 뽑기 카드 ---
                        val interactionSource = remember { MutableInteractionSource() }
                        val isPressed by interactionSource.collectIsPressedAsState()
                        val scale by animateFloatAsState(if (isPressed) 0.94f else 1f, label = "scale")

                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .graphicsLayer {
                                    scaleX = scale
                                    scaleY = scale
                                }
                                .clickable(
                                    interactionSource = interactionSource,
                                    indication = null,
                                    onClick = { onSimpleDialog("펫을 뽑으시겠습니까?") }
                                ),
                            shape = RoundedCornerShape(28.dp), // 더 둥글게 해서 귀여운 느낌 강조
                            color = MaterialTheme.colorScheme.surface, // 너무 어두운 scrim 대신 밝은 surface 추천
                            border = BorderStroke(3.dp, MaterialTheme.colorScheme.primaryContainer),
                            shadowElevation = 6.dp // 입체감 추가
                        ) {
                            Column(
                                modifier = Modifier
                                    .padding(16.dp)
                                    .fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = "펫 뽑기",
                                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onSurface
                                )

                                // 기계 이미지 영역 (배경에 살짝 원형 강조)
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier
                                        .size(130.dp)
                                        .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f), CircleShape)
                                ) {
                                    JustImage(
                                        filePath = "etc/pat_machine.json",
                                        modifier = Modifier.size(110.dp)
                                    )
                                }

                                // 가격 표시 바
                                Surface(
                                    shape = CircleShape,
                                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                                    modifier = Modifier.padding(top = 4.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "$patPrice",
                                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                                        )
                                        JustImage(
                                            filePath = "etc/sun.png",
                                            modifier = Modifier.size(18.dp).padding(start = 4.dp)
                                        )
                                    }
                                }
                            }
                        }

                        // --- [2] 아이템 뽑기 카드 ---
                        val interactionSource2 = remember { MutableInteractionSource() }
                        val isPressed2 by interactionSource2.collectIsPressedAsState()
                        val scale2 by animateFloatAsState(if (isPressed2) 0.94f else 1f, label = "scale")

                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .graphicsLayer {
                                    scaleX = scale2
                                    scaleY = scale2
                                }
                                .clickable(
                                    interactionSource = interactionSource2,
                                    indication = null,
                                    onClick = { onSimpleDialog("아이템을 뽑으시겠습니까?") }
                                ),
                            shape = RoundedCornerShape(28.dp),
                            color = MaterialTheme.colorScheme.surface,
                            border = BorderStroke(3.dp, MaterialTheme.colorScheme.secondaryContainer),
                            shadowElevation = 6.dp
                        ) {
                            Column(
                                modifier = Modifier
                                    .padding(16.dp)
                                    .fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = "아이템 뽑기",
                                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onSurface
                                )

                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier
                                        .size(130.dp)
                                        .background(MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.2f), CircleShape)
                                ) {
                                    JustImage(
                                        filePath = "etc/item_machine.json",
                                        modifier = Modifier.size(90.dp) // 아이템 기계는 약간 작게 조절 (패딩 고려)
                                    )
                                }

                                Surface(
                                    shape = CircleShape,
                                    color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f),
                                    modifier = Modifier.padding(top = 4.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "$itemPrice",
                                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                                        )
                                        JustImage(
                                            filePath = "etc/moon.png",
                                            modifier = Modifier.size(18.dp).padding(start = 4.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // 닉네임 변경 아이템
                item {
                    val interactionSource = remember { MutableInteractionSource() }
                    val isPressed by interactionSource.collectIsPressedAsState()
                    val scale by animateFloatAsState(if (isPressed) 0.96f else 1f, label = "scale")

                    val currentName = userData.find { it.id == "name" }?.value ?: "이웃"
                    val price = if (currentName == "이웃") "0" else "3"

                    Surface(
                        shape = RoundedCornerShape(24.dp),
                        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f),
                        border = BorderStroke(2.dp, MaterialTheme.colorScheme.outlineVariant),
                        shadowElevation = 2.dp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp)
                            .graphicsLayer {
                                scaleX = scale
                                scaleY = scale
                            }
                            .clickable(
                                interactionSource = interactionSource,
                                indication = null,
                                onClick = { onShowDialogChange("name") }
                            )
                    ) {
                        Row(
                            modifier = Modifier
                                .padding(20.dp)
                                .fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "닉네임 변경",
                                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                                )
                                Text(
                                    text = "현재 이름: $currentName",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            // 가격 표시 뱃지
                            Surface(
                                shape = CircleShape,
                                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f),
                                modifier = Modifier.padding(start = 8.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = price,
                                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                                    )
                                    JustImage(
                                        filePath = "etc/sun.png",
                                        modifier = Modifier.size(18.dp).padding(start = 4.dp)
                                    )
                                }
                            }
                        }
                    }
                }

// 화폐 교환 아이템
                item {
                    val interactionSource = remember { MutableInteractionSource() }
                    val isPressed by interactionSource.collectIsPressedAsState()
                    val scale by animateFloatAsState(if (isPressed) 0.96f else 1f, label = "scale")

                    Surface(
                        shape = RoundedCornerShape(24.dp),
                        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f),
                        border = BorderStroke(2.dp, MaterialTheme.colorScheme.outlineVariant),
                        shadowElevation = 2.dp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp)
                            .graphicsLayer {
                                scaleX = scale
                                scaleY = scale
                            }
                            .clickable(
                                interactionSource = interactionSource,
                                indication = null,
                                onClick = { onSimpleDialog("화폐를 변경하겠습니까?") }
                            )
                    ) {
                        Row(
                            modifier = Modifier
                                .padding(20.dp)
                                .fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "화폐 교환",
                                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                                )
                                Text(
                                    text = "햇살을 달빛으로 환전합니다",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            // 교환 비율 표시 (화살표 형태)
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .background(MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.4f), CircleShape)
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text("1", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold))
                                JustImage(filePath = "etc/sun.png", modifier = Modifier.size(16.dp).padding(horizontal = 2.dp))
                                Text("→", modifier = Modifier.padding(horizontal = 4.dp))
                                Text("3000", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold))
                                JustImage(filePath = "etc/moon.png", modifier = Modifier.size(16.dp).padding(horizontal = 2.dp))
                            }
                        }
                    }
                }

                if(pay == "0") {
                    item {
                        val interactionSource = remember { MutableInteractionSource() }
                        val isPressed by interactionSource.collectIsPressedAsState()
                        val scale by animateFloatAsState(
                            targetValue = if (isPressed) 0.96f else 1f,
                            label = "scale"
                        )

                        Surface(
                            shape = RoundedCornerShape(28.dp), // 더 둥글고 부드럽게
                            color = Color(0xFFFFF1F1),        // 맑고 연한 핑크 배경
                            border = BorderStroke(2.dp, Color(0xFFFFB2B2)), // 테두리는 살짝 얇게
                            shadowElevation = 8.dp,            // 입체감 부여
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 10.dp)
                                .graphicsLayer {
                                    scaleX = scale
                                    scaleY = scale
                                }
                                .clickable(
                                    interactionSource = interactionSource,
                                    indication = null,
                                    onClick = { onShowDialogChange("donate") }
                                )
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(20.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                // 🔹 상단: 타이틀 및 방명록 버튼
                                Box(
                                    modifier = Modifier.fillMaxWidth(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    // 메인 타이틀
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text(
                                            text = "광고 제거",
                                            style = MaterialTheme.typography.headlineSmall.copy(
                                                fontWeight = FontWeight.ExtraBold,
                                                letterSpacing = 1.sp
                                            ),
                                            color = Color(0xFFD32F2F)
                                        )
                                    }

                                    // 방명록 버튼을 작고 예쁜 카드 형태로 우측 배치
                                    Surface(
                                        onClick = { onShowDialogChange("donation") },
                                        shape = RoundedCornerShape(12.dp),
                                        color = Color.White.copy(alpha = 0.6f),
                                        border = BorderStroke(1.dp, Color(0xFFFF9A9A)),
                                        modifier = Modifier.align(Alignment.CenterEnd)
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text("📝", fontSize = 14.sp)
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(
                                                text = "방명록",
                                                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                                                color = Color(0xFFD32F2F)
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                // 🔹 중단: 메시지 (카드 형태의 말풍선 느낌)
                                Surface(
                                    color = Color.White.copy(alpha = 0.4f),
                                    shape = RoundedCornerShape(16.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = "방명록을 남길 수 있으며,\n후원은 개발자에게 큰 힘이 됩니다.",
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            lineHeight = 20.sp,
                                            fontWeight = FontWeight.Medium
                                        ),
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.padding(12.dp),
                                        color = Color(0xFF634D4D)
                                    )
                                }

                                Spacer(modifier = Modifier.height(20.dp))

                                // 🔹 하단: 가격 버튼 (더욱 강조된 디자인)
                                Surface(
                                    shape = CircleShape,
                                    color = Color(0xFFFF7070), // 명도가 높은 레드
                                    shadowElevation = 4.dp
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 32.dp, vertical = 10.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "₩ 2,200",
                                            style = MaterialTheme.typography.titleMedium.copy(
                                                fontWeight = FontWeight.Bold
                                            ),
                                            color = Color.White
                                        )
                                    }
                                }
                            }
                        }
                    }


                }


            }

        }
    }


}

@Preview(showBackground = true)
@Composable
fun StoreScreenPreview() {
    MypatTheme {
        StoreScreen(
            onDialogCloseClick = {},
            onPatRoomUpClick = {},
            onSimpleDialog = {},
            onItemRoomUpClick = {},
            onTextChange = {},
            onShowDialogChange = {},
            onNameChangeConfirm = {},
            onNameChangeClick = {},
            onMoneyChangeClick = {},
            onPatStoreClick = {},
            onPatEggClick = {},
            onPatSelectClick = {},
            onItemClick = {},
            onItemStoreClick = {},
            onItemSelectClick = {},
            onItemSelectCloseClick = {},

            newPat = null,
            userData = emptyList(),
            showDialog = "",
            simpleDialogState = "",
            newArea = null,
            newItem = null,
            text = "",
            patEggDataList = emptyList(),
            patStoreDataList = emptyList(),
            patSelectIndexList = emptyList(),
            selectPatData = null,
            shuffledItemDataList = emptyList(),
            selectItemData = null,
            selectAreaData = null

        )
    }
}
