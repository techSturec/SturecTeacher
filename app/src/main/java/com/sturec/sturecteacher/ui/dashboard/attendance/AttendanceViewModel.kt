package com.sturec.sturecteacher.ui.dashboard.attendance

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sturec.sturecteacher.R
import com.sturec.sturecteacher.data.attendance.AttendanceRepositoryImpl
import com.sturec.sturecteacher.data.classroom_operations.TeacherAssignedClassroomData
import com.sturec.sturecteacher.util.UiEvents
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AttendanceViewModel @Inject constructor(
    val attendanceRepositoryImpl: AttendanceRepositoryImpl
):ViewModel() {
    private val _uiEvent = Channel<UiEvents>()
    val uiEvents = _uiEvent.receiveAsFlow()

    fun onEvent(event:AttendanceEvents){
        when(event){
            is AttendanceEvents.Navigate->{
                sendUiEvent(UiEvents.Navigate(R.id.action_attendanceFragment_to_navigation_dashboard))
            }
            is AttendanceEvents.Snackbar->{
                sendUiEvent(UiEvents.ShowSnackbar(event.message))
            }
        }
    }

    private fun sendUiEvent(event: UiEvents){
        viewModelScope.launch {
            _uiEvent.send(event)
        }
    }
}