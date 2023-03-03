package com.sturec.sturecteacher.ui.dashboard.classroom

import android.util.Log
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sturec.sturecteacher.data.classroom_operations.ClassroomData
import com.sturec.sturecteacher.data.classroom_operations.ClassroomOperationsRepositoryImpl
import com.sturec.sturecteacher.data.classroom_operations.TeacherAssignedClassroomData
import com.sturec.sturecteacher.util.UiEvents
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import java.util.concurrent.Flow
import javax.inject.Inject

@HiltViewModel
class ClassroomViewModel @Inject constructor(
    val classroomOperationsRepository: ClassroomOperationsRepositoryImpl
): ViewModel() {

    val classroomList = classroomOperationsRepository.getAssignedClassroomsList()

    private val _uiEvent = Channel<UiEvents> {  }
    val uiEvent = _uiEvent.receiveAsFlow()

    fun onEvent(event:ClassroomEvents){
        when(event)
        {
            is ClassroomEvents.OnSaveStudentButtonClicked->{
                viewModelScope.launch {
                    classroomOperationsRepository.addStudent(event.data).collect{
                        if(it)
                        {
                            sendUIEvent(UiEvents.ShowSnackbar("Student Added Successfully"))
                        }else
                        {
                            sendUIEvent(UiEvents.ShowSnackbar("Failed to add Student, Something Went Wrong"))
                        }
                    }
                }
            }
        }
    }

    fun sendUIEvent(event:UiEvents)
    {
        viewModelScope.launch {
            _uiEvent.send(event)
        }
    }
}