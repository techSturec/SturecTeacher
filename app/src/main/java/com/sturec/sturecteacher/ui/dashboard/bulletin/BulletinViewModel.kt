package com.sturec.sturecteacher.ui.dashboard.bulletin

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sturec.sturecteacher.data.bulletin.BulletinRepositoryImpl
import com.sturec.sturecteacher.util.UiEvents
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BulletinViewModel @Inject constructor(
    val bulletinRepositoryImpl: BulletinRepositoryImpl
):ViewModel() {

    private val _uiEvent = Channel<UiEvents>()
    val uiEvent = _uiEvent.receiveAsFlow()

    fun onEvent(event:BulletinEvent){
        when(event){
            is BulletinEvent.OnButtonClicked->{
                viewModelScope.launch {
                    bulletinRepositoryImpl.getAllAnnouncements().collect{
                        Log.e("Data", it.toString())
                    }
                }

            }
        }
    }

    fun sendUiEvent(event:UiEvents) {
        viewModelScope.launch {
            _uiEvent.send(event)
        }
    }
}