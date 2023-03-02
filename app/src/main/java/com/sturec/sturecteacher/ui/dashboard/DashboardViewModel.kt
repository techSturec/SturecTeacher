package com.sturec.sturecteacher.ui.dashboard

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sturec.sturecteacher.R
import com.sturec.sturecteacher.util.UiEvents
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class DashboardViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is dashboard Fragment"
    }
    val text: LiveData<String> = _text

    private val _uiEvent = Channel<UiEvents>()
    val uiEvent = _uiEvent.receiveAsFlow()

    fun onEvent(event: DashboardEvent)
    {
        when(event)
        {
            is DashboardEvent.OnClassroomButtonClicked->{
                viewModelScope.launch {
                    _uiEvent.send(UiEvents.Navigate(R.id.action_navigation_dashboard_to_classroomFragment))
                }
            }
            is DashboardEvent.OnSubjectsButtonClicked->{
                viewModelScope.launch {
                    _uiEvent.send(UiEvents.ShowSnackbar("Subjects"))
                }
            }
            is DashboardEvent.OnAttendanceButtonClicked->{
                viewModelScope.launch {
                    _uiEvent.send(UiEvents.ShowSnackbar("Attendance"))
                }
            }
            is DashboardEvent.OnBulletinButtonClicked->{
                viewModelScope.launch {
                    _uiEvent.send(UiEvents.ShowSnackbar("Bulletin"))
                }
            }
        }
    }
}