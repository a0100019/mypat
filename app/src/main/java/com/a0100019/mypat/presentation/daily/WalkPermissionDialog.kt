package com.a0100019.mypat.presentation.daily

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.a0100019.mypat.data.room.pat.Pat
import com.a0100019.mypat.presentation.ui.component.MainButton
import com.a0100019.mypat.presentation.ui.theme.MypatTheme
import android.provider.Settings
import android.net.Uri

@Composable
fun WalkPermissionDialog(
    onCloseClick: () -> Unit = {},
    onCheckClick: () -> Unit = {},

) {

    Dialog(
        onDismissRequest = {  }
    ) {
        Column (

        ) {
            Button(onClick = {
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                    data = Uri.parse("package:${context.packageName}")
                }
                context.startActivity(intent)
            }) {
                Text("설정에서 권한을 허용해주세요")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun WalkPermissionDialogPreview() {
    MypatTheme {
        WalkPermissionDialog(
        )
    }
}