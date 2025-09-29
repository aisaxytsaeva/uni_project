package com.example.uni_project.core.data_class

import com.example.uni_project.dao.User

data class LoginResult(
    val success: Boolean,
    val token: String? = null,
    val error: String? = null,
    val user: User? = null,
    val message: String? = null
)