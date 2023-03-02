package com.sturec.sturecteacher.util

sealed class UiEvents {
    data class ShowSnackbar(
        val message:String
    ):UiEvents()

    data class Navigate(
        val actionId:Int
    ):UiEvents()
}