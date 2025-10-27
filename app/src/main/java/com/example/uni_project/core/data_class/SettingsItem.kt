package com.example.uni_project.core.data_class

sealed class SettingsItem {
    data class Header(val title: String) : SettingsItem()
    data class MenuItem(
        val id: Int,
        val title: Int,
        val icon: Int,
        val showArrow: Boolean = true,
        val showSwitch: Boolean = false,
        val switchState: Boolean = false
    ) : SettingsItem()
}