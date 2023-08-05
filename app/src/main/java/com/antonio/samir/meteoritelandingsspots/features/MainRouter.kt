package com.antonio.samir.meteoritelandingsspots.features

sealed class Screen(val route: String) {
    object meteoriteList : Screen("friendslist")
    object meteoriteDetail : Screen("profile")
}