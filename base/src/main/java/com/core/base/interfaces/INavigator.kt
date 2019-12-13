package com.core.base.interfaces

import android.view.View
import androidx.navigation.NavController
import com.google.android.material.bottomnavigation.BottomNavigationView

interface INavigator {

    fun attachBottomNavigation(navController: NavController, navView: BottomNavigationView)
    fun hideNavView()
    fun showNavView()
}