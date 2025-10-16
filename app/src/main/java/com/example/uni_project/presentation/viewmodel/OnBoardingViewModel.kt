package com.example.uni_project.presentation.viewmodel

import androidx.compose.runtime.IntState
import androidx.compose.runtime.mutableIntStateOf
import androidx.lifecycle.ViewModel
import com.example.uni_project.R
import com.example.uni_project.core.data_class.OnboardingSlide


class OnboardingViewModel : ViewModel() {
    private val _currentPage = mutableIntStateOf(0)
    val currentPage: IntState = _currentPage

    private val _slides = listOf(
        OnboardingSlide(
            imageRes = R.drawable.greet1,
            title = R.string.greet1,
            description = R.string.greet_d1
        ),
        OnboardingSlide(
            imageRes = R.drawable.greet2,
            title = R.string.greet2,
            description = R.string.greet_d2
        ),
        OnboardingSlide(
            imageRes = R.drawable.greet3,
            title = R.string.greet3,
            description = R.string.greet_d3
        )
    )

    val slides: List<OnboardingSlide> = _slides
    val isLastPage: Boolean get() = _currentPage.intValue == _slides.size - 1

    fun nextPage() {
        if (_currentPage.intValue < _slides.size - 1) {
            _currentPage.intValue++
        }
    }
    
}