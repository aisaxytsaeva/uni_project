package com.example.uni_project.presentation.screens

import androidx.compose.foundation.layout.PaddingValues

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.uni_project.R
import com.example.uni_project.core.data_class.SettingsItem
import com.example.uni_project.core.data_class.UserProfileState
import com.example.uni_project.presentation.components.SettingsMenuItem
import com.example.uni_project.presentation.components.UserProfileSection
import com.example.uni_project.presentation.viewmodel.SettingsViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = viewModel(),
    onProfileClick: () -> Unit = {},
    onBookingsClick: () -> Unit = {},
    onThemeClick: () -> Unit = {},
    onNotificationsClick: () -> Unit = {},
    onAddCarClick: () -> Unit = {},
    onHelpClick: () -> Unit = {},
    onReferralClick: () -> Unit = {}
) {
    val userProfileState by viewModel.userProfile.collectAsState()
    val settingsItems = remember {
        listOf(

            SettingsItem.MenuItem(
                id = 1,
                title = R.string.bookings,
                icon = R.drawable.taxi
            ),
            SettingsItem.MenuItem(
                id = 2,
                title = R.string.theme,
                icon = R.drawable.theme
            ),
            SettingsItem.MenuItem(
                id = 3,
                title = R.string.notifications,
                icon = R.drawable.notif
            ),
            SettingsItem.MenuItem(
                id = 4,
                title = R.string.your_auto,
                icon = R.drawable.banknotes
            ),
            SettingsItem.MenuItem(
                id = 5,
                title = R.string.help,
                icon = R.drawable.question
            ),
            SettingsItem.MenuItem(
                id = 6,
                title = R.string.friend,
                icon = R.drawable.letter
            )
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.settings),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()

                    )
                },


            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {

            item {
                val profile = (userProfileState as UserProfileState.Success).userProfile
                UserProfileSection(
                    userName = profile.userName,
                    userEmail = profile.userEmail,
                    avatarUrl = profile.avatarUrl,
                    onProfileClick = onProfileClick
                )
                Spacer(modifier = Modifier.height(24.dp))
            }


            items(settingsItems) { item ->
                SettingsMenuItem(
                    item = item,
                    onClick = {
                        when (item.id) {
                            1 -> onBookingsClick()
                            2 -> onThemeClick()
                            3 -> onNotificationsClick()
                            4 -> onAddCarClick()
                            5 -> onHelpClick()
                            6 -> onReferralClick()
                        }
                    }
                )
            }

        }
    }
}

//@Preview(showBackground = true)
//@Composable
//fun Setting(){
//    SettingsScreen(
//        onProfileClick = {},
//        onHelpClick = {},
//        onThemeClick = {},
//        onBookingsClick = {},
//        onReferralClick = {},
//        onNotificationsClick = {},
//        onAddCarClick = {},
//    )
//}