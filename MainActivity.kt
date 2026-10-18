package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.local.ShopProfileManager
import com.example.di.AppContainer
import com.example.ui.screens.DashboardScreen
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val repairDao = AppContainer.getRepairDao(this)
        val shopProfileManager = ShopProfileManager(this)

        setContent {
            MyApplicationTheme {
                val viewModel: MainViewModel = viewModel(
                    factory = MainViewModel.Factory(repairDao, shopProfileManager)
                )
                DashboardScreen(viewModel = viewModel)
            }
        }
    }
}
