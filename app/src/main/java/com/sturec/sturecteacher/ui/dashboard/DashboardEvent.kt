package com.sturec.sturecteacher.ui.dashboard

sealed class DashboardEvent {
    data class ButtonClicked(val title:String) : DashboardEvent()
}