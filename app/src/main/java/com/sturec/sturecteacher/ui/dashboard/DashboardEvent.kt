package com.sturec.sturecteacher.ui.dashboard

sealed class DashboardEvent {
    object OnClassroomButtonClicked:DashboardEvent()
    object OnSubjectsButtonClicked: DashboardEvent()
    object OnAttendanceButtonClicked: DashboardEvent()
    object OnBulletinButtonClicked: DashboardEvent()
}