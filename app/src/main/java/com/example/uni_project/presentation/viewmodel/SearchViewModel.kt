package com.example.uni_project.presentation.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.uni_project.core.data_class.Car
import com.example.uni_project.core.data_class.mockCars
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.compose.runtime.State

class SearchViewModel : ViewModel() {
    private val _searchResults = mutableStateOf(emptyList<Car>())
    val searchResults: State<List<Car>> = _searchResults

    private val _isLoading = mutableStateOf(false)
    val isLoading: State<Boolean> = _isLoading

    private val _error = mutableStateOf<String?>(null)
    val error: State<String?> = _error

    fun searchCars(query: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            try {
                // Имитация API call
                delay(1000)
                val results = mockCars.filter { car ->
                    car.brand.contains(query, ignoreCase = true) ||
                            car.model.contains(query, ignoreCase = true)
                }
                _searchResults.value = results
            } catch (e: Exception) {
                _error.value = "Не удалось выполнить поиск. Попробуйте снова."
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearError() {
        _error.value = null
    }
    fun clearResults() {
        _searchResults.value = emptyList()
        _error.value = null
    }
}