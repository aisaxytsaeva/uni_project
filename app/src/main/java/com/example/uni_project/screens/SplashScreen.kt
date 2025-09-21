package com.example.uni_project.screens


import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.*
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*
import com.example.uni_project.R


@Composable
fun SplashScreen() {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.primary
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.primary),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Start as Arrangement.Vertical
        ) {
            Text(
                text = stringResource(R.string.app_name),
                color = MaterialTheme.colorScheme.onPrimary,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Start,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Text(
                text = stringResource(R.string.splashscreen_text),
                color = MaterialTheme.colorScheme.onPrimary,
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 64.dp)
            )
            Image(
                painter = painterResource(R.drawable.splashscreen_im),
                contentDescription = "DriveNextLogo",
                modifier = Modifier.size(240.dp)
            )

        }
    }
}

