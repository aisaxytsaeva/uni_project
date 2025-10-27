package com.example.uni_project.presentation.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.uni_project.core.data_class.Car
import com.example.uni_project.core.data_class.mockCars
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.compose.runtime.State

class HomeViewModel : ViewModel() {
    private val _cars = mutableStateOf(emptyList<Car>())
    val cars: State<List<Car>> = _cars

    private val _isLoading = mutableStateOf(false)
    val isLoading: State<Boolean> = _isLoading

    fun loadCars() {
        viewModelScope.launch {
            _isLoading.value = true
            // Загрузка данных
            delay(1000)
            _cars.value = mockCars
            _isLoading.value = false
        }
    }
}