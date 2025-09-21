package com.example.uni_project.screens.components

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*
import com.example.uni_project.core.viewmodel.OnboardingSlide

@Composable
fun OnboardingSlideItem(
    slide: OnboardingSlide,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(slide.imageRes),
            contentDescription = null,
            modifier = Modifier
                .size(240.dp)
                .padding(bottom = 32.dp),
            contentScale = ContentScale.Fit
        )


        Text(
            text = stringResource(slide.title),
            color = MaterialTheme.colorScheme.onBackground,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 16.dp)
        )


        Text(
            text = stringResource(slide.description),
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
            fontSize = 16.sp,
            textAlign = TextAlign.Center,
            lineHeight = 20.sp
        )
    }
}