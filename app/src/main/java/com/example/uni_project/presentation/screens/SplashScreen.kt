package com.example.uni_project.presentation.screens


import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.*
import com.example.uni_project.R
import com.example.uni_project.ui.theme.Purple
import kotlinx.coroutines.delay


@Composable
fun SplashScreen(
    onSplashComplete: () -> Unit
) {
    LaunchedEffect(Unit) {
        delay(3000L)
        onSplashComplete()
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Top
        ) {
            Text(
                text = stringResource(R.string.app_name),
                color = Purple,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Start,
                modifier = Modifier.padding(top = 75.dp, start = 16.dp)
            )
            Text(
                text = stringResource(R.string.splashscreen_text),
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = 14.sp,
                textAlign = TextAlign.Start,
                modifier = Modifier.padding(bottom = 64.dp, start = 16.dp)
            )
            Image(
                painter = painterResource(R.drawable.splashscreen_im),
                contentDescription = "DriveNextLogo",
                modifier = Modifier.size(500.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun Spl(){
    SplashScreen (
        onSplashComplete = {}
    )
}


