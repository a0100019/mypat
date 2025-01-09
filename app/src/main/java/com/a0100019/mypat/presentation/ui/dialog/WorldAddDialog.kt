package com.a0100019.mypat.presentation.ui.dialog


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.a0100019.mypat.data.room.pet.Pat
import com.a0100019.mypat.data.room.world.World
import com.a0100019.mypat.presentation.ui.image.pat.AddDialogPatImage
import com.a0100019.mypat.ui.theme.MypatTheme


@Composable
fun WorldAddDialog(
    onClose: () -> Unit,
    allPatDataList: List<Pat>,
    patWorldDataList: List<World>,
    onAddPatImageClick: (String) -> Unit
) {

    val patOpenCount = patWorldDataList.count { it.type == "pat" && it.open == "1" }
    val patUseCount =  patWorldDataList.count { it.type == "pat" && it.value != "0" }

    Dialog(
        onDismissRequest = onClose
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.8f)
                .background(Color.White, shape = RoundedCornerShape(16.dp))
                .padding(16.dp)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {

                Text("Pat")
                Text("${patUseCount}/${patOpenCount}")
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .background(Color.Gray, shape = RoundedCornerShape(16.dp))
                        .padding(16.dp)
                ) {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(4), // 한 줄에 5개씩 배치
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(allPatDataList.size) { index ->
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                AddDialogPatImage(
                                    patData = allPatDataList[index],
                                    onAddPatImageClick = onAddPatImageClick
                                )
                                if (patWorldDataList.any { it.value == allPatDataList[index].id.toString() && it.type == "pat"}) {
                                    Text("마을")
                                }
                                Text(allPatDataList[index].name)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

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
    }
}


@Preview(showBackground = true)
@Composable
fun WorldAddDialogPreview() {
    MypatTheme {
        WorldAddDialog(
            onClose = {},
            allPatDataList = listOf(Pat(url = "pat/cat.json", name = "고양이"), Pat(url = "pat/cat.json"), Pat(url = "pat/cat.json"), Pat(url = "pat/cat.json"), Pat(url = "pat/cat.json")),
            patWorldDataList = listOf(World(id = "pat1", value = "1", open = "1", type = "pat"), World(id = "pat2", value = "2", open = "1", type = "pat"), World(id = "pat3", value = "0", open = "1", type = "pat")),
            onAddPatImageClick = {}
        )
    }
}