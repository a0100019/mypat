package com.a0100019.mypat.presentation.welcome


import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController


@Composable
fun WelcomeNavHost() {

    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = WelcomeRoute.WelcomeScreen.name,
    ){
        composable(route = WelcomeRoute.WelcomeScreen.name) {
            WelcomeScreen(
                onNavigateToSideEffectNavScreen = {
                    navController.navigate(route = WelcomeRoute.SideEffectNavScreen.name)
                }
            )
        }

        composable(route = WelcomeRoute.SideEffectNavScreen.name) {
            SideEffectNavScreen(
                onNavigateToSelectScreen = {
                    navController.navigate(route = WelcomeRoute.SelectScreen.name)
                }
            )
        }

        //이동의 대상이 되는 스크린도 작성
        composable(route = WelcomeRoute.SelectScreen.name) {
            MainScreen()
        }
//
//        composable(route = LoginRoute.SignUpScreen.name) {
//
//            SignUpScreen(
//                onNavigationToLoginScreen = {
//                    navController.navigate(
//                        route = LoginRoute.LoginScreen.name,
//                        navOptions = navOptions {
//                            popUpTo(LoginRoute.WelcomeScreen.name)
//                        }
//                    )
//                }
//            )
//
//        }
    }
}