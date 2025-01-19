package com.a0100019.mypat.presentation.index

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.a0100019.mypat.data.room.item.Item
import com.a0100019.mypat.data.room.pet.Pat
import com.a0100019.mypat.presentation.loading.LoadingSideEffect
import com.a0100019.mypat.presentation.loading.LoadingState
import com.a0100019.mypat.presentation.loading.LoadingViewModel
import com.a0100019.mypat.ui.theme.MypatTheme
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

@Composable
fun IndexScreen(
    indexViewModel: IndexViewModel = hiltViewModel()

) {

    val indexState : IndexState = indexViewModel.collectAsState().value

    val context = LocalContext.current

    indexViewModel.collectSideEffect { sideEffect ->
        when (sideEffect) {
            is IndexSideEffect.Toast -> Toast.makeText(context, sideEffect.message, Toast.LENGTH_SHORT).show()
        }
    }

    IndexScreen(
        allPatDataList = indexState.allPatDataList,
        allItemDataList = indexState.allItemDataList,
        allMapDataList = indexState.allMapDataList
    )
}



@Composable
fun IndexScreen(
    allPatDataList: List<Pat>,
    allItemDataList: List<Item>,
    allMapDataList: List<Item>
) {
    // Fullscreen container
    Column(modifier = Modifier.fillMaxSize()) {
        Text("Pat")
        LazyVerticalGrid(
            columns = GridCells.Fixed(4), // 한 줄에 5개씩 배치
            modifier = Modifier.fillMaxSize()
        ) {

        }
    }
}

@Preview(showBackground = true)
@Composable
fun IndexScreenPreview() {
    MypatTheme {
        IndexScreen(
            allPatDataList = listOf(Pat(url = "pat/cat.json")),
            allItemDataList = listOf(Item(url = "item/table.png")),
            allMapDataList = listOf(Item(url = "item/forest.png")),
        )
    }
}