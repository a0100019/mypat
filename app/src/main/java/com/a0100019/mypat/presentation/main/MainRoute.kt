package com.a0100019.mypat.presentation.main

sealed class MainRoute(
    val name:String
) {

    object MainScreen : MainRoute("MainScreen")

    object DailyScreen : MainRoute("DailyScreen")

    object GameScreen : MainRoute("GameScreen")

    object StoreScreen : MainRoute("StoreScreen")

    object IndexScreen : MainRoute("IndexScreen")


    object DiaryScreen : MainRoute("DiaryScreen")

    object EnglishScreen : MainRoute("EnglishScreen")

    object KoreanIdiomScreen : MainRoute("KoreanIdiomScreen")

    object WalkScreen : MainRoute("WalkScreen")

}