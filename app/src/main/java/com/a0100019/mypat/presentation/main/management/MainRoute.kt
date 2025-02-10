package com.a0100019.mypat.presentation.main.management

sealed class MainRoute(
    val name:String
) {

    object MainScreen : MainRoute("MainScreen")

    object DailyScreen : MainRoute("DailyScreen")

    object StoreScreen : MainRoute("StoreScreen")

    object IndexScreen : MainRoute("IndexScreen")

    object FirstGameScreen : MainRoute("FirstGameScreen")
    object SecondGameScreen : MainRoute("SecondGameScreen")
    object ThirdGameScreen : MainRoute("ThirdGameScreen")


    object DiaryScreen : MainRoute("DiaryScreen")
    object DiaryWriteScreen : MainRoute("DiaryWriteScreen")

    object EnglishScreen : MainRoute("EnglishScreen")

    object KoreanIdiomScreen : MainRoute("KoreanIdiomScreen")

    object WalkScreen : MainRoute("WalkScreen")

}