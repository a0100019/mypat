package com.a0100019.mypat.presentation.activity.daily.walk

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.PowerManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import android.provider.Settings


@Composable
fun RequestPermissionScreen() {
    val context = LocalContext.current

    val permissionLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (!isGranted) {
                Toast.makeText(context, "걸음 수 측정을 위해 권한이 필요합니다.", Toast.LENGTH_SHORT).show()
            }
        }

    LaunchedEffect(Unit) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACTIVITY_RECOGNITION)
            != PackageManager.PERMISSION_GRANTED
        ) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                permissionLauncher.launch(Manifest.permission.ACTIVITY_RECOGNITION)
            }
        }
    }
}


@Composable
fun RequestNotificationPermissionScreen() {
    val context = LocalContext.current

    // Android 13 이상만 알림 권한 필요
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return

    val permissionLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission()
        ) { isGranted ->
            if (!isGranted) {
                Toast.makeText(context, "알림을 표시하려면 권한이 필요합니다.", Toast.LENGTH_SHORT).show()
            }
        }

    LaunchedEffect(Unit) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)
            != PackageManager.PERMISSION_GRANTED
        ) {
            permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }
}

@SuppressLint("BatteryLife")
@Composable
fun RequestBatteryPermissionScreen() {

    val context = LocalContext.current

    LaunchedEffect(Unit) {
        val intent = Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS).apply {
            data = Uri.parse("package:${context.packageName}")
        }
        context.startActivity(intent)
    }
}
