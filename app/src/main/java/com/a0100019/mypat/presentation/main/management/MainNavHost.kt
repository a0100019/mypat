package com.a0100019.mypat.presentation.main.management

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.a0100019.mypat.presentation.daily.DailyScreen
import com.a0100019.mypat.presentation.daily.diary.DiaryScreen
import com.a0100019.mypat.presentation.daily.diary.DiaryViewModel
import com.a0100019.mypat.presentation.daily.diary.DiaryWriteScreen
import com.a0100019.mypat.presentation.daily.english.EnglishScreen
import com.a0100019.mypat.presentation.daily.koreanIdiom.KoreanIdiomScreen
import com.a0100019.mypat.presentation.daily.walk.WalkScreen
import com.a0100019.mypat.presentation.index.IndexScreen
import com.a0100019.mypat.presentation.main.MainScreen
import com.a0100019.mypat.presentation.store.StoreScreen

@Composable
fun MainNavHost() {
    // 엑티비티 전환은 네비호스트 사용안함!!
    val navController = rememberNavController()

    //뷰모델 공유하고 싶으면 이렇게 하기
    val diaryViewModel: DiaryViewModel = hiltViewModel()

    NavHost(
        navController = navController,
        startDestination = MainRoute.MainScreen.name,
    ){
        composable(route = MainRoute.MainScreen.name) {
            MainScreen(
                onDailyNavigateClick = {
                    navController.navigate(route = MainRoute.DailyScreen.name)
                },
                onStoreNavigateClick = {
                    navController.navigate(route = MainRoute.StoreScreen.name)
                },
                onIndexNavigateClick = {
                    navController.navigate(route = MainRoute.IndexScreen.name)
                },
            )
        }

        composable(route = MainRoute.DailyScreen.name) {
            DailyScreen(
                onDiaryNavigateClick = {
                    navController.navigate(route = MainRoute.DiaryScreen.name)
                },
                onEnglishNavigateClick = {
                    navController.navigate(route = MainRoute.EnglishScreen.name)
                },
                onKoreanIdiomNavigateClick = {
                    navController.navigate(route = MainRoute.KoreanIdiomScreen.name)
                },
                onWalkNavigateClick = {
                    navController.navigate(route = MainRoute.WalkScreen.name)
                },
            )
        }


        composable(route = MainRoute.StoreScreen.name) {
            StoreScreen()
        }

        composable(route = MainRoute.IndexScreen.name) {
            IndexScreen()
        }

        composable(route = MainRoute.DiaryScreen.name) {
            DiaryScreen(
                diaryViewModel = diaryViewModel, // ViewModel을 전달
                onDiaryClick = {
                    navController.navigate(route = MainRoute.DiaryWriteScreen.name)
                }
            )
        }

        composable(route = MainRoute.DiaryWriteScreen.name) {
            // 같은 ViewModel 사용
            DiaryWriteScreen(
                diaryViewModel = diaryViewModel,
                //()가 있는 함수는 {} 안에 해야함
                popBackStack = { navController.popBackStack() }
            )
        }

        composable(route = MainRoute.EnglishScreen.name) {
            EnglishScreen()
        }

        composable(route = MainRoute.KoreanIdiomScreen.name) {
            KoreanIdiomScreen()
        }

        composable(route = MainRoute.WalkScreen.name) {
            WalkScreen()
        }

//
//        composable(route = LoginRoute.SignUpScreen.name) {
//
//            SignUpScreen(
//                onNavigationToLoginScreen = {
//                    navController.navigate(
//                        route = LoginRoute.LoginScreen.name,
//                        navOptions = navOptions {
//                            //웰컴화면 이후 화면들을 제거, 만약 inclusive = ture까지하면 웰컴도 제거
//                            popUpTo(LoginRoute.WelcomeScreen.name)
//                        }
//                    )
//                }
//            )
//
//        }

    }
}