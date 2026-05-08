package com.bill.bizpilot.navigation

import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.compose.viewModel
import com.bill.bizpilot.ui.theme.BizpilotTheme
import com.bill.bizpilot.ui.theme.viewmodels.MainViewModel
import com.bill.bizpilot.ui.theme.screens.AuthScreen
import com.bill.bizpilot.ui.theme.screens.MainDashboard

@Composable
fun App(viewModel: MainViewModel = viewModel()) {
    BizpilotTheme {
        val user = viewModel.currentUser
        val isLoading = viewModel.isLoading

        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Color.Cyan)
            }
        } else {
            if (user == null) {
                AuthScreen(viewModel)
            } else {
                MainDashboard(viewModel)
            }
        }
    }
}
