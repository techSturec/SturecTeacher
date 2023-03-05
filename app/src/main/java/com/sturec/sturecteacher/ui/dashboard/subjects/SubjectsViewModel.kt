package com.sturec.sturecteacher.ui.dashboard.subjects

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sturec.sturecteacher.data.classroom_operations.ClassroomOperationsRepositoryImpl
import com.sturec.sturecteacher.data.subject_operations.SubjectOperationsRepositoryImpl
import com.sturec.sturecteacher.util.UiEvents
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SubjectsViewModel @Inject constructor(
    val subjectOperationsRepositoryImpl: SubjectOperationsRepositoryImpl,
    val classroomOperationsRepositoryImpl: ClassroomOperationsRepositoryImpl
): ViewModel() {

    private val _uiEvent = Channel<UiEvents>()
    val uiEvents = _uiEvent.receiveAsFlow()

    fun onEvent(event: SubjectsEvent){
        when(event){
            is SubjectsEvent.OnSaveSubjectButtonClicked->{
                viewModelScope.launch {
                    subjectOperationsRepositoryImpl.addSubjectsForClassroom(
                        event.classroomData,
                        event.data
                    ).collect{
                        if(!it) {
                            sendUiEvent(UiEvents.ShowSnackbar("Some Error Occurred"))
                        }
                    }
                }
            }
        }
    }

    private fun sendUiEvent(event: UiEvents){
        viewModelScope.launch {
            _uiEvent.send(event)
        }
    }
}