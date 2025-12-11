package com.a0100019.mypat.presentation.main.management

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.a0100019.mypat.presentation.chat.ChatScreen
import com.a0100019.mypat.presentation.community.CommunityScreen
import com.a0100019.mypat.presentation.daily.DailyScreen
import com.a0100019.mypat.presentation.daily.diary.DiaryScreen
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
import com.a0100019.mypat.presentation.store.StoreScreen
import com.a0100019.mypat.presentation.main.world.WorldScreen
import com.a0100019.mypat.presentation.operator.OperatorScreen
import com.a0100019.mypat.presentation.privateChat.PrivateChatInScreen
import com.a0100019.mypat.presentation.privateChat.PrivateRoomScreen

@Composable
fun MainNavHost() {
    // 엑티비티 전환은 네비호스트 사용안함!!
    val navController = rememberNavController()

    //뷰모델 공유하고 싶으면 이렇게 하기
    // val diaryViewModel: DiaryViewModel = hiltViewModel()

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
                onWorldNavigateClick = {
                    navController.navigate(route = MainRoute.WorldScreen.name)
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
                },
                onChatNavigateClick = {
                    navController.navigate(route = MainRoute.ChatScreen.name)
                },
                onOperatorNavigateClick = {
                    navController.navigate(route = MainRoute.OperatorScreen.name)
                },
                onPrivateRoomNavigateClick = {
                    navController.navigate(route = MainRoute.PrivateRoomScreen.name)
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
                popBackStack = { navController.popBackStack() }
            )
        }

        composable(route = MainRoute.WorldScreen.name) {
            WorldScreen(
                onMainNavigateClick = {
                    navController.navigate(route = MainRoute.MainScreen.name) {
                        popUpTo(0) { inclusive = true } // 백스택 전체 제거
                        launchSingleTop = true // 같은 화면 여러 번 안 쌓이게
                    }
                }
            )
        }

        composable(route = MainRoute.StoreScreen.name) {
            StoreScreen(
                popBackStack = { navController.popBackStack() }
            )
        }

        composable(route = MainRoute.IndexScreen.name) {
            IndexScreen(
                popBackStack = { navController.popBackStack() }
            )
        }

        composable(route = MainRoute.InformationScreen.name) {
            InformationScreen(
                popBackStack = { navController.popBackStack() }
            )
        }

        composable(route = MainRoute.CommunityScreen.name) {
            CommunityScreen(
                popBackStack = { navController.popBackStack() }
            )
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
                onDiaryClick = {
                    navController.navigate(route = MainRoute.DiaryWriteScreen.name)
                },
                popBackStack = { navController.popBackStack() }
            )
        }

        composable(route = MainRoute.DiaryWriteScreen.name) {
            // 같은 ViewModel 사용
            DiaryWriteScreen(
                //()가 있는 함수는 {} 안에 해야함
                popBackStack = { navController.popBackStack() }
            )
        }

        composable(route = MainRoute.EnglishScreen.name) {
            EnglishScreen(
                popBackStack = { navController.popBackStack() }
            )
        }

        composable(route = MainRoute.KoreanScreen.name) {
            KoreanScreen(
                popBackStack = { navController.popBackStack() }
            )
        }

        composable(route = MainRoute.SettingScreen.name) {
            SettingScreen(
                onSignOutClick = {
                    navController.navigate(route = MainRoute.LoginScreen.name) {
                        popUpTo(0) { inclusive = true } // 백스택 전체 제거
                        launchSingleTop = true // 같은 화면 여러 번 안 쌓이게
                    }
                },
                popBackStack = { navController.popBackStack() }
            )
        }

        composable(route = MainRoute.ChatScreen.name) {
            ChatScreen(
                popBackStack = { navController.popBackStack() }
            )
        }


        composable(route = MainRoute.WalkScreen.name) {
            WalkScreen(
                popBackStack = { navController.popBackStack() }
            )
        }


        composable(route = MainRoute.OperatorScreen.name) {
            OperatorScreen(
                popBackStack = { navController.popBackStack() }
            )
        }


        composable(route = MainRoute.PrivateRoomScreen.name) {
            PrivateRoomScreen(
                popBackStack = { navController.popBackStack() },
                onNavigateToPrivateChatInScreen = {
                    navController.navigate(route = MainRoute.PrivateChatInScreen.name)
                }
            )
        }

        composable(route = MainRoute.PrivateChatInScreen.name) {
            PrivateChatInScreen(
                popBackStack = { navController.popBackStack() }
            )
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