package com.example.uni_project.presentation.screens


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.uni_project.R
import com.example.uni_project.presentation.viewmodel.OnboardingViewModel
import com.example.uni_project.presentation.screens.components.OnboardingSlideItem

@Composable
fun Greetings(
    onFinish: () -> Unit,
    onSkip: () -> Unit
){
    val viewModel: OnboardingViewModel = viewModel()
    val currentPage by viewModel.currentPage

    Box(modifier = Modifier.fillMaxSize()){
        if (!viewModel.isLastPage) {
            TextButton(
                onClick = onSkip,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(16.dp)
            ) {
                Text(
                    text = stringResource(R.string.skip),
                    color = MaterialTheme.colorScheme.onBackground,
                    fontSize = 16.sp
                )
            }
        }
        OnboardingSlideItem(slide = viewModel.slides[currentPage])

        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 80.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            repeat(viewModel.slides.size) { index ->
                Box(
                    modifier = Modifier
                        .padding(4.dp)
                        .background(
                            color = if (index == currentPage) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                MaterialTheme.colorScheme.onBackground.copy(alpha = 0.3f)
                            },
                            shape = CircleShape
                        )
                )
            }
        }

        // Кнопка действия
        Button(
            onClick = {
                if (viewModel.isLastPage) {
                    onFinish()
                } else {
                    viewModel.nextPage()
                }
            },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 24.dp)
                .fillMaxWidth(0.8f)

        ) {
            Text(
                text = if (viewModel.isLastPage) stringResource(R.string.go) else stringResource(R.string.cont),
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}
