package com.a0100019.mypat.presentation.main

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.ui.graphics.vector.ImageVector

enum class MainRoute (
    val route: String,
    val contentDescription:String,
    val icon:ImageVector
){
    SECOND(route = "SecondScreen", contentDescription = "222", icon = Icons.Filled.Home),
    MAIN(route = "MainScreen", contentDescription = "메인", icon = Icons.Filled.AddCircle),
    THIRD(route = "ThirdScreen", contentDescription = "333", icon = Icons.Filled.AccountCircle),

}