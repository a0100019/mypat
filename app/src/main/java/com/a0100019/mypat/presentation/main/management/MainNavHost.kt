package com.a0100019.mypat.presentation.main.management

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.a0100019.mypat.presentation.community.CommunityScreen
import com.a0100019.mypat.presentation.daily.DailyScreen
import com.a0100019.mypat.presentation.daily.diary.DiaryScreen
import com.a0100019.mypat.presentation.daily.diary.DiaryViewModel
import com.a0100019.mypat.presentation.daily.diary.DiaryWriteScreen
import com.a0100019.mypat.presentation.daily.english.EnglishScreen
import com.a0100019.mypat.presentation.daily.korean.KoreanScreen
import com.a0100019.mypat.presentation.daily.walk.WalkScreen
import com.a0100019.mypat.presentation.game.firstGame.FirstGameScreen
import com.a0100019.mypat.presentation.game.secondGame.SecondGameScreen
import com.a0100019.mypat.presentation.game.thirdGame.ThirdGameScreen
import com.a0100019.mypat.presentation.index.IndexScreen
import com.a0100019.mypat.presentation.information.InformationScreen
import com.a0100019.mypat.presentation.login.LoginScreen
import com.a0100019.mypat.presentation.main.MainScreen
import com.a0100019.mypat.presentation.setting.SettingScreen
import com.a0100019.mypat.presentation.setting.WebViewScreen
import com.a0100019.mypat.presentation.store.StoreScreen

@Composable
fun MainNavHost() {
    // 엑티비티 전환은 네비호스트 사용안함!!
    val navController = rememberNavController()

    //뷰모델 공유하고 싶으면 이렇게 하기
    val diaryViewModel: DiaryViewModel = hiltViewModel()

    NavHost(
        navController = navController,
        startDestination = MainRoute.LoginScreen.name,
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
                onCommunityNavigateClick = {
                    navController.navigate(route = MainRoute.CommunityScreen.name)
                },
                onInformationNavigateClick = {
                    navController.navigate(route = MainRoute.InformationScreen.name)
                },
                onSettingNavigateClick = {
                    navController.navigate(route = MainRoute.SettingScreen.name)
                },
                onFirstGameNavigateClick = {
                    navController.navigate(route = MainRoute.FirstGameScreen.name)
                },
                onSecondGameNavigateClick = {
                    navController.navigate(route = MainRoute.SecondGameScreen.name)
                },
                onThirdGameNavigateClick = {
                    navController.navigate(route = MainRoute.ThirdGameScreen.name)
                }
            )
        }

        composable(route = MainRoute.LoginScreen.name) {
            LoginScreen(navController = navController)
        }


        composable(route = MainRoute.DailyScreen.name) {
            DailyScreen(
                onDiaryNavigateClick = {
                    navController.navigate(route = MainRoute.DiaryScreen.name)
                },
                onEnglishNavigateClick = {
                    navController.navigate(route = MainRoute.EnglishScreen.name)
                },
                onKoreanNavigateClick = {
                    navController.navigate(route = MainRoute.KoreanScreen.name)
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

        composable(route = MainRoute.InformationScreen.name) {
            InformationScreen()
        }

        composable(route = MainRoute.CommunityScreen.name) {
            CommunityScreen()
        }



        composable(route = MainRoute.FirstGameScreen.name) {
            FirstGameScreen(
                popBackStack = { navController.popBackStack() }
            )
        }

        composable(route = MainRoute.SecondGameScreen.name) {
            SecondGameScreen(
                popBackStack = { navController.popBackStack() }
            )
        }

        composable(route = MainRoute.ThirdGameScreen.name) {
            ThirdGameScreen(
                popBackStack = { navController.popBackStack() }
            )
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

        composable(route = MainRoute.KoreanScreen.name) {
            KoreanScreen()
        }

        composable(route = MainRoute.SettingScreen.name) {
            SettingScreen(
                onNavigateToWebView = { url ->
                    navController.navigate("webview?url=${Uri.encode(url)}")
                }
            )
        }

        composable(route = MainRoute.WalkScreen.name) {
            WalkScreen()
        }

        composable(
            route = "webview?url={url}",
            arguments = listOf(navArgument("url") { type = NavType.StringType })
        ) { backStackEntry ->
            val url = backStackEntry.arguments?.getString("url") ?: ""
            WebViewScreen(url = Uri.decode(url))
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