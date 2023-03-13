package com.sturec.sturecteacher.ui.dashboard.create_exmas

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sturec.sturecteacher.data.create_exams.ExamsRepositoryImpl
import com.sturec.sturecteacher.util.UiEvents
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreateExamsViewModel @Inject constructor(
    val examsRepositoryImpl: ExamsRepositoryImpl
):ViewModel() {

    private val _uiEvent = Channel<UiEvents>()
    val uiEvent = _uiEvent.receiveAsFlow()

    fun onEvent(event:ExamsEvent){

    }

    fun sendUiEvent(event:UiEvents) {
        viewModelScope.launch {
            _uiEvent.send(event)
        }
    }

}